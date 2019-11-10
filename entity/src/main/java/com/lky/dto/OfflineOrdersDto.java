package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 线下订单Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/2
 */
public class OfflineOrdersDto {

    @ApiModelProperty(notes = "数据库id")
    private String id;

    @ApiModelProperty(notes = "用户手机号")
    private String mobile;

    @ApiModelProperty(notes = "店铺名称")
    private String shopName;

    @ApiModelProperty(notes = "店铺id")
    private String shopId;

    @ApiModelProperty(notes = "订单状态（待付款、已付款）", allowableValues = "unpaid,paid")
    private String state;

    @ApiModelProperty(notes = "订单总金额")
    private double amount;

    @ApiModelProperty(notes = "需要支付的现金价格")
    private double price;

    @ApiModelProperty(notes = "需要支付的小米")
    private double rpointPrice;

    @ApiModelProperty(notes = "消费获得的G米")
    private double giveWPoint;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

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

    public double getGiveWPoint() {
        return giveWPoint;
    }

    public void setGiveWPoint(double giveWPoint) {
        this.giveWPoint = giveWPoint;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "OfflineOrdersDto{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", shopName='" + shopName + '\'' +
                ", shopId='" + shopId + '\'' +
                ", state='" + state + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", rpointPrice=" + rpointPrice +
                ", giveWPoint=" + giveWPoint +
                ", createTime=" + createTime +
                '}';
    }
}
