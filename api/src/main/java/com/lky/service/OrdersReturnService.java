package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.OrdersReturnDao;
import com.lky.entity.*;
import com.lky.enums.dict.*;
import com.lky.pay.ali.AliUtils;
import com.lky.pay.wx.WxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.OrderResCode.ORDERS_RETURN_FAIL;
import static com.lky.enums.code.OrderResCode.PAYMENT_RECORD_NOT_EXIST;
import static com.lky.enums.dict.OrdersReturnDict.RETURN_TYPE_EXCHANGE;
import static com.lky.enums.dict.OrdersReturnDict.STATE_AGREE;
import static com.lky.enums.dict.RPointRecordDict.TYPE_CONSUMER_RETURNED;

/**
 * 订单退款
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@Service
public class OrdersReturnService extends BaseService<OrdersReturn, Integer> {

    private static final Logger log = LoggerFactory.getLogger(OrdersReturnService.class);

    @Inject
    private OrdersReturnDao ordersReturnDao;

    @Inject
    private OrdersService ordersService;

    @Inject
    private OrdersItemService ordersItemService;

    @Inject
    private UserService userService;

    @Inject
    private RPointRecordService rPointRecordService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private PaymentRecordService paymentRecordService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Override
    public BaseDao<OrdersReturn, Integer> getBaseDao() {
        return this.ordersReturnDao;
    }

    public OrdersReturn findByOrdersItemId(Integer ordersItemId) {
        SimpleSpecificationBuilder<OrdersReturn> builder = new SimpleSpecificationBuilder<>();
        builder.add("ordersItemId", SpecificationOperator.Operator.eq, ordersItemId);
        List<OrdersReturn> ordersReturnList = super.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "id"));
        return CollectionUtils.isEmpty(ordersReturnList) ? null : ordersReturnList.get(0);
    }

    public OrdersReturn findByOrdersId(String ordersId) {
        SimpleSpecificationBuilder<OrdersReturn> builder = new SimpleSpecificationBuilder<>();
        builder.add("ordersId", SpecificationOperator.Operator.eq, ordersId);
        List<OrdersReturn> ordersReturnList = super.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "id"));
        return CollectionUtils.isEmpty(ordersReturnList) ? null : ordersReturnList.get(0);
    }

    /**
     * 处理退款
     *
     * @param ordersReturn 订单退款记录
     * @param state        退款状态
     */
    public void process(OrdersReturn ordersReturn, String state) {
        ordersReturn.setState(state);
        ordersReturn.setUpdateTime(new Date());
        super.update(ordersReturn);

        String ordersId = ordersReturn.getOrdersId();
        Orders orders = ordersService.findById(ordersId);
        if (ordersReturn.getOrdersItemId() != null) {
            OrdersItem ordersItem = ordersItemService.findById(ordersReturn.getOrdersItemId());
            ordersItem.setReturnState(state);
            ordersItemService.update(ordersItem);
        } else {
            orders.setReturnState(state);
            ordersService.update(orders);
        }
        //同意售后需要的操作
        if (STATE_AGREE.compare(state)) {
            //处理退款，检查订单是否还有退款的，没有删除订单状态
            if (ordersReturn.getOrdersItemId() != null) {
                List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId(ordersId);
                if (!CollectionUtils.isEmpty(ordersItemList)) {
                    boolean returned = ordersItemList.stream()
                            .allMatch(item -> STATE_AGREE.compare(item.getReturnState()));
                    if (returned) {
                        returned = RETURN_TYPE_EXCHANGE.compare(ordersReturn.getReturnType())
                                && STATE_AGREE.compare(ordersReturn.getState());
                        //如有订单中有换货的子订单，则不关闭该订单。
                        if (ordersItemList.size() > 1 && !returned) {
                            List<OrdersReturn> ordersReturnList = ordersReturnDao.findByOrdersId(ordersId);
                            if (!CollectionUtils.isEmpty(ordersReturnList)) {
                                returned = ordersReturnList.stream()
                                        .anyMatch(item ->
                                                RETURN_TYPE_EXCHANGE.compare(item.getReturnType())
                                                        && STATE_AGREE.compare(item.getState()));
                            }
                        }
                        if (!returned) {
                            orders.setState(String.valueOf(OrdersDict.STATE_CLOSE));
                        }
                        orders.setReturned(Boolean.FALSE);
                        ordersService.update(orders);
                    }
                }
            } else {
                //同意且是退款，关闭订单
                if (!RETURN_TYPE_EXCHANGE.compare(ordersReturn.getReturnType())) {
                    orders.setState(String.valueOf(OrdersDict.STATE_CLOSE));
                }
                orders.setReturned(Boolean.FALSE);
                ordersService.update(orders);
            }

            //如果是退款金额，则原路返还
            if (!RETURN_TYPE_EXCHANGE.compare(ordersReturn.getReturnType())) {
                User user = userService.findById(ordersReturn.getUserId());
                //普通商城退款，只退现金
                if (ordersReturn.getOrdersItemId() != null) {
                    priceRefund(orders, ordersReturn);
                } else {
                    double rPointPrice = ordersReturn.getRpointPrice();
                    double wPointPrice = ordersReturn.getWpointPrice();
                    double price = ordersReturn.getPrice();
                    UserAsset userAsset = user.getUserAsset();
                    //退G米支付的价格
                    if (wPointPrice > 0) {
                        userAsset.setWpoint(userAsset.getWpoint() + wPointPrice);
                        //退货G米记录
                        WPointRecord returnWPointRecord = new WPointRecord();
                        returnWPointRecord.setUser(user);
                        returnWPointRecord.setChangeWPoint(wPointPrice);
                        returnWPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                        returnWPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_RETURNED));
                        returnWPointRecord.setCurrentWPoint(userAsset.getWpoint());
                        returnWPointRecord.setRemark("退款获得G米：" + wPointPrice);
                        wPointRecordService.save(returnWPointRecord);
                    }
                    //退小米支付的价格
                    if (rPointPrice > 0) {
                        userAsset.setRpoint(userAsset.getRpoint() + rPointPrice);
                        //退货小米记录
                        RPointRecord returnRPointRecord = new RPointRecord();
                        returnRPointRecord.setUser(user);
                        returnRPointRecord.setChangeRPoint(rPointPrice);
                        returnRPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_CONSUMER));
                        returnRPointRecord.setType(String.valueOf(TYPE_CONSUMER_RETURNED));
                        returnRPointRecord.setCurrentRPoint(userAsset.getRpoint());
                        returnRPointRecord.setRemark("退款获得小米：" + rPointPrice);
                        rPointRecordService.save(returnRPointRecord);
                    }
                    //退现金支付的价格
                    if (price > 0) {
                        priceRefund(orders, ordersReturn);
                    }
                    userService.update(user);
                }
            }
        }
    }

    /**
     * 退现金支付的钱
     *
     * @param orders       订单
     * @param ordersReturn 退款记录
     */
    private void priceRefund(Orders orders, OrdersReturn ordersReturn) {
        PaymentRecord paymentRecord = paymentRecordService.findByOrdersId(orders.getId());
        AssertUtils.notNull(PAYMENT_RECORD_NOT_EXIST, paymentRecord);
        String transactionCode = paymentRecord.getTransactionCode();

        double ordersReturnPrice = 0.01;
        double totalFee = 0.01;
        if (environmentService.executeEnv()) {
            totalFee = paymentRecord.getAmount();
            ordersReturnPrice = ordersReturn.getPrice();
        }
        //支付方式，用来返还退款金额
        PaymentRecordDict payType = PaymentRecordDict.getEnum(orders.getPayType());
        boolean refund = false;
        switch (payType) {
            case TYPE_ALIPAY:
                refund = AliUtils.refund(transactionCode, ordersReturnPrice);
                break;
            case TYPE_WECHAT:
                refund = WxUtils.refund(transactionCode, totalFee, ordersReturnPrice);
                break;
            case TYPE_BALANCE:
                refund = this.balanceRefund(paymentRecord, ordersReturn.getPrice());
            case TYPE_UNIPAY:
                break;
            default:
                break;
        }
        AssertUtils.isTrue(ORDERS_RETURN_FAIL, refund);
    }

    /**
     * 大米退款
     *
     * @param paymentRecord 支付记录
     * @param ordersReturnPrice 退款金额
     * @return 退款成功
     */
    public boolean balanceRefund(PaymentRecord paymentRecord, double ordersReturnPrice) {
        AssertUtils.notNull(PARAMS_EXCEPTION, paymentRecord);
        AssertUtils.isTrue(PARAMS_EXCEPTION, paymentRecord.getAmount() >= ordersReturnPrice);
        //更新用户账户大米
        User user = paymentRecord.getUser();
        user.getUserAsset().setBalance(user.getUserAsset().getBalance() + ordersReturnPrice);
        userService.update(user);
        //添加大米明细记录
        BalanceRecord balanceRecord = new BalanceRecord();
        balanceRecord.setChangeBalance(ordersReturnPrice);
        balanceRecord.setCurrentBalance(user.getUserAsset().getBalance());
        balanceRecord.setUser(user);
        balanceRecord.setType(String.valueOf(BalanceRecordDict.TYPE_REFUND));
        balanceRecord.setRemark("退款获得的大米，大米增加：" + ordersReturnPrice);
        balanceRecordService.save(balanceRecord);
        return true;
    }

    /**
     * 订单退款总额
     *
     * @param ordersList 订单列表
     * @return 退款总额
     */
    public double sumOrdersReturned(List<Orders> ordersList) {
        double returnSum = 0;
        Set<String> ordersIdSet = ordersList.stream().filter(Orders::getReturned).map(Orders::getId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(ordersIdSet)) {
            SimpleSpecificationBuilder<OrdersReturn> builder = new SimpleSpecificationBuilder<>();
            builder.add("ordersId", SpecificationOperator.Operator.in, ordersIdSet);
            builder.add("state", SpecificationOperator.Operator.eq, OrdersReturnDict.STATE_AGREE.getKey());
            List<OrdersReturn> list = super.findAll(builder.generateSpecification());
            returnSum = CollectionUtils.isEmpty(list) ? 0 : list.stream().mapToDouble(OrdersReturn::getPrice).sum();
        }
        return returnSum;
    }
}
