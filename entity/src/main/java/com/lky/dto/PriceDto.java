package com.lky.dto;

import com.lky.entity.Orders;

import java.util.List;

/**
 * 价格dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/1/15
 */
public class PriceDto {

    //订单总金额
    private double amount = 0;

    //应付金额
    private double price = 0;

    //应付小米
    private double rpointPrice = 0;

    //应付G米
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

    //订单列表
    private List<Orders> ordersList;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
}
