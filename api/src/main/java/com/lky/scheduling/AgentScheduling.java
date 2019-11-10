package com.lky.scheduling;

import com.google.common.collect.Lists;
import com.lky.commons.utils.ArithUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.JsonUtils;
import com.lky.dto.AgentArea;
import com.lky.entity.*;
import com.lky.enums.dict.ABalanceRecordDict;
import com.lky.enums.dict.AWPointRecordDict;
import com.lky.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

import static com.lky.service.ComputeService.W_POINT_RATE;

/**
 * 代理商任务
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/25
 */
@Component
public class AgentScheduling {

    @Inject
    private AUserService aUserService;

    @Inject
    private ComputeService computeService;

    @Inject
    private AIncomeRecordService aIncomeRecordService;

    @Inject
    private AWPointRecordService awPointRecordService;

    @Inject
    private ABalanceRecordService aBalanceRecordService;

    @Inject
    private WPointRecordService wPointRecordService;

    /**
     * 每天凌晨零点1分计算代理商收益
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void income() {
        // 1、获取所有可用的代理商
        List<AUser> activeAgent = aUserService.findActiveAgent();
        // 2、解析代理商代理的区域
        if (!CollectionUtils.isEmpty(activeAgent)) {
            List<AWPointRecord> awPointRecordList = Lists.newArrayList();
            List<AIncomeRecord> aIncomeRecordList = Lists.newArrayList();
            List<ABalanceRecord> aBalanceRecordList = Lists.newArrayList();
            for (AUser aUser : activeAgent) {
                Integer aUserId = aUser.getId();
                AUserInfo aUserInfo = aUser.getAUserInfo();
                AUserAsset aUserAsset = aUser.getAUserAsset();
                AgentArea agentArea = JsonUtils.jsonToObject(aUserInfo.getArea(), AgentArea.class);
                String area = agentArea.getProvince() != null ? agentArea.getProvince().getName() : "";
                area = agentArea.getCity() != null ? area + agentArea.getCity().getName() : area;
                area = agentArea.getDistrict() != null ? area + agentArea.getDistrict().getName() : area;
                // 3、根据代理区域查询该区域前一天用户消费额，包含线下订单和线上订单（确认收货的订单,减去退款的金额）
                //代理区域白积分变动数量
                double sumConsumerWPoint = wPointRecordService.sumAgentIncomeWPoint(area, null, null);
                //代理区域白积分转销售额
                double sumConsumerAmount = ArithUtils.round(ArithUtils.mul(sumConsumerWPoint, W_POINT_RATE), 2);
                // 4、根据用户消费额和设置的收益率计算代理商所得收益
                double incomeRate = aUserInfo.getIncomeRate();
                double payRate = aUserInfo.getPayRate();
                double incomeAmount = computeService.agentIncomeAmount(sumConsumerAmount, incomeRate, payRate);
                // 5、判断代理商是否全额出资，全额出资全部转换成代理商大米，否则按照出资比例收益转换成大米和G米
                double incomeWPoint = 0;
                double backAmount = 0;
                if (!aUserInfo.getPayAll()) {
                    incomeWPoint = computeService.agentIncomeWPoint(sumConsumerAmount, incomeRate, payRate);
                    // 6、是否需要倒扣
                    long backBeginDate = aUserInfo.getBackBeginDate().getTime();
                    long currentTimeMillis = System.currentTimeMillis();
                    if (backBeginDate <= currentTimeMillis) {
                        // 7、计算倒扣金额
                        backAmount = computeService.agentBackAmount(incomeAmount, aUserInfo.getBackRate());
                        double amount = aUserInfo.getAmount();
                        double payAmount = aUserInfo.getPayAmount();
                        double sumBackAmount = aUserAsset.getSumBackAmount();
                        if (amount - payAmount - sumBackAmount < backAmount) {
                            backAmount = amount - payAmount - sumBackAmount;
                        }
                        //倒扣金额为负数则设置为0
                        backAmount = backAmount > 0 ? backAmount : 0;
                        aUserAsset.setSumBackAmount(ArithUtils.round(sumBackAmount + backAmount, 2));
                    }
                    if (incomeWPoint != 0) {
                        aUserAsset.setWpoint(ArithUtils.round(aUserAsset.getWpoint() + incomeWPoint, 2));
                        AWPointRecord awPointRecord = new AWPointRecord();
                        awPointRecord.setAUserId(aUserId);
                        awPointRecord.setType(String.valueOf(AWPointRecordDict.TYPE_INCOME));
                        awPointRecord.setChangeWPoint(incomeWPoint);
                        awPointRecord.setCurrentWPoint(aUserAsset.getWpoint());
                        awPointRecord.setRemark("营业额为：" + sumConsumerAmount + "，收益获得G米：" + incomeWPoint);
                        awPointRecordList.add(awPointRecord);
                    }
                }
                if (sumConsumerAmount != 0) {
                    AIncomeRecord aIncomeRecord = new AIncomeRecord();
                    aIncomeRecord.setAUserId(aUserId);
                    aIncomeRecord.setConsumerAmount(sumConsumerWPoint);
                    aIncomeRecord.setIncomeAmount(ArithUtils.round(incomeAmount - backAmount, 2));
                    aIncomeRecord.setIncomeSumAmount(incomeAmount);
                    aIncomeRecord.setBackAmount(backAmount);
                    aIncomeRecord.setIncomeWPoint(incomeWPoint);
                    aIncomeRecordList.add(aIncomeRecord);
                }
                if (incomeAmount - backAmount != 0) {
                    incomeAmount = ArithUtils.round(incomeAmount - backAmount, 2);
                    aUserAsset.setSumIncomeAmount(ArithUtils.round(aUserAsset.getSumIncomeAmount() + incomeAmount, 2));
                    aUserAsset.setBalance(ArithUtils.round(aUserAsset.getBalance() + incomeAmount, 2));
                    ABalanceRecord aBalanceRecord = new ABalanceRecord();
                    aBalanceRecord.setAUserId(aUserId);
                    aBalanceRecord.setType(String.valueOf(ABalanceRecordDict.TYPE_INCOME));
                    aBalanceRecord.setChangeBalance(incomeAmount);
                    aBalanceRecord.setCurrentBalance(aUserAsset.getBalance());
                    aBalanceRecord.setRemark("营业额为：" + sumConsumerAmount + "，收益获得金额：" + incomeAmount);
                    aBalanceRecordList.add(aBalanceRecord);
                }
            }
            if (!CollectionUtils.isEmpty(awPointRecordList)) {
                awPointRecordService.save(awPointRecordList);
            }
            if (!CollectionUtils.isEmpty(aIncomeRecordList)) {
                aIncomeRecordService.save(aIncomeRecordList);
            }
            if (!CollectionUtils.isEmpty(aBalanceRecordList)) {
                aBalanceRecordService.save(aBalanceRecordList);
            }
            aUserService.save(activeAgent);
        }
    }
}
