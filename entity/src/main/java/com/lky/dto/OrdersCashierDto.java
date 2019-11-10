package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 订单收银详情dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/1
 */
@ApiModel(value = "OrdersCashierDto", description = "订单收银详情dto")
public class OrdersCashierDto {

    @ApiModelProperty(notes = "应付金额")
    private double price;

    @ApiModelProperty(notes = "应付小米")
    private double rpointPrice;

    @ApiModelProperty(notes = "用户拥有小米")
    private double userRPoint;

    @ApiModelProperty(notes = "应付G米")
    private double wpointPrice;

    @ApiModelProperty(notes = "用户拥有G米")
    private double userWPoint;

    @ApiModelProperty(notes = "用户拥有大米")
    private double userBalance;

    @ApiModelProperty(notes = "扫码充值跳转条件Dto")
    private QrJumpDto qrJump;

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

    public double getUserRPoint() {
        return userRPoint;
    }

    public void setUserRPoint(double userRPoint) {
        this.userRPoint = userRPoint;
    }

    public double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(double userBalance) {
        this.userBalance = userBalance;
    }

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }

    public double getUserWPoint() {
        return userWPoint;
    }

    public void setUserWPoint(double userWPoint) {
        this.userWPoint = userWPoint;
    }

    public QrJumpDto getQrJump() {
        return qrJump;
    }

    public void setQrJump(QrJumpDto qrJump) {
        this.qrJump = qrJump;
    }
}
