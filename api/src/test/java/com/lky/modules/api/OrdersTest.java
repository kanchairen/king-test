package com.lky.modules.api;

import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.IdWorker;
import com.lky.dto.OrdersCashierDto;
import com.lky.entity.*;
import com.lky.pay.wx.WxUtils;
import com.lky.service.*;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.code.OrderResCode.PAYMENT_RECORD_NOT_EXIST;
import static com.lky.enums.dict.OrdersReturnDict.STATE_AGREE;

/**
 * 订单测试
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/20
 */
public class OrdersTest extends BaseTest {

    @Inject
    private OrdersService ordersService;

    @Inject
    private OrdersItemService ordersItemService;

    @Inject
    private OrdersReturnService ordersReturnService;

    @Inject
    private ComputeService computeService;

    @Inject
    private PaymentRecordService paymentRecordService;

    @Inject
    private PaymentRecordService ordersPaymentService;

    @Inject
    private UserService userService;

    @Test
    public void receive() {
        Orders orders = ordersService.findById("932448262843916288");
        double amount = orders.getAmount();
        double price = orders.getPrice();
        double rPointPrice = orders.getRpointPrice();
        double consumerGiveWPoint = orders.getGiveWPoint();
        double merchantGiveWPoint = 0;
        double merchantGiveCash = 0;
        double merchantGiveRPoint = 0;

        //获取订单子项,计算订单获取的商家资产
        List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId(orders.getId());
        for (OrdersItem ordersItem : ordersItemList) {
            OrdersReturn ordersReturn = ordersReturnService.findByOrdersItemId(ordersItem.getId());
            if (ordersReturn != null && STATE_AGREE.compare(ordersItem.getReturnState()) && ordersReturn.getPrice() > 0) {
                //扣减退款的订单项
                consumerGiveWPoint -= ordersItem.getGiveWPoint();
                continue;
            }
            double benefitRate = ordersItem.getBenefitRate();
            double ordersItemPrice = ordersItem.getPrice();
            double ordersItemRPointPrice = ordersItem.getRpointPrice();
            int ordersItemNumber = ordersItem.getNumber();
            if (ordersItemPrice != 0 && !ordersItem.getUseRPoint()) {
                merchantGiveCash += computeService.merchantGiveCash(ordersItemPrice * ordersItemNumber, benefitRate);
            }
            if (ordersItemRPointPrice != 0) {
                merchantGiveRPoint += computeService.merchantGiveRPoint(ordersItemRPointPrice * ordersItemNumber, benefitRate);
            }
            merchantGiveWPoint += computeService.merchantGiveWPoint(ordersItemPrice * ordersItemNumber, benefitRate);
        }

        //使用小米支付运费

        merchantGiveCash += orders.getFreightPrice();

    }

    @Test
    public void returned() {
//        List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId("932444912580681728");
//        boolean returned = ordersItemList.stream()
//                .noneMatch(item -> STATE_APPLY.compare(item.getReturnState()));
//        System.out.println(returned);

        PaymentRecord paymentRecord = paymentRecordService.findByOrdersId("935839381866799104");
        AssertUtils.notNull(PAYMENT_RECORD_NOT_EXIST, paymentRecord);
        String transactionCode = paymentRecord.getTransactionCode();
        boolean refund = WxUtils.refund(transactionCode, 0.05, 0.03);
        System.out.println(refund);
    }

    @Test
    public void idTest(){
        String id = IdWorker.getOrderCode();
        System.out.println(id);
        System.out.println(id.length());

    }

    @Test
    public void cashierDetail() {
        User user = userService.findById(15);
        String id = "932503094959136768";
        String type = "orders";
        OrdersCashierDto ordersCashierDto = ordersPaymentService.cashierDetail(user, id, type);
        System.out.println("----------" + ordersCashierDto.getUserRPoint());
    }
}
