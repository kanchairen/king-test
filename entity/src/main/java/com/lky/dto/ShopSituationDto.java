package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 店铺概况
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/13
 */
@ApiModel(value = "SituationDto", description = "店铺概况")
public class ShopSituationDto {

    @ApiModelProperty(notes = "可用大米")
    private double balance;

    @ApiModelProperty(notes = "保证金（冻结大米，默认1000）")
    private double cashDeposit;

    @ApiModelProperty(notes = "商家小米数量（卖出去商品获得的小米）")
    private double merchantRPoint;

    @ApiModelProperty(notes = "开通店铺有效期")
    private Date openShopExpire;

    @ApiModelProperty(notes = "待付款订单数量")
    private int waitOrders;

    @ApiModelProperty(notes = "待发货订单数量")
    private int sendOrders;

    @ApiModelProperty(notes = "待收货订单数量")
    private int receiveOrders;

    @ApiModelProperty(notes = "今日新增订单数量")
    private int todayOrders;

    @ApiModelProperty(notes = "开通店铺费用")
    private double openShopFee;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getCashDeposit() {
        return cashDeposit;
    }

    public void setCashDeposit(double cashDeposit) {
        this.cashDeposit = cashDeposit;
    }

    public double getMerchantRPoint() {
        return merchantRPoint;
    }

    public void setMerchantRPoint(double merchantRPoint) {
        this.merchantRPoint = merchantRPoint;
    }

    public Date getOpenShopExpire() {
        return openShopExpire;
    }

    public void setOpenShopExpire(Date openShopExpire) {
        this.openShopExpire = openShopExpire;
    }

    public int getWaitOrders() {
        return waitOrders;
    }

    public void setWaitOrders(int waitOrders) {
        this.waitOrders = waitOrders;
    }

    public int getSendOrders() {
        return sendOrders;
    }

    public void setSendOrders(int sendOrders) {
        this.sendOrders = sendOrders;
    }

    public int getReceiveOrders() {
        return receiveOrders;
    }

    public void setReceiveOrders(int receiveOrders) {
        this.receiveOrders = receiveOrders;
    }

    public int getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(int todayOrders) {
        this.todayOrders = todayOrders;
    }

    public double getOpenShopFee() {
        return openShopFee;
    }

    public void setOpenShopFee(double openShopFee) {
        this.openShopFee = openShopFee;
    }

    @Override
    public String toString() {
        return "ShopSituationDto{" +
                "balance=" + balance +
                ", cashDeposit=" + cashDeposit +
                ", merchantRPoint=" + merchantRPoint +
                ", openShopExpire=" + openShopExpire +
                ", waitOrders=" + waitOrders +
                ", sendOrders=" + sendOrders +
                ", receiveOrders=" + receiveOrders +
                ", todayOrders=" + todayOrders +
                ", openShopFee=" + openShopFee +
                '}';
    }
}
