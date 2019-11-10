package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.redis.RedisHelper;
import com.lky.commons.utils.*;
import com.lky.dao.UserSqlDao;
import com.lky.dto.HighConfigDto;
import com.lky.entity.*;
import com.lky.enums.dict.*;
import com.lky.global.constant.Constant;
import com.lky.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lky.global.constant.Constant.*;
import static java.util.Calendar.DAY_OF_YEAR;

/**
 * G米转换业务
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/3/9
 */
@Service
@Transactional
public class ConvertService {

    private static final Logger log = LoggerFactory.getLogger(ConvertService.class);

    @Inject
    private UserService userService;

    @Inject
    private AUserService aUserService;

    @Inject
    private ComputeService computeService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private AWPointRecordService awPointRecordService;

    @Inject
    private RPointRecordService rPointRecordService;

    @Inject
    private ARPointRecordService arPointRecordService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private ABalanceRecordService aBalanceRecordService;

    @Inject
    private RedisHelper redisHelper;

    @Inject
    private UserAssetService userAssetService;

    @Inject
    private UserSqlDao userSqlDao;

    @Inject
    private SurplusGrainRecordService surplusGrainRecordService;

    @Inject
    private BaseConfigService baseConfigService;

    /**
     * 冻结所有用户和商家的G米
     *
     * @param lockWPointRate 冻结比例
     */
    public void lockWPoint(Double lockWPointRate) {
        List<User> allUser = userService.findAll();
        for (User user : allUser) {
            UserAsset userAsset = user.getUserAsset();
            if (userAsset != null) {
                double wPoint = userAsset.getWpoint();
                double merchantWPoint = userAsset.getMerchantWPoint();
                if (wPoint > 0) {
                    double lockWPoint = computeService.lockWPoint(wPoint, lockWPointRate);
                    userAsset.setLockWPoint(userAsset.getLockWPoint() + lockWPoint);
                    userAsset.setWpoint(wPoint - lockWPoint);
                    //插入用户G米变动记录
                    WPointRecord consumerWPointRecord = new WPointRecord();
                    consumerWPointRecord.setUser(user);
                    consumerWPointRecord.setChangeWPoint(-lockWPoint);
                    consumerWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                    consumerWPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_CONVERT_LOCK_WPOINT));
                    consumerWPointRecord.setCurrentWPoint(wPoint - lockWPoint);
                    consumerWPointRecord.setRemark("系统冻结用户G米：" + lockWPoint);
                    wPointRecordService.save(consumerWPointRecord);
                }
                if (merchantWPoint > 0) {
                    double merchantLockWPoint = computeService.lockWPoint(merchantWPoint, lockWPointRate);
                    userAsset.setMerchantLockWPoint(userAsset.getMerchantLockWPoint() + merchantLockWPoint);
                    userAsset.setMerchantWPoint(merchantWPoint - merchantLockWPoint);
                    //插入商家G米变动记录
                    WPointRecord merchantWPointRecord = new WPointRecord();
                    merchantWPointRecord.setUser(user);
                    merchantWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
                    merchantWPointRecord.setChangeWPoint(-merchantLockWPoint);
                    merchantWPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_MERCHANT_CONVERT_LOCK_WPOINT));
                    merchantWPointRecord.setCurrentWPoint(merchantWPoint - merchantLockWPoint);
                    merchantWPointRecord.setRemark("系统冻结商家G米：" + merchantLockWPoint);
                    wPointRecordService.save(merchantWPointRecord);
                }
                if (wPoint > 0 || merchantWPoint > 0) {
                    //更新用户资产
                    user.setUserAsset(userAsset);
                    userService.update(user);
                }
            }
        }
    }

    /**
     * 获取乐康指数
     *
     * @return 乐康指数
     */
    public double getLHealthIndex() {
        Double lhealth = (Double) redisHelper.get(Constant.L_HEATH_INDEX);
        return lhealth == null ? 0 : lhealth;
    }

    /**
     * 执行转换
     *
     */
    public void executeMUser() {
        Integer userId = (Integer) redisHelper.rightPop(USER_CONVERT_LIST);
        double lHealthIndex = getLHealthIndex();
        if (userId != null && lHealthIndex > 0) {
            User user = userService.findById(userId);
            UserAsset userAsset = user.getUserAsset();

            double userTransWPoint = userAsset.getTransWPoint();

            //用户可激励G米转换的都是用户小米和大米
            double convertRPoint = computeService.consumerWPointConvertRPoint(userTransWPoint, lHealthIndex);
            double convertBalance = computeService.consumerWPointConvertBalance(userTransWPoint, lHealthIndex);
            //转换掉的用户G米
            double convertWPoint = computeService.convertOverWPoint(userTransWPoint, lHealthIndex);
            if (convertWPoint > 0) {
                WPointRecord userWPointRecordConvertRPoint = new WPointRecord();
                userWPointRecordConvertRPoint.setUser(user);
                userWPointRecordConvertRPoint.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_CONVERT_RPOINT));
                userWPointRecordConvertRPoint.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                userWPointRecordConvertRPoint.setChangeWPoint(-convertWPoint);
                userWPointRecordConvertRPoint.setCurrentWPoint(userAsset.getWpoint() - convertWPoint);
                userWPointRecordConvertRPoint.setRemark("系统自动转换成消耗的用户G米：" + convertWPoint);
                wPointRecordService.save(userWPointRecordConvertRPoint);
            }

            //插入用户G米转换成用户小米变动记录
            if (convertBalance > 0) {
                RPointRecord userRPointRecord = new RPointRecord();
                userRPointRecord.setUser(user);
                userRPointRecord.setType(String.valueOf(RPointRecordDict.TYPE_CONSUMER_CONVERT));
                userRPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_CONSUMER));
                userRPointRecord.setChangeRPoint(convertRPoint);
                userRPointRecord.setCurrentRPoint(userAsset.getRpoint() + convertRPoint);
                userRPointRecord.setRemark("系统自动转换G米，通过用户G米获得的小米：" + convertRPoint);
                rPointRecordService.save(userRPointRecord);
            }

            //插入用户G米转换成大米变动记录
            if (convertBalance > 0) {
                BalanceRecord balanceRecord = new BalanceRecord();
                balanceRecord.setUser(user);
                balanceRecord.setType(String.valueOf(BalanceRecordDict.TYPE_CONSUMER_WPOINT_CONVERT_BALANCE));
                balanceRecord.setChangeBalance(convertBalance);
                balanceRecord.setCurrentBalance(userAsset.getBalance() + convertBalance);
                balanceRecord.setRemark("系统自动转换G米，通过用户G米获得的大米：" + convertBalance);
                balanceRecordService.save(balanceRecord);
            }

            userAssetService.covertUserUpdate(userAsset.getId(), convertBalance, -convertWPoint, convertRPoint);
        }
    }

    public void executeMerchant() {
        Integer userId = (Integer) redisHelper.rightPop(MERCHANT_CONVERT_LIST);
        double lHealthIndex = getLHealthIndex();
        if (userId != null && lHealthIndex > 0) {
            User user = userService.findById(userId);
            UserAsset userAsset = user.getUserAsset();
            double merchantTransWPoint = userAsset.getMerchantTransWPoint();

            //商家可激励G米转换的都是用户小米和大米
            double merchantConvertRPoint = computeService.merchantWPointConvertRPoint(merchantTransWPoint, lHealthIndex);
            double merchantConvertBalance = computeService.merchantWPointConvertBalance(merchantTransWPoint, lHealthIndex);
            //转换掉的商家G米
            double convertMerchantWPoint = computeService.convertOverWPoint(merchantTransWPoint, lHealthIndex);

            if (convertMerchantWPoint > 0) {
                WPointRecord merchantWPointRecordConvertRPoint = new WPointRecord();
                merchantWPointRecordConvertRPoint.setUser(user);
                merchantWPointRecordConvertRPoint.setType(String.valueOf(WPointRecordDict.TYPE_MERCHANT_CONVERT_RPOINT));
                merchantWPointRecordConvertRPoint.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
                merchantWPointRecordConvertRPoint.setChangeWPoint(-convertMerchantWPoint);
                merchantWPointRecordConvertRPoint.setCurrentWPoint(userAsset.getMerchantWPoint() - convertMerchantWPoint);
                merchantWPointRecordConvertRPoint.setRemark("系统自动转换成消耗的商家G米：" + convertMerchantWPoint);
                wPointRecordService.save(merchantWPointRecordConvertRPoint);
            }

            //插入商家G米转换成用户小米变动记录
            if (merchantConvertRPoint > 0) {
                RPointRecord merchantRPointRecord = new RPointRecord();
                merchantRPointRecord.setUser(user);
                merchantRPointRecord.setType(String.valueOf(RPointRecordDict.TYPE_MERCHANT_CONVERT));
                merchantRPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_CONSUMER));
                merchantRPointRecord.setChangeRPoint(merchantConvertRPoint);
                merchantRPointRecord.setCurrentRPoint(userAsset.getRpoint() + merchantConvertRPoint);
                merchantRPointRecord.setRemark("系统自动转换G米，通过商家G米获得的小米：" + merchantConvertRPoint);
                rPointRecordService.save(merchantRPointRecord);
            }

            //插入商家G米转换成大米变动记录
            if (merchantConvertBalance > 0) {
                BalanceRecord merchantBalanceRecord = new BalanceRecord();
                merchantBalanceRecord.setUser(user);
                merchantBalanceRecord.setType(String.valueOf(BalanceRecordDict.TYPE_MERCHANT_WPOINT_CONVERT_BALANCE));
                merchantBalanceRecord.setChangeBalance(merchantConvertBalance);
                merchantBalanceRecord.setCurrentBalance(userAsset.getBalance() + merchantConvertBalance);
                merchantBalanceRecord.setRemark("系统自动转换G米，通过商家G米获得的大米：" + merchantConvertBalance);
                balanceRecordService.save(merchantBalanceRecord);
            }
            userAssetService.covertMerchantUpdate(userAsset.getId(), merchantConvertBalance, -convertMerchantWPoint, merchantConvertRPoint);
        }
    }

    public void executeAUser() {
        long startTime = System.currentTimeMillis();
        if (redisHelper.exists(L_HEATH_INDEX)) {
            Double lHealthIndex = (Double) redisHelper.get(L_HEATH_INDEX);
            if (lHealthIndex != null && lHealthIndex > 0) {
                this.aUserConvertTask(lHealthIndex);
                log.info("代理商转换消耗的时间：" + (System.currentTimeMillis() - startTime) + "ms");
            }
        }
    }

    /**
     * GNC代理商G米转换
     *
     * @param lHealthIndex GNC指数
     */
    private void aUserConvertTask(double lHealthIndex) {
        List<AUser> activeAgent = aUserService.findActiveAgent()
                .stream()
                .filter(aUser -> aUser.getAUserAsset().getTransWPoint() > 0)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(activeAgent)) {
            List<AWPointRecord> awPointRecordList = Lists.newArrayList();
            List<ARPointRecord> arPointRecordList = Lists.newArrayList();
            List<ABalanceRecord> aBalanceRecordList = Lists.newArrayList();
            List<AUser> aUserList = Lists.newArrayList();
            for (AUser aUser : activeAgent) {
                AUserAsset aUserAsset = aUser.getAUserAsset();
                this.aUserOperation(awPointRecordList, arPointRecordList, aBalanceRecordList, lHealthIndex, aUser.getId(), aUserAsset);
            }
            if (!CollectionUtils.isEmpty(awPointRecordList)) {
                awPointRecordService.save(awPointRecordList);
            }
            if (!CollectionUtils.isEmpty(arPointRecordList)) {
                arPointRecordService.save(arPointRecordList);
            }
            if (!CollectionUtils.isEmpty(aBalanceRecordList)) {
                aBalanceRecordService.save(aBalanceRecordList);
            }
            if (!CollectionUtils.isEmpty(aUserList)) {
                aUserService.update(aUserList);
            }
        }
    }

    /**
     * 代理商用户可激励G米操作记录
     *
     * @param awPointRecordList  代理商G米记录列表
     * @param arPointRecordList  代理商小米记录列表
     * @param aBalanceRecordList 代理商大米记录列表
     * @param lHealthIndex       乐康指数
     * @param aUserId            代理商用户id
     * @param aUserAsset         代理商用户资产
     */
    private void aUserOperation(List<AWPointRecord> awPointRecordList, List<ARPointRecord> arPointRecordList, List<ABalanceRecord> aBalanceRecordList,
                                double lHealthIndex, Integer aUserId, AUserAsset aUserAsset) {
        double userTransWPoint = aUserAsset.getTransWPoint();
        if (userTransWPoint > 0) {
            //代理商用户可激励G米转换的都是代理商用户小米和大米
            double convertRPoint = computeService.agentWPointConvertRPoint(userTransWPoint, lHealthIndex);
            double convertBalance = computeService.agentWPointConvertBalance(userTransWPoint, lHealthIndex);
            //转换掉的代理商用户G米
            double convertWPoint = computeService.convertOverWPoint(userTransWPoint, lHealthIndex);

            Map<String, Double> convertOverWPointMap = computeService.convertOverAWPointMap(convertWPoint);
            if (CollectionUtils.isEmpty(convertOverWPointMap)) {
                return;
            }
            double agentWPointConvertRPoint = convertOverWPointMap.get("agentConvertRPoint");
            double agentWPointConvertBalance = convertOverWPointMap.get("agentConvertBalance");

            //插入代理商用户G米转换成小米变动记录
            double currentWPointConvertRPoint = aUserAsset.getWpoint() - agentWPointConvertRPoint > 0 ? aUserAsset.getWpoint() - agentWPointConvertRPoint : 0;
            double currentWPointConvertBalance = currentWPointConvertRPoint - agentWPointConvertBalance > 0 ? currentWPointConvertRPoint - agentWPointConvertBalance : 0;

            if (currentWPointConvertRPoint > 0) {
                AWPointRecord userWPointRecordConvertRPoint = new AWPointRecord();
                userWPointRecordConvertRPoint.setAUserId(aUserId);
                userWPointRecordConvertRPoint.setType(String.valueOf(AWPointRecordDict.TYPE_CONVERT_RPOINT));
                userWPointRecordConvertRPoint.setChangeWPoint(-agentWPointConvertRPoint);
                userWPointRecordConvertRPoint.setCurrentWPoint(currentWPointConvertRPoint);
                userWPointRecordConvertRPoint.setRemark("系统自动转换成小米的代理商用户G米：" + agentWPointConvertRPoint);
                awPointRecordList.add(userWPointRecordConvertRPoint);
                aUserAsset.setWpoint(currentWPointConvertRPoint);
            }

            //插入代理商用户G米转换成大米变动记录
            if (agentWPointConvertBalance > 0) {
                AWPointRecord userWPointRecordConvertBalance = new AWPointRecord();
                userWPointRecordConvertBalance.setAUserId(aUserId);
                userWPointRecordConvertBalance.setType(String.valueOf(AWPointRecordDict.TYPE_CONVERT_BALANCE));
                userWPointRecordConvertBalance.setChangeWPoint(-agentWPointConvertBalance);
                userWPointRecordConvertBalance.setCurrentWPoint(currentWPointConvertBalance);
                userWPointRecordConvertBalance.setRemark("系统自动转换成大米的代理商用户G米：" + agentWPointConvertBalance);
                awPointRecordList.add(userWPointRecordConvertBalance);
                aUserAsset.setWpoint(currentWPointConvertBalance);
            }

            //插入代理商用户G米转换成用户小米变动记录
            if (convertBalance > 0) {
                ARPointRecord userRPointRecord = new ARPointRecord();
                userRPointRecord.setAUserId(aUserId);
                userRPointRecord.setType(String.valueOf(ARPointRecordDict.TYPE_WPOINT_CONVERT));
                userRPointRecord.setChangeRPoint(convertRPoint);
                userRPointRecord.setCurrentRPoint(aUserAsset.getRpoint() + convertRPoint);
                userRPointRecord.setRemark("系统自动转换代理商G米，通过代理商用户G米获得的小米：" + convertRPoint);
                arPointRecordList.add(userRPointRecord);
                aUserAsset.setRpoint(aUserAsset.getRpoint() + convertRPoint);
            }

            //插入代理商用户G米转换成大米变动记录
            if (convertBalance > 0) {
                ABalanceRecord balanceRecord = new ABalanceRecord();
                balanceRecord.setAUserId(aUserId);
                balanceRecord.setType(String.valueOf(ABalanceRecordDict.TYPE_WPOINT_CONVERT_BALANCE));
                balanceRecord.setChangeBalance(convertBalance);
                balanceRecord.setCurrentBalance(aUserAsset.getBalance() + convertBalance);
                balanceRecord.setRemark("系统自动转换代理商G米，通过代理商用户G米获得的大米：" + convertBalance);
                aBalanceRecordList.add(balanceRecord);
                aUserAsset.setBalance(aUserAsset.getBalance() + convertBalance);
                aUserAsset.setConvertAmount(ArithUtils.round(aUserAsset.getConvertAmount() + convertBalance, 2));
            }
        }
    }

    public HighConfigDto toDto(HighConfig highConfig) {
        HighConfigDto highConfigDto = new HighConfigDto();
        BeanUtils.copyPropertiesIgnoreNull(highConfig, highConfigDto, "memberRights", "remindMobile");

        if (StringUtils.isNotEmpty(highConfig.getMemberRights())) {
            Map<String, Double> sharingMap = JsonUtils.jsonToMap(highConfig.getMemberRights(), String.class, Double.class);
            if (sharingMap != null) {
                highConfigDto.setFirstSharing(sharingMap.get(HighConfigDict.SHARING_FIRST.getKey()));
                highConfigDto.setSecondSharing(sharingMap.get(HighConfigDict.SHARING_SECOND.getKey()));
                highConfigDto.setMerchantSharing(sharingMap.get(HighConfigDict.SHARING_MERCHANT.getKey()));
            }
        }

        if (StringUtils.isNotEmpty(highConfig.getRemindMobile())) {
            highConfigDto.setRemindMobileMap(JsonUtils.jsonToMap(highConfig.getRemindMobile(), String.class, Object.class));
        }
        return highConfigDto;
    }


    public void automaticAddSurplusGrain(HighConfig highConfig) {
        log.info("大米开始自动转入余粮公社。。。");
        long startTime = System.currentTimeMillis();
        double maxSurplusGrain = highConfig.getMaxSurplusGrain();
        List<Map<String, Object>> simpleList = userSqlDao.findAutomaticSurplusGrain(maxSurplusGrain);
        log.info("查询符合自动转入条件用户sql消耗的时间：" + (System.currentTimeMillis() - startTime) + "ms");
        if (!CollectionUtils.isEmpty(simpleList)) {
            String balanceType = String.valueOf(BalanceRecordDict.TYPE_TO_SURPLUS_GRAIN);
            String surplusGrainType = String.valueOf(SurplusGrainRecordDict.TYPE_FROM_BALANCE);
            List<BalanceRecord> balanceRecordList = Lists.newArrayListWithCapacity(simpleList.size());
            List<SurplusGrainRecord> surplusGrainRecordList = Lists.newArrayListWithCapacity(simpleList.size());

            Date beginDate = DateUtils.getBeginDate(new Date(), DAY_OF_YEAR);
            Date incomeDay = DateUtils.add(beginDate, Calendar.DATE, highConfig.getSurplusGrainStartDay());
            for (Map<String, Object> map : simpleList) {
                int id = Integer.parseInt(map.get("id").toString());
                double balance = ((BigDecimal) map.get("balance")).doubleValue();
                double surplusGrain = ((BigDecimal) map.get("surplusGrain")).doubleValue();
                double amount;
                if ((balance + surplusGrain) > maxSurplusGrain) {
                    amount = maxSurplusGrain - surplusGrain;
                    surplusGrain = maxSurplusGrain;
                    balance -= amount;
                } else {
                    surplusGrain += balance;
                    amount = balance;
                    balance = 0;
                }
                if (amount > 0) {
                    BalanceRecord balanceRecord = new BalanceRecord();
                    User user = userService.findById(id);
                    balanceRecord.setUser(user);
                    balanceRecord.setType(balanceType);
                    balanceRecord.setCurrentBalance(balance);
                    balanceRecord.setChangeBalance(-amount);
                    balanceRecord.setRemark("大米转到余粮公社大米减少：" + amount);
                    balanceRecordList.add(balanceRecord);

                    SurplusGrainRecord surplusGrainRecord = new SurplusGrainRecord();
                    surplusGrainRecord.setChangeSurplusGrain(amount);
                    surplusGrainRecord.setCurrentSurplusGrain(surplusGrain);
                    surplusGrainRecord.setUser(user);
                    surplusGrainRecord.setType(surplusGrainType);
                    surplusGrainRecord.setIncomeTime(incomeDay);
                    surplusGrainRecord.setRemark("大米转入余粮公社：" + amount);
                    surplusGrainRecordList.add(surplusGrainRecord);
                    userSqlDao.updateSurplusGrain(user.getUserAsset().getId(), balance, surplusGrain);
                }
            }
            log.info("自动转入余粮公社消耗的时间：" + (System.currentTimeMillis() - startTime) + "ms");
            if (!CollectionUtils.isEmpty(balanceRecordList)) {
                balanceRecordService.save(balanceRecordList);
            }
            if (!CollectionUtils.isEmpty(surplusGrainRecordList)) {
                surplusGrainRecordService.save(surplusGrainRecordList);
            }
            log.info("自动转入余粮公社增加记录消耗的时间：" + (System.currentTimeMillis() - startTime) + "ms"+
                    " 更新用户数为：" + surplusGrainRecordList.size());
        }

    }

    public void incomeWPointBySurplusGrain(double surplusGrainRate) {
        log.info("余粮公社产生G米收益开始。。。");
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> calculateList = userSqlDao.incomeSurplusGrain(new Date());
        log.info("查询符合条件用户sql消耗的时间：" + (System.currentTimeMillis() - startTime) + "ms");
        if (!CollectionUtils.isEmpty(calculateList)) {
            String userType = String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER);
            String type = String.valueOf(WPointRecordDict.TYPE_CONSUMER_SURPLUS_GRAIN_INCOME);
            List<WPointRecord> wPointRecordList = Lists.newArrayListWithCapacity(calculateList.size());
            for (Map<String, Object> map : calculateList) {
                int id = Integer.parseInt(map.get("id").toString());
                double surplusGrain = ((BigDecimal) map.get("surplusGrain")).doubleValue();
                double wpoint = ((BigDecimal) map.get("wpoint")).doubleValue();
                User user = userService.findById(id);
                if (surplusGrain <= 0) {
                    continue;
                }
                double amount = ArithUtils.round(ArithUtils.mul(surplusGrainRate, surplusGrain), 2);
                if (amount > 0) {
                    //添加G米明细记录
                    WPointRecord wPointRecord = new WPointRecord();
                    double currentWPoint = wpoint + amount;
                    wPointRecord.setUser(user);
                    wPointRecord.setChangeWPoint(amount);
                    wPointRecord.setCalculated(false);
                    wPointRecord.setCurrentWPoint(currentWPoint);
                    wPointRecord.setUserType(userType);
                    wPointRecord.setType(type);
                    wPointRecord.setRemark("余粮公社收益获得用户G米:" + amount);
                    wPointRecordList.add(wPointRecord);
                    userSqlDao.updateSGPoint(user.getUserAsset().getId(), currentWPoint);
                }
            }
            log.info("余粮公社产生G米收益消耗的时间（不含增加记录）：" + (System.currentTimeMillis() - startTime) + "ms");
            wPointRecordService.save(wPointRecordList);
            log.info("余粮公社产生G米收益增加记录消耗的时间：" + (System.currentTimeMillis() - startTime) + "ms" +
                    " 更新用户数为：" + wPointRecordList.size());
        }
    }
}
