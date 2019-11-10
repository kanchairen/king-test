package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.*;
import com.lky.dao.WithdrawRecordDao;
import com.lky.dto.WithdrawRecordDto;
import com.lky.entity.*;
import com.lky.enums.code.AssetResCode;
import com.lky.enums.dict.WithdrawRecordDict;
import com.lky.mapper.WithdrawRecordMapper;
import com.lky.pay.etone.EToneUtils;
import com.lky.pay.etone.sdk.EToneConfig;
import com.lky.pay.etone.sdk.xml.entry.y2e.Y2e0010Res;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsxExporterBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static com.lky.enums.dict.BalanceRecordDict.TYPE_WITHDRAW;
import static com.lky.enums.dict.BalanceRecordDict.TYPE_WITHDRAW_FAILURE;
import static com.lky.enums.dict.WithdrawRecordDict.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 * 提现记录
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-15
 */
@Service
public class WithdrawRecordService extends BaseService<WithdrawRecord, Integer> {

    @Inject
    private WithdrawRecordDao withdrawRecordDao;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private UserService userService;

    @Inject
    private EToneService eToneService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private WithdrawRecordMapper withdrawRecordMapper;

    private static final Logger log = LoggerFactory.getLogger(WithdrawRecordService.class);

    @Override
    public BaseDao<WithdrawRecord, Integer> getBaseDao() {
        return this.withdrawRecordDao;
    }

    /**
     * 用户申请提现
     *
     * @param user     用户
     * @param bankCard 用户选择提现到的银行卡
     * @param amount   用户提现金额
     */
    public void applyWithdraw(User user, BankCard bankCard, Double amount) {
        //获取用户财富资产
        UserAsset userAsset = user.getUserAsset();

        //计算提现手续费
        HighConfig highConfig = baseConfigService.findH();
        //提现手续费直接在后台设置每笔多少钱，不按费率计算
        double withdrawFee = highConfig.getBalanceWithdrawFee();
        double realAmount = amount - withdrawFee;

        //提现备注
        String remark = "用户大米提现到" + bankCard.getRealName() + "的银行卡";

        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setUser(user);
        //设置代付批次号，0开头的批次号代表用户、商家提现，1开头的批次号代表代理商提现
        withdrawRecord.setBatchNo("0" + StringUtils.getNumberUUID(7));
        withdrawRecord.setCurrentBalance(userAsset.getBalance() - amount);
        withdrawRecord.setWithdrawAmount(amount);
        withdrawRecord.setWithdrawFee(withdrawFee);
        withdrawRecord.setRealAmount(realAmount);
        withdrawRecord.setBankCard(bankCard);
        withdrawRecord.setState(WithdrawRecordDict.STATE_APPLY.getKey());
        withdrawRecord.setRemark(remark);
        super.save(withdrawRecord);

        //添加大米变动记录
        balanceRecordService.create(user, -amount, TYPE_WITHDRAW.getKey(), remark);

        //更新用户大米
        userAsset.setBalance(userAsset.getBalance() - amount);
        user.setUpdateTime(new Date());
        userService.update(user);
    }

    /**
     * 后台管理员同意用户提现申请，向易通发起代付请求
     *
     * @param withdrawRecord 提现记录
     */
    @SuppressWarnings("unchecked")
    public void agreeWithdraw(WithdrawRecord withdrawRecord) {
        //向用户打钱的金额，转换成分
        int amount = 50;
        //生产环境时，提现金额为真实金额
        if (environmentService.executeEnv()) {
            amount = (int) ArithUtils.mul(String.valueOf(withdrawRecord.getRealAmount()), "100");
        }
        BankCard bank = withdrawRecord.getBankCard();

        //处理遗留的手动提现没有批次号问题
        if (StringUtils.isEmpty(withdrawRecord.getBatchNo())) {
            withdrawRecord.setBatchNo("0" + StringUtils.getNumberUUID(7));
        }

        //发起易通代付请求，请求受理则更新提现记录
        Y2e0010Res result = EToneUtils.daiFu(withdrawRecord.getBatchNo(), amount, environmentService.serverUrl(), bank.getBankcard(),
                bank.getRealName(), StringUtils.isEmpty(bank.getBranchName()) ? bank.getBankName() : bank.getBranchName());
        if (EToneConfig.SUCCESS_CODE.equals(result.getRspcod())) {
            withdrawRecord.setState(STATE_AGREE.getKey());
            withdrawRecord.setUpdateTime(new Date());
            super.update(withdrawRecord);

            eToneService.addRedis(withdrawRecord.getId(), withdrawRecord.getBatchNo());
            return;
        } else if (EToneConfig.BALANCE_NOT_ENOUGH.equals(result.getRspcod())) {
            ExceptionUtils.throwResponseException(AssetResCode.ETONE_BALANCE_NOT_ENOUGH);
        }
        ExceptionUtils.throwResponseException(AssetResCode.ETONE_NOT_ANSWER);
    }

    /**
     * 后台管理员拒绝用户提现申请，则提现失败
     *
     * @param record 提现记录
     */
    public void refuseWithdraw(WithdrawRecord record) {
        Date date = new Date();
        //提现失败，个人提现失败，退还大米，添加大米变动记录
        User user = record.getUser();
        balanceRecordService.create(user, record.getWithdrawAmount(),
                TYPE_WITHDRAW_FAILURE.getKey(), TYPE_WITHDRAW_FAILURE.getValue());

        //更新提现记录
        record.setState(STATE_REFUSE.getKey());
        record.setFinishTime(date);
        super.update(record);

        //更新用户大米
        UserAsset userAsset = user.getUserAsset();
        userAsset.setBalance(userAsset.getBalance() + record.getWithdrawAmount());
        user.setUpdateTime(date);
        userService.update(user);
    }

    /**
     * 代付请求查询有返回结果，则处理提现记录：
     * 如果返回代付失败，则退还用户大米；如果代付成功，则更新提现记录
     *
     * @param id          个人提现记录id
     * @param queryResult 代付查询结果
     * @param date        当前时间
     */
    public void handleWithdraw(Integer id, Boolean queryResult, Date date) {
        WithdrawRecord record = findById(id);
        if (record != null && STATE_AGREE.getKey().equals(record.getState())) {
            if (queryResult) {
                //代付成功，更新个人提现记录
                record.setState(STATE_FINISH.getKey());
                record.setFinishTime(date);
                super.update(record);
            } else {
                //代付失败，个人提现失败，退还小米
                User user = record.getUser();
                //添加小米变动记录
                balanceRecordService.create(user, record.getWithdrawAmount(),
                        TYPE_WITHDRAW_FAILURE.getKey(), TYPE_WITHDRAW_FAILURE.getValue());

                //更新提现记录
                record.setState(STATE_FAILURE.getKey());
                record.setFinishTime(date);
                super.update(record);

                //更新用户小米
                UserAsset userAsset = user.getUserAsset();
                userAsset.setBalance(userAsset.getBalance() + record.getWithdrawAmount());
                user.setUpdateTime(date);
                userService.update(user);
            }
        }
    }

    /**
     * 统计今日用户提现申请次数
     *
     * @param user 用户
     * @return 申请提现次数
     */
    public long count(User user) {
        Specification<WithdrawRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (user != null) {
                predicates.add(cb.equal(root.get("user"), user));
            }

            predicates.add(cb.between(root.get("applyTime"),
                    DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR),
                    DateUtils.getEndDate(new Date(), Calendar.DAY_OF_YEAR)));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return count(spec);
    }

    public WithdrawRecord findById(Integer id) {
        return super.findById(id);
    }

    public WithdrawRecord findByBatchNo(String batchNo) {
        return withdrawRecordDao.findByBatchNo(batchNo);
    }

    public Page<WithdrawRecordDto> findByState(User user, String state,
                                               Long begin, Long end,
                                               Boolean isUser, Pageable pageable) {
        Specification<WithdrawRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (user != null) {
                predicates.add(cb.equal(root.get("user"), user));
            }

            if (StringUtils.isNotEmpty(state)) {
                Predicate p1 = cb.equal(root.get("state"), state);

                //用户查询时，状态只有三种，apply和agree合并为一种状态
                if (isUser && WithdrawRecordDict.STATE_APPLY.getKey().equals(state)) {
                    Predicate p2 = cb.equal(root.get("state"), STATE_AGREE.getKey());
                    predicates.add(cb.or(p1, p2));
                } else {
                    predicates.add(p1);
                }
            }

            if (begin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyTime"), new Date(begin)));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyTime"), new Date(end)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<WithdrawRecord> page = findAll(spec, pageable);
        List<WithdrawRecordDto> dtoList = withdrawRecordMapper.toDtoList(page.getContent());
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    /**
     * 构建提现记录报表
     *
     * @param state 提现状态
     * @param begin 起始时间
     * @param end   结束时间
     * @return 报表文件名
     */
    public String buildWithdrawReport(String state, Long begin, Long end) {
        Specification<WithdrawRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(state)) {
                predicates.add(cb.equal(root.get("state"), state));
            }

            if (begin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyTime"), new Date(begin)));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyTime"), new Date(end)));
            }

            cb.and(predicates.toArray(new Predicate[predicates.size()]));
            query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            query.orderBy(cb.desc(root.get("applyTime")));
            return query.getRestriction();
        };
        List<WithdrawRecord> withdrawRecordList = super.findAll(spec);
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(withdrawRecordList)) {
            withdrawRecordList.forEach(w -> {
                Map<String, Object> map = new HashMap<>();
                map.put("applyName", w.getBankCard().getRealName());
                map.put("cellphone", w.getBankCard().getMobile());
                map.put("applyTime", DateUtils.toString(w.getApplyTime(), "yyyy-MM-dd HH:mm:ss"));
                map.put("bankId", w.getBankCard().getBankcard());
                map.put("realName", w.getBankCard().getRealName());
                map.put("id", w.getBankCard().getCardNo());
                map.put("mobile", w.getBankCard().getMobile());
                map.put("bankName", w.getBankCard().getBankName());
                map.put("bankArea", w.getBankCard().getBankArea());
                map.put("branchName", w.getBankCard().getBranchName());
                map.put("state", WithdrawRecordDict.getValue(w.getState()));
                map.put("applyAmount", BigDecimal.valueOf(w.getWithdrawAmount()));
                map.put("fee", BigDecimal.valueOf(w.getWithdrawFee()));
                map.put("realAmount", BigDecimal.valueOf(w.getRealAmount()));
                mapList.add(map);
            });
        }

        String reportFileName = getReportFileName();
        createExcel(reportFileName, mapList,
                col.column("申请人姓名", "applyName", type.stringType()),
                col.column("申请人手机号", "cellphone", type.stringType()),
                col.column("申请时间", "applyTime", type.stringType()),
                col.column("银行卡号", "bankId", type.stringType()),
                col.column("持卡人姓名", "realName", type.stringType()),
                col.column("持卡人身份证号", "id", type.stringType()),
                col.column("持卡人手机号", "mobile", type.stringType()),
                col.column("银行名称", "bankName", type.stringType()),
                col.column("开户地区", "bankArea", type.stringType()),
                col.column("开户支行", "branchName", type.stringType()),
                col.column("提现状态", "state", type.stringType()),
                col.column("申请提现金额(元)", "applyAmount", type.bigDecimalType()),
                col.column("提现手续费(元)", "fee", type.bigDecimalType()),
                col.column("实际提现金额(元)", "realAmount", type.bigDecimalType()));
        return reportFileName;
    }

    private String getReportFileName() {
        File file = new File("reports/");
        if (!file.exists() && !file.mkdirs()) {
            log.error("mkdir 失败");
        }
        return "reports/个人提现" + System.currentTimeMillis() + ".xlsx";
    }

    private void createExcel(String fileName, Collection collection, ColumnBuilder... columns) {
        try {
            DynamicReports.report()
                    .ignorePageWidth()
                    .ignorePagination()
                    .columns(columns)
                    .setDataSource(collection)
                    .toXlsx(getJasperXlsxExporterBuilder(fileName));
        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    private JasperXlsxExporterBuilder getJasperXlsxExporterBuilder(String fileName) {
        return DynamicReports.export
                .xlsxExporter(fileName)
                .setDetectCellType(true)
                .setIgnorePageMargins(true)
                .setWhitePageBackground(false)
                .setRemoveEmptySpaceBetweenRows(true);
    }
}
