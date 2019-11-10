package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.*;
import com.lky.dao.PaymentRecordDao;
import com.lky.dto.OrdersCashierDto;
import com.lky.dto.PriceDto;
import com.lky.dto.ProcessOrdersDto;
import com.lky.dto.QrJumpDto;
import com.lky.entity.*;
import com.lky.enums.dict.*;
import com.lky.pay.ali.AliUtils;
import com.lky.pay.wx.WxUtils;
import com.lky.service.async.AsyncMessageService;
import com.lky.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.dto.ProcessOrdersDto.ORDERS_TYPE_OFFLINE;
import static com.lky.dto.ProcessOrdersDto.ORDERS_TYPE_ONLINE;
import static com.lky.enums.code.AssetResCode.BALANCE_NOT_ENOUGH;
import static com.lky.enums.code.OrderResCode.*;
import static com.lky.enums.code.UserResCode.PAY_PWD_NULL;
import static com.lky.enums.dict.OrdersReturnDict.*;
import static com.lky.enums.dict.PaymentRecordDict.*;

/**
 * 订单支付
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/17
 */
@Service
public class PaymentRecordService extends BaseService<PaymentRecord, Integer> {

    private static final Logger log = LoggerFactory.getLogger(PaymentRecordService.class);

    @Inject
    private PaymentRecordDao paymentRecordDao;

    @Inject
    private ApplyRecordService applyRecordService;

    @Inject
    private OrdersService ordersService;

    @Inject
    private OrdersItemService ordersItemService;

    @Inject
    private OfflineOrdersService offlineOrdersService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private PointService pointService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private ComputeService computeService;

    @Inject
    private UserService userService;

    @Inject
    private UserAssetService userAssetService;

    @Inject
    private ShopService shopService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private OrdersJobService ordersJobService;

    @Inject
    private OrdersReturnService ordersReturnService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private AsyncMessageService asyncMessageService;

    @Inject
    private AnnualFeeRecordService annualFeeRecordService;

    @Inject
    private RechargeRecordService rechargeRecordService;

    @Inject
    private QrCodeRecordService qrCodeRecordService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private LockWPointRecordService lockWPointRecordService;

    @Override
    public BaseDao<PaymentRecord, Integer> getBaseDao() {
        return this.paymentRecordDao;
    }

    public PaymentRecord findByTransactionCode(String transactionCode) {
        return paymentRecordDao.findByTransactionCode(transactionCode);
    }

    public PaymentRecord findByOrdersId(String ordersId) {
        SimpleSpecificationBuilder<PaymentRecord> builder = new SimpleSpecificationBuilder<>();
        builder.add("targetId", SpecificationOperator.Operator.likeAll, ordersId);
        builder.add("targetType", SpecificationOperator.Operator.eq, TARGET_TYPE_ORDERS.getKey());
        builder.add("state", SpecificationOperator.Operator.eq, STATE_PAID.getKey());
        List<PaymentRecord> paymentRecordList =
                this.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "createTime"));
        return CollectionUtils.isEmpty(paymentRecordList) ? null : paymentRecordList.get(0);
    }

    /**
     * 收银台详情
     *
     * @param user 用户
     * @param id   组合订单号，如果有多个用逗号隔开
     * @return 收银台详情
     */
    public OrdersCashierDto cashierDetail(User user, String id, String type) {
        OrdersCashierDto ordersCashierDto = new OrdersCashierDto();
        PriceDto priceDto = this.computePrice(user, id, type, ordersCashierDto);
        BeanUtils.copyPropertiesIgnoreNull(priceDto, ordersCashierDto);
        ordersCashierDto.setUserRPoint(user.getUserAsset().getRpoint());
        ordersCashierDto.setUserBalance(user.getUserAsset().getBalance());
        ordersCashierDto.setUserWPoint(user.getUserAsset().getWpoint());
        return ordersCashierDto;
    }

    public synchronized void pointPay(User user, String id, String payPwd) {
        //获取支付金额
        double amount = this.getPayAmount(user, id, String.valueOf(TARGET_TYPE_ORDERS), payPwd);
        AssertUtils.isTrue(PRICE_MUST_ZERO, amount == 0);
    }

    public synchronized void balancePay(User user, String id, String type, String payPwd) {
        //获取支付金额
        double amount = this.getPayAmount(user, id, type, payPwd);
        if (amount != 0) {
            String transactionCode = StringUtils.getUUID();
            this.build(user, transactionCode, id, type, amount, PaymentRecordDict.TYPE_BALANCE);
            AssertUtils.isTrue(BALANCE_NOT_ENOUGH, user.getUserAsset().getBalance() >= amount);

            this.notifyOperation(transactionCode, amount);

            userAssetService.balancePay(user.getUserAsset().getId(), amount);
            //添加大米明细记录
            BalanceRecord balanceRecord = new BalanceRecord();
            balanceRecord.setChangeBalance(-amount);
            balanceRecord.setCurrentBalance(user.getUserAsset().getBalance() - amount);
            balanceRecord.setUser(user);
            balanceRecord.setType(String.valueOf(BalanceRecordDict.TYPE_PAYMENT));
            balanceRecord.setRemark("大米支付，大米减少：" + amount);
            balanceRecordService.save(balanceRecord);
        }
    }

    public ResponseInfo wxpay(User user, String id, String type, String remoteIp, String payPwd) {
        //获取支付金额
        double amount = this.getPayAmount(user, id, type, payPwd);
        String wxpay = null;
        Boolean isJump = false;
        if (amount != 0) {
            String transactionCode = StringUtils.getUUID();
            PaymentRecord paymentRecord = this.build(user, transactionCode, id, type, amount, PaymentRecordDict.TYPE_WECHAT);
            String serverUrl = environmentService.serverUrl();
            if (environmentService.executeEnv()) {
                wxpay = WxUtils.unifiedOrder(transactionCode, paymentRecord.getAmount(), remoteIp, serverUrl);
            } else {
                wxpay = WxUtils.unifiedOrder(transactionCode, 0.01, remoteIp, serverUrl);
            }
            isJump = true;
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("wxpay", wxpay);
        responseInfo.putData("isJump", isJump);
        return responseInfo;
    }

    public ResponseInfo alipay(User user, String id, String type, String payPwd) {
        //获取支付金额
        double amount = this.getPayAmount(user, id, type, payPwd);
        String alipay = null;
        Boolean isJump = false;
        if (amount != 0) {
            String transactionCode = StringUtils.getUUID();
            PaymentRecord paymentRecord = this.build(user, transactionCode, id, type, amount, PaymentRecordDict.TYPE_ALIPAY);
            String serverUrl = environmentService.serverUrl();
            if (environmentService.executeEnv()) {
                alipay = AliUtils.appPay(transactionCode, paymentRecord.getAmount(), serverUrl);
            } else {
                alipay = AliUtils.appPay(transactionCode, 0.01, serverUrl);
            }
            isJump = true;
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("alipay", alipay);
        responseInfo.putData("isJump", isJump);
        return responseInfo;
    }

    public ResponseInfo unipay(User user, String id, String type, String payPwd) {
        //获取支付金额
        double amount = this.getPayAmount(user, id, type, payPwd);
        String unipay = null;
        Boolean isJump = false;
        if (amount != 0) {
            String transactionCode = StringUtils.getUUID();
            PaymentRecord paymentRecord = this.build(user, transactionCode, id, type, amount, PaymentRecordDict.TYPE_UNIPAY);
            //获取银联受理订单号
//            if (environmentService.executeEnv()) {
//                unipay = UnionUtils.appPay(transactionCode, paymentRecord.getAmount());
//            } else {
//                unipay = UnionUtils.appPay(transactionCode, 0.01);
//            }
            isJump = true;
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("unipay", unipay);
        responseInfo.putData("isJump", isJump);
        return responseInfo;
    }

    public boolean wxpayNotify(HttpServletRequest request) {
        Map<String, Object> map = WxUtils.acceptNotify(request);
        //更新申请开通记录为申请中、已支付或更新订单状态为已支付
        if (!CollectionUtils.isEmpty(map)) {
            String outTradeNo = (String) map.get("out_trade_no");
            double amount = AmountUtils.changeF2Y((String) map.get("total_fee"));
            return this.notifyOperation(outTradeNo, amount);
        }
        return Boolean.FALSE;
    }

    public boolean alipayNotify(HttpServletRequest request) {
        Map<String, Object> map = AliUtils.appNotify(request);
        //更新申请开通记录为申请中、已支付或更新订单状态为已支付
        if (!CollectionUtils.isEmpty(map)) {
            String outTradeNo = (String) map.get("outTradeNo");
            double amount = (double) map.get("amount");
            return this.notifyOperation(outTradeNo, amount);
        }
        return Boolean.FALSE;
    }

    public boolean unipayNotify(HttpServletRequest request) {
//        Map<String, String> map = UnionUtils.acceptNotice(request);
//        //更新申请开通记录为申请中、已支付或更新订单状态为已支付
//        if (!CollectionUtils.isEmpty(map)) {
//            //交易流水号
//            String outTradeNo = map.get("orderId");
//            //付款金额
//            double amount = AmountUtils.changeF2Y(map.get("txtAmt"));
//            return this.notifyOperation(outTradeNo, amount);
//        }
        return Boolean.FALSE;
    }

    /**
     * 获取需要支付的现金金额
     *
     * @param user 用户
     * @param id   需要支付的id
     * @param type 需要支付的类型
     * @return 需要支付的现金金额
     */
    private double getPayAmount(User user, String id, String type, String payPwd) {
        PriceDto priceDto = this.computePrice(user, id, type, null);
        //线上订单支付
        if (!CollectionUtils.isEmpty(priceDto.getOrdersList())) {
            //没有现金，只使用米支付
            if (priceDto.getPrice() == 0 && (priceDto.getRpointPrice() > 0 || priceDto.getWpointPrice() > 0)) {
                AssertUtils.notNull(PAY_PWD_NULL, payPwd);
                AssertUtils.isTrue(PROINT_LACK, user.getUserAsset().getRpoint() >= priceDto.getRpointPrice());
                AssertUtils.isTrue(WPOINT_LACK, user.getUserAsset().getWpoint() >= priceDto.getWpointPrice());
                this.processOnlineOrders(priceDto.getOrdersList(), String.valueOf(TYPE_POINT));
            }
        }
        return priceDto.getPrice();
    }

    /**
     * 构建未支付的支付记录
     *
     * @param user            用户
     * @param transactionCode 交易号
     * @param id              支付的id
     * @param type            支付的类型
     * @param payType         支付方式
     * @return 支付记录
     */
    private PaymentRecord build(User user, String transactionCode, String id, String type, double amount, PaymentRecordDict payType) {
        //构建未支付的支付记录
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setAmount(amount);
        paymentRecord.setTransactionCode(transactionCode);
        paymentRecord.setTargetId(id);
        paymentRecord.setTargetType(type);
        paymentRecord.setUser(user);
        paymentRecord.setPayType(String.valueOf(payType));
        paymentRecord.setState(String.valueOf(STATE_UNPAID));
        this.save(paymentRecord);
        return paymentRecord;
    }

    /**
     * 计算所有应付的金额
     *
     * @param user 用户
     * @param id   目标id
     * @param type 目标类型
     * @return 应付金额
     */
    private PriceDto computePrice(User user, String id, String type, OrdersCashierDto ordersCashierDto) {

        //应付金额
        double price = 0;
        //应付小米
        double rPointPrice = 0;
        //应付G米
        double wPointPrice = 0;

        PriceDto priceDto = new PriceDto();

        String[] checkIds = {"id"};

        PaymentRecordDict targetType = PaymentRecordDict.getEnum(type);
        switch (targetType) {
            //活动扫码支付，充值G米
            case TARGET_TYPE_QR_CODE:
                int qrCodeRecordId = Integer.parseInt(id);
                QrCodeRecord qrCodeRecord = qrCodeRecordService.findById(qrCodeRecordId);
                AssertUtils.notNull(PARAMS_EXCEPTION, checkIds, qrCodeRecord);
                price = qrCodeRecord.getAmount();
                if (ordersCashierDto != null) {
                    QrJumpDto qrJumpDto = new QrJumpDto();
                    qrJumpDto.setRedirect(qrCodeRecord.getRedirect());
                    qrJumpDto.setThreshold(qrCodeRecord.getThreshold());
                    qrJumpDto.setUrl(qrCodeRecord.getUrl());
                    ordersCashierDto.setQrJump(qrJumpDto);
                }
                break;
            //店铺申请开通支付
            case TARGET_TYPE_APPLY:
                int applyRecordId = Integer.parseInt(id);
                ApplyRecord applyRecord = applyRecordService.findById(applyRecordId);
                AssertUtils.notNull(PARAMS_EXCEPTION, checkIds, applyRecord);
                price = applyRecord.getAmount();
                break;
            //支付缴纳年费
            case TARGET_TYPE_ANNUAL:
                AnnualFeeRecord annualFeeRecord = annualFeeRecordService.findById(Integer.parseInt(id));
                AssertUtils.notNull(PARAMS_EXCEPTION, checkIds, annualFeeRecord);
                price = annualFeeRecord.getAmount();
                break;
            //充值大米
            case TARGET_TYPE_RECHARGE:
                RechargeRecord rechargeRecord = rechargeRecordService.findById(Integer.parseInt(id));
                AssertUtils.notNull(PARAMS_EXCEPTION, checkIds, rechargeRecord);
                price = rechargeRecord.getAmount();
                break;
            //线下订单支付
            case TARGET_TYPE_OFFLINE_ORDERS:
                OfflineOrders offlineOrders = offlineOrdersService.findById(id);
                AssertUtils.notNull(PARAMS_EXCEPTION, checkIds, offlineOrders);
                price = offlineOrders.getPrice();
                break;
            //订单支付
            case TARGET_TYPE_ORDERS:
                String[] ordersIdArray = id.split(",");
                List<Orders> ordersList = Lists.newArrayListWithCapacity(ordersIdArray.length);
                for (String orderId : ordersIdArray) {
                    Orders orders = ordersService.findByIdAndUser(orderId, user);
                    AssertUtils.notNull(PARAMS_EXCEPTION, checkIds, orders);
                    price += orders.getPrice();
                    rPointPrice += orders.getRpointPrice();
                    wPointPrice += orders.getWpointPrice();
                    ordersList.add(orders);
                }
                priceDto.setOrdersList(ordersList);
                break;
            default:
        }

        priceDto.setPrice(price);
        priceDto.setRpointPrice(rPointPrice);
        priceDto.setWpointPrice(wPointPrice);
        return priceDto;
    }

    /**
     * 处理回调的各种操作
     *
     * @param paymentRecord 支付记录
     */
    private void process(PaymentRecord paymentRecord) {
        String targetId = paymentRecord.getTargetId();
        PaymentRecordDict targetType = PaymentRecordDict.getEnum(paymentRecord.getTargetType());
        User user = paymentRecord.getUser();
        switch (targetType) {
            //活动扫码支付，充值G米
            case TARGET_TYPE_QR_CODE:
                QrCodeRecord qrCodeRecord = qrCodeRecordService.findById(Integer.parseInt(targetId));
                qrCodeRecord.setState(String.valueOf(AnnualFeeRecordDict.STATE_PAID));
                qrCodeRecordService.update(qrCodeRecord);

                //更新用户资产
                user.getUserAsset().setWpoint(ArithUtils.round(user.getUserAsset().getWpoint() + qrCodeRecord.getNumber(), 2));
                userService.update(user);

                //添加G米明细记录
                WPointRecord wPointRecord = new WPointRecord();
                wPointRecord.setUser(user);
                wPointRecord.setChangeWPoint(qrCodeRecord.getNumber());
                wPointRecord.setCalculated(qrCodeRecord.getCalculated());
                wPointRecord.setCurrentWPoint(user.getUserAsset().getWpoint());
                wPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                wPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_QR_CODE));
                wPointRecord.setRemark("通过扫码支付金额：" + qrCodeRecord.getAmount() + ",获得用户G米:" + qrCodeRecord.getNumber());
                wPointRecordService.save(wPointRecord);

                //消费者两级上线得到G米分成
                wPointRecordService.userSharingWPoint(user, qrCodeRecord.getNumber());
                break;

            //店铺申请开通支付
            case TARGET_TYPE_APPLY:
                //更新申请记录
                ApplyRecord applyRecord = applyRecordService.findById(Integer.parseInt(targetId));
                applyRecord.setState(String.valueOf(ApplyRecordDict.STATE_APPLY));
                //待支付金额
                double unpaidAmount = applyRecord.getAmount() - paymentRecord.getAmount();
                applyRecord.setAmount(unpaidAmount > 0 ? unpaidAmount : 0);
                applyRecord.setSumPaidAmount(applyRecord.getSumPaidAmount() + paymentRecord.getAmount());
                applyRecordService.update(applyRecord);
                break;

            //支付缴纳年费
            case TARGET_TYPE_ANNUAL:
                AnnualFeeRecord annualFeeRecord = annualFeeRecordService.findById(Integer.parseInt(targetId));
                annualFeeRecord.setState(String.valueOf(AnnualFeeRecordDict.STATE_PAID));
                annualFeeRecordService.update(annualFeeRecord);
                //更新店铺有效期
                Shop shop = shopService.findById(annualFeeRecord.getShopId());
                ShopDatum shopDatum = shop.getShopDatum();
                Date systemTime = new Date();
                Date correctTime; //如果店铺有效期大于系统时间则用店铺有效期时间相加，否则用系统时间相加
                if (AnnualFeeRecordDict.TYPE_SHOP.compare(annualFeeRecord.getType())) {
                    correctTime = (shopDatum.getOpenShopExpire().after(systemTime)) ?
                            shopDatum.getOpenShopExpire() : systemTime;
                    shopDatum.setOpenShopExpire(DateUtils.add(correctTime, Calendar.YEAR, annualFeeRecord.getNumber()));

                }
                shop.setShopDatum(shopDatum);
                shopService.update(shop);
                break;

            //充值大米
            case TARGET_TYPE_RECHARGE:
                //更新用户充值记录表
                RechargeRecord rechargeRecord = rechargeRecordService.findById(Integer.parseInt(targetId));
                rechargeRecord.setState(String.valueOf(RechargeRecordDict.STATE_PAID));
                rechargeRecord.setUpdateTime(new Date());
                rechargeRecordService.update(rechargeRecord);

                //更新用户资产
                user.getUserAsset().setBalance(ArithUtils.round(user.getUserAsset().getBalance() + paymentRecord.getAmount(), 2));
                userService.update(user);

                //添加大米明细记录
                BalanceRecord balanceRecord = new BalanceRecord();
                balanceRecord.setChangeBalance(ArithUtils.round(paymentRecord.getAmount(), 2));
                balanceRecord.setCurrentBalance(user.getUserAsset().getBalance());
                balanceRecord.setUser(user);
                balanceRecord.setType(String.valueOf(BalanceRecordDict.TYPE_RECHARGE));
                balanceRecord.setRemark("用户充值，大米增加：" + ArithUtils.round(paymentRecord.getAmount(), 2));
                balanceRecordService.save(balanceRecord);
                break;

            //线下订单支付
            case TARGET_TYPE_OFFLINE_ORDERS:
                OfflineOrders offlineOrders = offlineOrdersService.findById(targetId);
                this.processOfflineOrders(offlineOrders);
                break;

            //订单支付
            case TARGET_TYPE_ORDERS:
                if (StringUtils.isNotEmpty(targetId)) {
                    String[] ordersIdArray = targetId.split(",");
                    List<Orders> ordersList = Lists.newArrayListWithCapacity(ordersIdArray.length);
                    for (String orderId : ordersIdArray) {
                        Orders orders = ordersService.findById(orderId);
                        ordersList.add(orders);
                    }
                    this.processOnlineOrders(ordersList, paymentRecord.getPayType());
                } else {
                    log.error("notify operation exception, payment record targetId is null.");
                }
                break;

            default:
        }
    }

    /**
     * 支付成功，回调操作
     *
     * @param transactionCode 交易流水号
     * @param amount          交易金额
     * @return 是否操作成功
     */
    private synchronized Boolean notifyOperation(String transactionCode, double amount) {
        log.debug("transactionCode = {}, amount = {}", transactionCode, amount);
        if (StringUtils.isNotEmpty(transactionCode)) {
            PaymentRecord paymentRecord = this.findByTransactionCode(transactionCode);
            //未支付状态才能更新,且支付金额和数据库中的金额相等
            if (STATE_UNPAID.compare(paymentRecord.getState())) {
                if (environmentService.executeEnv()) {
                    if (amount != paymentRecord.getAmount()) {
                        return Boolean.FALSE;
                    }
                }
                this.process(paymentRecord);
                paymentRecord.setState(String.valueOf(STATE_PAID));
                paymentRecord.setUpdateTime(new Date());
                super.update(paymentRecord);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 处理线下订单
     *
     * @param offlineOrders 订单
     */
    private void processOfflineOrders(OfflineOrders offlineOrders) {
        //更新线下订单
        offlineOrders.setState(String.valueOf(OfflineOrdersDict.STATE_PAID));
        offlineOrders.setUpdateTime(new Date());
        offlineOrdersService.update(offlineOrders);

        User user = offlineOrders.getUser();
        Shop shop = offlineOrders.getShop();
        double amount = offlineOrders.getAmount();
        double price = offlineOrders.getPrice();
        double benefitRate = offlineOrders.getBenefitRate();
        double consumerGiveWPoint = offlineOrders.getGiveWPoint();

        double merchantGiveWPoint = computeService.merchantGiveWPoint(amount, benefitRate);

        double merchantGiveCash = 0;
        if (price != 0) {
            merchantGiveCash = computeService.merchantGiveCash(price, benefitRate);
        }

        ProcessOrdersDto processOrdersDto = new ProcessOrdersDto();
        processOrdersDto.setUser(user);
        processOrdersDto.setShop(shop);
        processOrdersDto.setOrdersType(ORDERS_TYPE_OFFLINE);
        processOrdersDto.setAmount(amount);
        processOrdersDto.setConsumerGiveWPoint(consumerGiveWPoint);
        processOrdersDto.setMerchantGiveWPoint(merchantGiveWPoint);
        processOrdersDto.setMerchantGiveCash(merchantGiveCash);
        processOrdersDto.setNeedWaitReceive(Boolean.FALSE);
        pointService.processAssertRecord(processOrdersDto);

        //发送异步消息提醒
        asyncMessageService.sendOfflineOrdersPay(user.getMobile());
    }

    /**
     * 处理线上订单
     *
     * @param ordersList 线上订单列表
     * @param payType    支付方式
     */
    private void processOnlineOrders(List<Orders> ordersList, String payType) {
        //更新订单记录
        if (!CollectionUtils.isEmpty(ordersList)) {
            //更新订单列表
            ordersService.update(ordersList.stream().map(orders -> {
                orders.setState(String.valueOf(OrdersDict.STATE_SEND));
                orders.setUpdateTime(new Date());
                orders.setPayType(payType);
                orders.setPayTime(new Date());
                return orders;
            }).collect(Collectors.toList()));
            //删除定时任务
            ordersJobService.batchDel(OrdersJobDict.TYPE_CLOSE, ordersList.stream().map(Orders::getId).collect(Collectors.toSet()));
            //发送异步消息提醒
            asyncMessageService.onlineOrdersPay(ordersList);
            //计算支付价格
            PriceDto priceDto = this.computePayPrice(ordersList, Boolean.TRUE, Boolean.FALSE);
            ProcessOrdersDto processOrdersDto = new ProcessOrdersDto();
            processOrdersDto.setUser(ordersList.get(0).getUser());
            processOrdersDto.setShop(shopService.findById(ordersList.get(0).getShopId()));
            processOrdersDto.setOrdersType(ORDERS_TYPE_ONLINE);
            processOrdersDto.setNeedWaitReceive(Boolean.TRUE);
            BeanUtils.copyPropertiesIgnoreNull(priceDto, processOrdersDto);
            pointService.processAssertRecord(processOrdersDto);
        }
    }

    /**
     * 计算支付了的价格
     *
     * @param ordersList        订单列表
     * @param updateProductSold 是否更新商品库存
     * @return 价格dto
     */
    public PriceDto computePayPrice(List<Orders> ordersList, boolean updateProductSold, boolean updateOrdersReturn) {
        PriceDto priceDto = new PriceDto();

        double amount = 0, rPointPrice = 0, wPointPrice = 0, consumerGiveWPoint = 0;
        double merchantGiveWPoint = 0, merchantGiveCash = 0, merchantGiveRPoint = 0;
        double merchantGiveOrdersWPoint = 0, merchantGiveCash_old = 0;
        for (Orders orders : ordersList) {
            Shop shop = shopService.findById(orders.getShopId());
            Boolean openRPoint = shop.getShopConfig().getOpenRPoint();
            amount += orders.getAmount();
            rPointPrice += orders.getRpointPrice();
            wPointPrice += orders.getWpointPrice();
            consumerGiveWPoint += orders.getGiveWPoint();

            //获取订单子项,计算订单获取的商家资产
            List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId(orders.getId());
            for (OrdersItem ordersItem : ordersItemList) {
                //确认收货需要的操作
                if (updateOrdersReturn) {
                    OrdersReturn ordersReturn = ordersReturnService.findByOrdersItemId(ordersItem.getId());
                    if (ordersReturn != null && STATE_AGREE.compare(ordersItem.getReturnState()) && ordersReturn.getPrice() > 0) {
                        //扣减退款的订单项
                        if (!RETURN_TYPE_EXCHANGE.compare(ordersReturn.getReturnType())) {
                            consumerGiveWPoint -= ordersItem.getGiveWPoint();
                            wPointPrice -= ordersItem.getWpointPrice();
                            merchantGiveOrdersWPoint -= ordersItem.getWpointPrice();
                        }
                        continue;
                    }
                    //已申请退的订单项，全部修改为拒绝
                    if (STATE_APPLY.compare(ordersItem.getReturnState())) {
                        ordersItem.setReturnState(String.valueOf(STATE_REFUSE));
                        ordersItemService.update(ordersItem);

                        if (ordersReturn != null) {
                            ordersReturn.setUpdateTime(new Date());
                            ordersReturn.setState(String.valueOf(STATE_REFUSE));
                            ordersReturn.setReplyReason("确认收货，系统自动处理退款申请。");
                            ordersReturnService.update(ordersReturn);
                        }
                    }
                }
                double benefitRate = ordersItem.getBenefitRate();
                double ordersItemPrice = ordersItem.getPrice();
                double ordersItemWPointPrice = ordersItem.getWpointPrice();
                int ordersItemNumber = ordersItem.getNumber();
                if (ordersItemPrice != 0) {
                    merchantGiveCash += computeService.merchantGiveCash(ordersItemPrice * ordersItemNumber, benefitRate);
                    merchantGiveWPoint += computeService.merchantGiveWPoint(ordersItemPrice * ordersItemNumber, benefitRate);
                }
                if (ordersItemPrice != 0 && !ordersItem.getUseRPoint()) {
                    merchantGiveCash_old += computeService.merchantGiveCash(ordersItemPrice * ordersItemNumber, benefitRate);
                }
                if (ordersItem.getRpointPrice() != 0 && !openRPoint) {
                    merchantGiveRPoint += computeService.merchantGiveRPoint(ordersItem.getRpointPrice() * ordersItemNumber, benefitRate);
                }
                if (ordersItemWPointPrice != 0) {
                    merchantGiveOrdersWPoint += ordersItemWPointPrice * ordersItemNumber;
                }

                if (updateProductSold) {
                    //更新商品和商品组的总销量
                    Product product = ordersItem.getProduct();
                    product.setTotalSold(product.getTotalSold() + ordersItemNumber);
                    ProductGroup productGroup = productGroupService.findById(product.getProductGroupId());
                    productGroup.setTotalSold(productGroup.getTotalSold() + ordersItemNumber);
                    productGroupService.update(productGroup);
                }
            }
            if (orders.getRpointPrice() != 0 && openRPoint) {
                merchantGiveRPoint += orders.getRpointPrice();
            }
            //不使用小米支付运费
            if (!openRPoint) {
                merchantGiveCash = merchantGiveCash_old + orders.getFreightPrice();
            } else {
                merchantGiveCash += orders.getFreightPrice() - orders.getRpointPrice() * ComputeService.R_POINT_RATE;
            }
        }

        priceDto.setAmount(amount);
        priceDto.setRpointPrice(rPointPrice);
        priceDto.setWpointPrice(wPointPrice);
        priceDto.setConsumerGiveWPoint(consumerGiveWPoint);
        priceDto.setMerchantGiveCash(merchantGiveCash);
        priceDto.setMerchantGiveWPoint(merchantGiveWPoint);
        priceDto.setMerchantGiveRPoint(merchantGiveRPoint);
        priceDto.setMerchantGiveOrdersWPoint(merchantGiveOrdersWPoint);
        return priceDto;
    }
}
