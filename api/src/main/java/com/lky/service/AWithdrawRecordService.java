package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.*;
import com.lky.dao.AWithdrawRecordDao;
import com.lky.dto.WithdrawRecordDto;
import com.lky.entity.*;
import com.lky.enums.code.AssetResCode;
import com.lky.enums.dict.WithdrawRecordDict;
import com.lky.mapper.AWithdrawRecordMapper;
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

import static com.lky.enums.dict.ABalanceRecordDict.TYPE_WITHDRAW;
import static com.lky.enums.dict.ABalanceRecordDict.TYPE_WITHDRAW_BACK;
import static com.lky.enums.dict.WithdrawRecordDict.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

/**
 * 代理商提现记录Service
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-22
 */
@Service
public class AWithdrawRecordService extends BaseService<AWithdrawRecord, Integer> {

    @Inject
    private AWithdrawRecordDao aWithdrawRecordDao;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private AUserService aUserService;

    @Inject
    private ABalanceRecordService aBalanceRecordService;

    @Inject
    private AWithdrawRecordMapper aWithdrawRecordMapper;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private EToneService eToneService;

    private static final Logger log = LoggerFactory.getLogger(AWithdrawRecordService.class);

    @Override
    public BaseDao<AWithdrawRecord, Integer> getBaseDao() {
        return this.aWithdrawRecordDao;
    }

    /**
     * 代理商申请提现
     *
     * @param aUser     代理商
     * @param aBankCard 代理商选择提现到的银行卡
     * @param amount   代理商提现金额
     */
    public void applyWithdraw(AUser aUser, ABankCard aBankCard, Double amount) {
        //获取用户财富资产
        AUserAsset aUserAsset = aUser.getAUserAsset();

        //计算提现手续费
        HighConfig highConfig = baseConfigService.findH();
        //提现手续费直接在后台设置每笔多少钱，不按费率计算
        double withdrawFee = highConfig.getBalanceWithdrawFee();
        double realAmount = amount - withdrawFee;

        //提现备注
        String remark = "代理商大米提现到" + aBankCard.getRealName() + "的银行卡";

        AWithdrawRecord record = new AWithdrawRecord();
        record.setAUser(aUser);
        //设置代付批次号，0开头的批次号代表用户、商家提现，1开头的批次号代表代理商提现
        record.setBatchNo("1" + StringUtils.getNumberUUID(7));
        record.setCurrentBalance(aUserAsset.getBalance() - amount);
        record.setWithdrawAmount(amount);
        record.setWithdrawFee(withdrawFee);
        record.setRealAmount(realAmount);
        record.setAbankCard(aBankCard);
        record.setState(WithdrawRecordDict.STATE_APPLY.getKey());
        record.setRemark(remark);
        super.save(record);

        //添加大米变动记录
        aBalanceRecordService.create(aUser, -amount, TYPE_WITHDRAW.getKey(), remark);

        //更新用户大米、已提现大米
        aUserAsset.setBalance(aUserAsset.getBalance() - amount);
        aUserAsset.setWithdrawBalance(aUserAsset.getWithdrawBalance() + amount);
        aUserService.update(aUser);
    }

    /**
     * 后台管理员同意代理商提现申请，向易通发起代付请求
     *
     * @param aWithdrawRecord 代理商提现记录
     */
    @SuppressWarnings("unchecked")
    public void agreeWithdraw(AWithdrawRecord aWithdrawRecord) {
        //向用户打钱的金额，转换成分
        int amount = 10;
        if (environmentService.executeEnv()) {
            amount = (int) ArithUtils.mul(String.valueOf(aWithdrawRecord.getRealAmount()), "100");
        }
        ABankCard bank = aWithdrawRecord.getAbankCard();

        //发起易通代付请求，请求受理则更新提现记录
        Y2e0010Res result = EToneUtils.daiFu(aWithdrawRecord.getBatchNo(),
                amount, environmentService.serverUrl(), bank.getBankcard(),
                bank.getRealName(), bank.getBranchName());
        if (EToneConfig.SUCCESS_CODE.equals(result.getRspcod())) {
            aWithdrawRecord.setState(STATE_AGREE.getKey());
            aWithdrawRecord.setUpdateTime(new Date());
            super.update(aWithdrawRecord);

            eToneService.addRedis(aWithdrawRecord.getId(), aWithdrawRecord.getBatchNo());
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
    public void refuseWithdraw(AWithdrawRecord record) {
        Date date = new Date();
        //代付失败，代理商提现失败，退还大米，添加大米变动记录
        AUser aUser = record.getAUser();
        aBalanceRecordService.create(aUser, record.getWithdrawAmount(),
                TYPE_WITHDRAW_BACK.getKey(), TYPE_WITHDRAW_BACK.getValue());

        //更新代理商提现记录
        record.setState(STATE_REFUSE.getKey());
        record.setFinishTime(date);
        super.update(record);

        //更新代理商大米、已提现大米
        AUserAsset aUserAsset = aUser.getAUserAsset();
        aUserAsset.setBalance(aUserAsset.getBalance() + record.getWithdrawAmount());
        aUserAsset.setWithdrawBalance(aUserAsset.getWithdrawBalance() - record.getWithdrawAmount());
        aUserService.update(aUser);
    }

    /**
     * 代付请求查询有返回结果，则处理提现记录：
     * 如果返回代付失败，则退还用户大米；如果代付成功，则更新提现记录
     *
     * @param id          代理商提现记录id
     * @param queryResult 代付查询结果
     * @param date        当前时间
     */
    public void handleWithdraw(Integer id, Boolean queryResult, Date date) {
        AWithdrawRecord record = findById(id);
        if (record != null && STATE_AGREE.getKey().equals(record.getState())) {

            if (queryResult) {
                //代付成功，更新代理商提现记录
                record.setState(STATE_FINISH.getKey());
                record.setFinishTime(date);
                super.update(record);
            } else {
                //代付失败，代理商提现失败，退还大米
                AUser aUser = record.getAUser();
                //添加大米变动记录
                aBalanceRecordService.create(aUser, record.getWithdrawAmount(),
                        TYPE_WITHDRAW_BACK.getKey(), TYPE_WITHDRAW_BACK.getValue());

                //更新代理商提现记录
                record.setState(STATE_FAILURE.getKey());
                record.setFinishTime(date);
                super.update(record);

                //更新代理商大米、已提现大米
                AUserAsset aUserAsset = aUser.getAUserAsset();
                aUserAsset.setBalance(aUserAsset.getBalance() + record.getWithdrawAmount());
                aUserAsset.setWithdrawBalance(aUserAsset.getWithdrawBalance() - record.getWithdrawAmount());
                aUserService.update(aUser);
            }
        }
    }

    public AWithdrawRecord findById(Integer id) {
        return super.findById(id);
    }

    public AWithdrawRecord findByBatchNo(String batchNo) {
        return aWithdrawRecordDao.findByBatchNo(batchNo);
    }

    public Page<WithdrawRecordDto> findByState(AUser aUser, String state, Long begin,
                                               Long end, Pageable pageable) {
        Specification<AWithdrawRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (aUser != null) {
                predicates.add(cb.equal(root.get("aUser"), aUser));
            }

            if (StringUtils.isNotEmpty(state)) {
                predicates.add(cb.equal(root.get("state"), state));
            }

            if (begin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyTime"), new Date(begin)));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyTime"), new Date(end)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<AWithdrawRecord> page = findAll(spec, pageable);
        List<WithdrawRecordDto> dtoList = aWithdrawRecordMapper.toDtoList(page.getContent());
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
        Specification<AWithdrawRecord> spec = (root, query, cb) -> {
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
        List<AWithdrawRecord> aWithdrawRecordList = super.findAll(spec);
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(aWithdrawRecordList)) {
            aWithdrawRecordList.forEach(a -> {
                Map<String, Object> map = new HashMap<>();
                map.put("applyName", a.getAbankCard().getRealName());
                map.put("cellphone", a.getAbankCard().getMobile());
                map.put("applyTime", DateUtils.toString(a.getApplyTime(), "yyyy-MM-dd HH:mm:ss"));
                map.put("bankId", a.getAbankCard().getBankcard());
                map.put("realName", a.getAbankCard().getRealName());
                map.put("id", a.getAbankCard().getCardNo());
                map.put("mobile", a.getAbankCard().getMobile());
                map.put("bankName", a.getAbankCard().getBankName());
                map.put("bankArea", a.getAbankCard().getBankArea());
                map.put("branchName", a.getAbankCard().getBranchName());
                map.put("state", WithdrawRecordDict.getValue(a.getState()));
                map.put("applyAmount", BigDecimal.valueOf(a.getWithdrawAmount()));
                map.put("fee", BigDecimal.valueOf(a.getWithdrawFee()));
                map.put("realAmount", BigDecimal.valueOf(a.getRealAmount()));
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
        return "reports/代理商提现" + System.currentTimeMillis() + ".xlsx";
    }

    private void createExcel(String fileName, Collection collection, ColumnBuilder... columns) {
        try {
            DynamicReports.report().ignorePageWidth()
                    .ignorePagination()
                    .columns(columns)
                    .setDataSource(collection)
                    .toXlsx(getJasperXlsxExporterBuilder(fileName));
        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    private JasperXlsxExporterBuilder getJasperXlsxExporterBuilder(String fileName) {
        return DynamicReports.export.xlsxExporter(fileName)
                .setDetectCellType(true)
                .setIgnorePageMargins(true)
                .setWhitePageBackground(false)
                .setRemoveEmptySpaceBetweenRows(true);
    }

    /**
     * 统计今日代理商提现申请次数
     *
     * @param aUser 代理商
     * @return 申请提现次数
     */
    public long todayCount(AUser aUser) {
        Specification<AWithdrawRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (aUser != null) {
                predicates.add(cb.equal(root.get("aUser"), aUser));
            }
            predicates.add(cb.between(root.get("applyTime"),
                    DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR),
                    DateUtils.getEndDate(new Date(), Calendar.DAY_OF_YEAR)));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return count(spec);
    }
}
