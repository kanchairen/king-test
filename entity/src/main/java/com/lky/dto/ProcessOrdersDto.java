package com.lky.dto;

import com.lky.entity.Shop;
import com.lky.entity.User;

/**
 * 支付完成处理订单dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/1/15
 */
public class ProcessOrdersDto {

    /**
     * 订单类型，线上订单：online 线下订单：offline
     */
    public static final String ORDERS_TYPE_ONLINE = "online";
    public static final String ORDERS_TYPE_OFFLINE = "offline";

    //支付的用户
    private User user;

    //购买的店铺
    private Shop shop;

    //订单类型
    private String ordersType;

    //用户订单消费的总金额
    private double amount = 0;

    //用户订单消费的小米
    private double rpointPrice = 0;

    //用户订单消费的G米
    private double wpointPrice = 0;

    //用户消费获得的G米
    private double consumerGiveWPoint = 0;

    //商家通过用户消费获得的G米
    private double merchantGiveWPoint = 0;

    //商家直接获得的大米
    private double merchantGiveCash = 0;

    //商家直接获得的小米
    private double merchantGiveRPoint = 0;

    //商家直接获得的G米
    private double merchantGiveOrdersWPoint = 0;

    //需要等待商家确认收货
    private boolean needWaitReceive;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getOrdersType() {
        return ordersType;
    }

    public void setOrdersType(String ordersType) {
        this.ordersType = ordersType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRpointPrice() {
        return rpointPrice;
    }

    public void setRpointPrice(double rpointPrice) {
        this.rpointPrice = rpointPrice;
    }

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }

    public double getConsumerGiveWPoint() {
        return consumerGiveWPoint;
    }

    public void setConsumerGiveWPoint(double consumerGiveWPoint) {
        this.consumerGiveWPoint = consumerGiveWPoint;
    }

    public double getMerchantGiveWPoint() {
        return merchantGiveWPoint;
    }

    public void setMerchantGiveWPoint(double merchantGiveWPoint) {
        this.merchantGiveWPoint = merchantGiveWPoint;
    }

    public double getMerchantGiveCash() {
        return merchantGiveCash;
    }

    public void setMerchantGiveCash(double merchantGiveCash) {
        this.merchantGiveCash = merchantGiveCash;
    }

    public double getMerchantGiveRPoint() {
        return merchantGiveRPoint;
    }

    public void setMerchantGiveRPoint(double merchantGiveRPoint) {
        this.merchantGiveRPoint = merchantGiveRPoint;
    }

    public double getMerchantGiveOrdersWPoint() {
        return merchantGiveOrdersWPoint;
    }

    public void setMerchantGiveOrdersWPoint(double merchantGiveOrdersWPoint) {
        this.merchantGiveOrdersWPoint = merchantGiveOrdersWPoint;
    }

    public boolean isNeedWaitReceive() {
        return needWaitReceive;
    }

    public void setNeedWaitReceive(boolean needWaitReceive) {
        this.needWaitReceive = needWaitReceive;
    }
}
