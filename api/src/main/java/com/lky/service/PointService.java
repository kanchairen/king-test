package com.lky.service;

import com.lky.commons.utils.ArithUtils;
import com.lky.dto.ProcessOrdersDto;
import com.lky.entity.*;
import com.lky.enums.dict.BalanceRecordDict;
import com.lky.enums.dict.LockWPointRecordDict;
import com.lky.enums.dict.RPointRecordDict;
import com.lky.enums.dict.WPointRecordDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.lky.dto.ProcessOrdersDto.ORDERS_TYPE_OFFLINE;
import static com.lky.dto.ProcessOrdersDto.ORDERS_TYPE_ONLINE;
import static com.lky.enums.dict.BalanceRecordDict.TYPE_MERCHANT_RPOINT_CONVERT_BALANCE;
import static com.lky.enums.dict.WPointRecordDict.TYPE_CONSUMER_WPOINT_ONLINE_ORDERS;

/**
 * 米操作
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/4/4
 */
@Component
public class PointService {

    @Inject
    private LockWPointRecordService lockWPointRecordService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private RPointRecordService rPointRecordService;

    @Inject
    private UserService userService;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private Environment environment;

    private static final Logger log = LoggerFactory.getLogger(ApplyRecordService.class);

    /**
     * 用户注册奖励，
     * 送存量G米、G米和推荐人G米
     *
     * @param user 用户
     */
    @Transactional
    public void userRegisterAward(User user) {
        //2018年4月20号之前注册的用户实名认证没有注册奖励
        try {
            String noRegisterAward = environment.getProperty("no-register-award-time");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date awardTimePoint = dateFormat.parse(noRegisterAward);
            if (user.getCreateTime().before(awardTimePoint)) {
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        HighConfig highConfig = baseConfigService.findH();
        if (highConfig == null) {
            log.error("高级配置为空，请尽快处理。");
            return;
        }
        double lockWPoint = highConfig.getRegisterGiveWPointNum();
        double registerWPoint = highConfig.getRegisterWPoint();
        double registerParentWPoint = highConfig.getRegisterParentWPoint();
        if (lockWPoint > 0) {
            user.getUserAsset().setLockWPoint(user.getUserAsset().getLockWPoint() + lockWPoint);
            //添加注册赠送用户存量G米记录
            LockWPointRecord lockWPointRecord = new LockWPointRecord();
            lockWPointRecord.setUser(user);
            lockWPointRecord.setType(String.valueOf(LockWPointRecordDict.TYPE_CONSUMER_REGISTER));
            lockWPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_CONSUMER));
            lockWPointRecord.setCalculated(Boolean.FALSE);
            lockWPointRecord.setChangeLockWPoint(lockWPoint);
            lockWPointRecord.setCurrentLockWPoint(user.getUserAsset().getLockWPoint());
            lockWPointRecord.setRemark("用户注册获得用户存量G米：" + lockWPoint);
            lockWPointRecord.setCreateTime(new Date());
            lockWPointRecordService.save(lockWPointRecord);
        }
        if (registerWPoint > 0) {
            user.getUserAsset().setWpoint(user.getUserAsset().getWpoint() + registerWPoint);
            WPointRecord wPointRecord = new WPointRecord();
            wPointRecord.setUser(user);
            wPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_REGISTER));
            wPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_CONSUMER));
            wPointRecord.setCalculated(Boolean.FALSE);
            wPointRecord.setChangeWPoint(registerWPoint);
            wPointRecord.setCurrentWPoint(user.getUserAsset().getWpoint());
            wPointRecord.setRemark("用户注册获得用户G米：" + registerWPoint);
            wPointRecord.setCreateTime(new Date());
            wPointRecordService.save(wPointRecord);
        }

        userService.update(user);
        if (user.getParentId() != null && registerParentWPoint > 0) {
            User parentUser = userService.findById(user.getParentId());
            parentUser.getUserAsset().setWpoint(parentUser.getUserAsset().getWpoint() + registerParentWPoint);
            WPointRecord wPointRecord = new WPointRecord();
            wPointRecord.setUser(parentUser);
            wPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_REGISTER_DIVIDE));
            wPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_CONSUMER));
            wPointRecord.setCalculated(Boolean.FALSE);
            wPointRecord.setChangeWPoint(registerParentWPoint);
            wPointRecord.setCurrentWPoint(parentUser.getUserAsset().getWpoint());
            wPointRecord.setRemark("推荐用户（" + user.getMobile() + "）注册，奖励用户G米：" + registerParentWPoint);
            wPointRecord.setCreateTime(new Date());
            wPointRecordService.save(wPointRecord);
            userService.update(parentUser);
        }
    }

    /**
     * 开店奖励
     *
     * @param user       用户
     * @param paidAmount 支付金额
     * @param highConfig 高级配置
     */
    @Transactional
    public void merchantSettledAward(User user, double paidAmount, HighConfig highConfig) {
        //商家入驻成功，获得商家存量G米
        if (highConfig.getBeMerchantWPointNum() > 0) {
            user.getUserAsset().setMerchantLockWPoint(user.getUserAsset().getMerchantLockWPoint() + highConfig.getBeMerchantWPointNum());
            LockWPointRecord lockWPointRecord = new LockWPointRecord();
            lockWPointRecord.setUser(user);
            lockWPointRecord.setType(String.valueOf(LockWPointRecordDict.TYPE_MERCHANT_REGISTER));
            lockWPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_MERCHANT));
            lockWPointRecord.setCalculated(Boolean.FALSE);
            lockWPointRecord.setChangeLockWPoint(highConfig.getBeMerchantWPointNum());
            lockWPointRecord.setCurrentLockWPoint(user.getUserAsset().getMerchantLockWPoint());
            lockWPointRecord.setRemark("商家开店获得商家存量G米：" + highConfig.getBeMerchantWPointNum());
            lockWPointRecordService.save(lockWPointRecord);
        }
        //商家入驻成功，获得N倍的商家G米
        if (highConfig.getBeMerchantWPointRate() > 0 && paidAmount > 0) {
            double merchantWPoint = ArithUtils.round(highConfig.getBeMerchantWPointRate() * paidAmount * 100, 2);
            user.getUserAsset().setMerchantWPoint(user.getUserAsset().getMerchantWPoint() + merchantWPoint);
            WPointRecord wPointRecord = new WPointRecord();
            wPointRecord.setUser(user);
            wPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_MERCHANT_REGISTER));
            wPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
            wPointRecord.setCalculated(Boolean.FALSE);
            wPointRecord.setChangeWPoint(merchantWPoint);
            wPointRecord.setCurrentWPoint(user.getUserAsset().getMerchantWPoint());
            wPointRecord.setRemark("商家入驻成功，获得商家G米：" + merchantWPoint);
            wPointRecordService.save(wPointRecord);
        }
        userService.update(user);

        //商家入驻成功，推荐人获得大米和G米
        if (user.getParentId() != null && (highConfig.getBeMerchantParentWPoint() > 0 || highConfig.getBeMerchantParentBalance() > 0)) {
            User parentUser = userService.findById(user.getParentId());
            if (highConfig.getBeMerchantParentWPoint() > 0) {
                parentUser.getUserAsset().setWpoint(parentUser.getUserAsset().getWpoint() + highConfig.getBeMerchantParentWPoint());
                WPointRecord wPointRecord = new WPointRecord();
                wPointRecord.setUser(parentUser);
                wPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_BE_MERCHANT_PARENT_WPOINT));
                wPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                wPointRecord.setCalculated(Boolean.FALSE);
                wPointRecord.setChangeWPoint(highConfig.getBeMerchantParentWPoint());
                wPointRecord.setCurrentWPoint(parentUser.getUserAsset().getWpoint());
                wPointRecord.setRemark("推荐商家（" + user.getMobile() + "）入驻成功，奖励G米：" + highConfig.getBeMerchantParentWPoint());
                wPointRecordService.save(wPointRecord);
            }

            if (highConfig.getBeMerchantParentBalance() > 0) {
                parentUser.getUserAsset().setBalance(parentUser.getUserAsset().getBalance() + highConfig.getBeMerchantParentBalance());
                BalanceRecord balanceRecord = new BalanceRecord();
                balanceRecord.setUser(parentUser);
                balanceRecord.setType(String.valueOf(BalanceRecordDict.TYPE_BE_MERCHANT_PARENT_BALANCE));
                balanceRecord.setChangeBalance(highConfig.getBeMerchantParentBalance());
                balanceRecord.setCurrentBalance(parentUser.getUserAsset().getBalance());
                balanceRecord.setRemark("推荐商家（" + user.getMobile() + "）入驻成功，奖励大米：" + highConfig.getBeMerchantParentWPoint());
                balanceRecordService.save(balanceRecord);
            }
            userService.update(parentUser);
        }
    }

    /**
     * 处理资产操作记录
     *
     * @param processOrdersDto 处理订单dto
     */
    @Transactional
    public void processAssertRecord(ProcessOrdersDto processOrdersDto) {

        User user = processOrdersDto.getUser();
        Shop shop = processOrdersDto.getShop();
        boolean needWaitReceive = processOrdersDto.isNeedWaitReceive();
        double rPointPrice = processOrdersDto.getRpointPrice();
        double wPointPrice = processOrdersDto.getWpointPrice();
        double amount = processOrdersDto.getAmount();
        double consumerGiveWPoint = processOrdersDto.getConsumerGiveWPoint();
        double merchantGiveRPoint = processOrdersDto.getMerchantGiveRPoint();
        double merchantGiveWPoint = processOrdersDto.getMerchantGiveWPoint();
        double merchantGiveCash = processOrdersDto.getMerchantGiveCash();
        double merchantGiveOrdersWPoint = processOrdersDto.getMerchantGiveOrdersWPoint();
        String ordersType = processOrdersDto.getOrdersType();

        String consumerWPointRecordType = "";
        String consumerRPointRecordType = "";
        String balanceRecordType = "";
        String merchantWPointRecordType = "";
        String merchantRPointRecordType = "";
        String remarkName = "";
        if (ORDERS_TYPE_ONLINE.equals(ordersType)) {
            consumerWPointRecordType = String.valueOf(WPointRecordDict.TYPE_CONSUMER_ONLINE_ORDERS);
            consumerRPointRecordType = String.valueOf(RPointRecordDict.TYPE_CONSUMER_ONLINE_ORDERS);
            balanceRecordType = String.valueOf(BalanceRecordDict.TYPE_ONLINE_ORDERS);
            merchantWPointRecordType = String.valueOf(WPointRecordDict.TYPE_MERCHANT_ONLINE_ORDERS);
            merchantRPointRecordType = String.valueOf(RPointRecordDict.TYPE_MERCHANT_ONLINE_ORDERS);
            remarkName = "线上";
        } else if (ORDERS_TYPE_OFFLINE.equals(ordersType)) {
            consumerWPointRecordType = String.valueOf(WPointRecordDict.TYPE_CONSUMER_OFFLINE_ORDERS);
            consumerRPointRecordType = String.valueOf(RPointRecordDict.TYPE_CONSUMER_OFFLINE_ORDERS);
            balanceRecordType = String.valueOf(BalanceRecordDict.TYPE_OFFLINE_ORDERS);
            merchantWPointRecordType = String.valueOf(WPointRecordDict.TYPE_MERCHANT_OFFLINE_ORDERS);
            merchantRPointRecordType = String.valueOf(RPointRecordDict.TYPE_MERCHANT_OFFLINE_ORDERS);
            remarkName = "线下";
        }

        // ---------------------   操作用户   --------------------- //
        HighConfig highConfig = baseConfigService.findH();

        //更新消费者账户资产
        assert user != null;
        assert shop != null;
        UserAsset userAsset = user.getUserAsset();

        //线上订单确认收货前的操作
        if (needWaitReceive) {
            //插入消费者小米记录
            if (rPointPrice > 0) {
                double rPoint = userAsset.getRpoint() - rPointPrice > 0 ? userAsset.getRpoint() - rPointPrice : 0;

                RPointRecord consumerRPointRecord = new RPointRecord();
                consumerRPointRecord.setUser(user);
                consumerRPointRecord.setChangeRPoint(-rPointPrice);
                consumerRPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_CONSUMER));
                consumerRPointRecord.setType(consumerRPointRecordType);
                consumerRPointRecord.setCurrentRPoint(rPoint);
                consumerRPointRecord.setRemark(remarkName + "（" + shop.getName() + "）店铺消费：" + rPointPrice + "小米，获得G米：" + consumerGiveWPoint);
                rPointRecordService.save(consumerRPointRecord);

                userAsset.setRpoint(rPoint);
            }
            //插入消费者G米记录
            if (wPointPrice > 0) {
                double wPoint = userAsset.getWpoint() - wPointPrice > 0 ? userAsset.getWpoint() - wPointPrice : 0;
                WPointRecord consumerWPointRecord = new WPointRecord();
                consumerWPointRecord.setUser(user);
                consumerWPointRecord.setChangeWPoint(-wPointPrice);
                consumerWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                consumerWPointRecord.setType(String.valueOf(TYPE_CONSUMER_WPOINT_ONLINE_ORDERS));
                consumerWPointRecord.setCurrentWPoint(wPoint);
                consumerWPointRecord.setRemark(remarkName + "（" + shop.getName() + "）店铺消费：" + wPointPrice + "G米");
                wPointRecordService.save(consumerWPointRecord);

                userAsset.setWpoint(wPoint);
            }
        } else {
            //插入消费者G米记录
            if (consumerGiveWPoint > 0) {
                WPointRecord consumerWPointRecord = new WPointRecord();
                consumerWPointRecord.setUser(user);
                consumerWPointRecord.setChangeWPoint(consumerGiveWPoint);
                consumerWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                consumerWPointRecord.setType(consumerWPointRecordType);
                consumerWPointRecord.setCalculated(Boolean.TRUE);
                consumerWPointRecord.setCurrentWPoint(userAsset.getWpoint() + consumerGiveWPoint);
                consumerWPointRecord.setRemark(remarkName + "（" + shop.getName() + "）店铺消费：" + amount + "金额，获得G米：" + consumerGiveWPoint);
                wPointRecordService.save(consumerWPointRecord);

                //消费者两级上线得到G米分成
                wPointRecordService.userSharingWPoint(user, consumerGiveWPoint);

                double unLockWPointTemp = 0;
                //解锁比例带出消费者的存量积分
                if (highConfig != null) {
                    unLockWPointTemp = ArithUtils.round(consumerGiveWPoint * highConfig.getUnlockWPointRate() / 100, 2);
                }

                //解冻消费者冻结的G米数量
                double unLockWPoint = 0;
                double lockWPoint = userAsset.getLockWPoint() - unLockWPointTemp;
                if (userAsset.getLockWPoint() > 0) {
                    if (lockWPoint > 0) {
                        unLockWPoint += unLockWPointTemp;
                        userAsset.setLockWPoint(lockWPoint);
                    } else {
                        unLockWPoint += userAsset.getLockWPoint();
                        userAsset.setLockWPoint(0);
                    }

                    //插入解冻G米记录
                    if (unLockWPoint > 0) {
                        WPointRecord consumerUnLockWPointRecord = new WPointRecord();
                        consumerUnLockWPointRecord.setUser(user);
                        consumerUnLockWPointRecord.setChangeWPoint(unLockWPoint);
                        consumerUnLockWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                        consumerUnLockWPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_LOCK_WPOINT_CONVERT));
                        consumerUnLockWPointRecord.setCurrentWPoint(userAsset.getWpoint() + consumerGiveWPoint + unLockWPoint);
                        consumerUnLockWPointRecord.setRemark(remarkName + "（" + shop.getName() + "）店铺消费：" + amount + "金额，解冻G米：" + unLockWPoint);
                        wPointRecordService.save(consumerUnLockWPointRecord);

                        //添加转化成用户G米消耗的用户存量G米记录
                        LockWPointRecord consumerLockWPointRecord = new LockWPointRecord();
                        consumerLockWPointRecord.setUser(user);
                        consumerLockWPointRecord.setType(String.valueOf(LockWPointRecordDict.TYPE_CONSUMER_LOCK_WPOINT_CONVERT));
                        consumerLockWPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_CONSUMER));
                        consumerLockWPointRecord.setCalculated(Boolean.FALSE);
                        consumerLockWPointRecord.setChangeLockWPoint(-unLockWPoint);
                        consumerLockWPointRecord.setCurrentLockWPoint(userAsset.getLockWPoint());
                        consumerLockWPointRecord.setRemark("转化成用户G米消耗的用户存量G米：" + unLockWPoint);
                        lockWPointRecordService.save(consumerLockWPointRecord);
                    }
                }
                //更新消费者G米记录
                userAsset.setWpoint(userAsset.getWpoint() + consumerGiveWPoint + unLockWPoint);
            }

            // ---------------------   操作商家   --------------------- //

            //更新商家账户资产
            User merchant = shop.getUser();
            if (merchant.getId() == user.getId()) {
                merchant = user;
            }
            UserAsset merchantUserAsset = merchant.getUserAsset();

            if (merchantGiveWPoint > 0 || merchantGiveOrdersWPoint > 0) {

                double merchantWPoint = merchantUserAsset.getMerchantWPoint();

                //插入商家G米记录
                if (merchantGiveWPoint > 0) {
                    WPointRecord merchantWPointRecord = new WPointRecord();
                    merchantWPointRecord.setUser(merchant);
                    merchantWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
                    merchantWPointRecord.setChangeWPoint(merchantGiveWPoint);
                    merchantWPointRecord.setType(merchantWPointRecordType);
                    merchantWPointRecord.setCurrentWPoint(merchantWPoint + merchantGiveWPoint);
                    merchantWPointRecord.setRemark("用户" + remarkName + "消费：" + amount + "金额，获得G米：" + merchantGiveWPoint);
                    wPointRecordService.save(merchantWPointRecord);
                    merchantWPoint += merchantGiveWPoint;
                }

                if (merchantGiveOrdersWPoint > 0) {
                    WPointRecord merchantWPointRecord = new WPointRecord();
                    merchantWPointRecord.setUser(merchant);
                    merchantWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
                    merchantWPointRecord.setChangeWPoint(merchantGiveOrdersWPoint);
                    merchantWPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_MERCHANT_WPOINT_ONLINE_ORDERS));
                    merchantWPointRecord.setCurrentWPoint(merchantWPoint + merchantGiveOrdersWPoint);
                    merchantWPointRecord.setRemark("用户" + remarkName + "消费G米：" + merchantGiveOrdersWPoint + "，商家获得G米：" + merchantGiveOrdersWPoint);
                    wPointRecordService.save(merchantWPointRecord);
                    merchantWPoint += merchantGiveOrdersWPoint;
                }

                //商家上级用户得到G米分成
                wPointRecordService.merchantSharingWPoint(merchant, merchantGiveWPoint);

                double unLockWPointTemp = 0;
                //解锁比例带出商家的存量积分
                if (highConfig != null) {
                    unLockWPointTemp = ArithUtils.round(merchantGiveWPoint * highConfig.getUnlockWPointRate() / 100, 2);
                }

                //解冻商家冻结的G米数量
                double merchantLockWPoint = merchantUserAsset.getMerchantLockWPoint() - unLockWPointTemp;
                double merchantUnLockWPoint = 0;
                if (merchantUserAsset.getMerchantLockWPoint() > 0) {
                    if (merchantLockWPoint > 0) {
                        merchantUnLockWPoint += unLockWPointTemp;
                        merchantUserAsset.setMerchantLockWPoint(merchantLockWPoint);
                    } else {
                        merchantUnLockWPoint += merchantUserAsset.getMerchantLockWPoint();
                        merchantUserAsset.setMerchantLockWPoint(0);
                    }
                    //插入解冻G米记录
                    if (merchantUnLockWPoint > 0) {
                        WPointRecord merchantUnLockWPointRecord = new WPointRecord();
                        merchantUnLockWPointRecord.setUser(merchant);
                        merchantUnLockWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
                        merchantUnLockWPointRecord.setChangeWPoint(merchantUnLockWPoint);
                        merchantUnLockWPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_MERCHANT_CONVERT_LOCK_WPOINT));
                        merchantUnLockWPointRecord.setCurrentWPoint(merchantWPoint + merchantUnLockWPoint);
                        merchantUnLockWPointRecord.setRemark("用户" + remarkName + "消费：" + amount + "金额，解冻G米：" + merchantUnLockWPoint);
                        wPointRecordService.save(merchantUnLockWPointRecord);

                        //添加转化成商家G米消耗的商家存量G米记录
                        LockWPointRecord consumerLockWPointRecord = new LockWPointRecord();
                        consumerLockWPointRecord.setUser(merchant);
                        consumerLockWPointRecord.setType(String.valueOf(LockWPointRecordDict.TYPE_MERCHANT_LOCK_WPOINT_CONVERT));
                        consumerLockWPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_MERCHANT));
                        consumerLockWPointRecord.setCalculated(Boolean.FALSE);
                        consumerLockWPointRecord.setChangeLockWPoint(-merchantUnLockWPoint);
                        consumerLockWPointRecord.setCurrentLockWPoint(merchantUserAsset.getMerchantLockWPoint());
                        consumerLockWPointRecord.setRemark("转化成商家G米消耗的商家存量G米：" + merchantUnLockWPoint);
                        lockWPointRecordService.save(consumerLockWPointRecord);

                        merchantWPoint += merchantUnLockWPoint;
                    }

                }
                merchantUserAsset.setMerchantWPoint(merchantWPoint);
            }

            //插入商家大米记录
            if (merchantGiveCash > 0) {
                this.cashDeposit(merchant, merchantUserAsset, merchantGiveCash, balanceRecordType);
            }

            //插入商家小米记录
            if (merchantGiveRPoint > 0) {
                double merchantRPoint = merchantUserAsset.getMerchantRPoint();
                RPointRecord merchantRPointRecord = new RPointRecord();
                merchantRPointRecord.setUser(merchant);
                merchantRPointRecord.setChangeRPoint(merchantGiveRPoint);
                merchantRPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_MERCHANT));
                merchantRPointRecord.setType(merchantRPointRecordType);
                merchantRPointRecord.setCurrentRPoint(merchantRPoint + merchantGiveRPoint);
                merchantRPointRecord.setRemark("用户" + remarkName + "消费小米：" + rPointPrice + "小米，获得小米：" + merchantGiveRPoint);
                rPointRecordService.save(merchantRPointRecord);
                merchantRPoint += merchantGiveRPoint;
                merchantUserAsset.setMerchantRPoint(merchantRPoint);
            }

            if (merchant.getId() != user.getId()) {
                userService.update(merchant);
            }
        }

        user.setUserAsset(userAsset);
        userService.update(user);
    }

    /**
     * 冻结保证金
     *
     * @param merchant          商家
     * @param merchantUserAsset 商家资产
     * @param merchantGiveCash  商家获取的金额
     * @param type              类型
     */
    @Transactional
    public void cashDeposit(User merchant, UserAsset merchantUserAsset, double merchantGiveCash, String type) {
        double balance = merchantUserAsset.getBalance();
        //线下订单消费不进入保证金
        if (BalanceRecordDict.TYPE_OFFLINE_ORDERS.compare(type)) {
            //商家得到的现金直接到放入大米
            BalanceRecord balanceRecord = new BalanceRecord();
            balanceRecord.setUser(merchant);
            balanceRecord.setChangeBalance(merchantGiveCash);
            balanceRecord.setCurrentBalance(balance + merchantGiveCash);
            balanceRecord.setType(type);
            balanceRecord.setRemark("用户下单消费，大米增加：" + merchantGiveCash);
            balanceRecordService.save(balanceRecord);
            merchantUserAsset.setBalance(balance + merchantGiveCash);
            return;
        }

        //店铺保证金
        double cashDeposit = merchantUserAsset.getCashDeposit();

        HighConfig highConfig = baseConfigService.findH();
        double shopCashDeposit = highConfig.getShopCashDeposit();
        double sumCashDeposit = cashDeposit + merchantGiveCash;
        if (sumCashDeposit <= shopCashDeposit) {
            merchantUserAsset.setCashDeposit(sumCashDeposit);
        } else {
            //店铺剩余多出的部分保证金
            double diffCashDeposit = sumCashDeposit - shopCashDeposit;
            merchantUserAsset.setCashDeposit(shopCashDeposit);
            if (diffCashDeposit > 0) {
                //商家得到的现金直接到放入大米
                BalanceRecord balanceRecord = new BalanceRecord();
                balanceRecord.setUser(merchant);
                balanceRecord.setChangeBalance(diffCashDeposit);
                balanceRecord.setCurrentBalance(balance + diffCashDeposit);
                balanceRecord.setType(type);
                if (TYPE_MERCHANT_RPOINT_CONVERT_BALANCE.compare(type)) {
                    balanceRecord.setRemark("商家手动转换全部小米获得的大米：" + diffCashDeposit);
                } else {
                    balanceRecord.setRemark("用户下单消费，大米增加：" + diffCashDeposit);
                }
                balanceRecordService.save(balanceRecord);
                merchantUserAsset.setBalance(balance + diffCashDeposit);
            }
        }
    }

}
