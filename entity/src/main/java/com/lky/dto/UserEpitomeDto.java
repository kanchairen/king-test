package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 用户财务信息
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/7
 */
public class UserEpitomeDto {

    @ApiModelProperty(notes = "用户ID")
    private int id;

    @ApiModelProperty(notes = "真实姓名")
    private String realName;

    @ApiModelProperty(notes = "手机号")
    private String mobile;

    @ApiModelProperty(notes = "用户角色")
    private String roleType;

    @ApiModelProperty(notes = "可用大米")
    private double balance;

    @ApiModelProperty(notes = "小米个数")
    private double rpoint;

    @ApiModelProperty(notes = "G米个数")
    private double wpoint;

    @ApiModelProperty(notes = "冻结G米数量")
    private double lockWpoint;

    @ApiModelProperty(notes = "余粮公社")
    private double surplusGrain;

    @ApiModelProperty(notes = "商家小米数量（卖出去商品获得的小米）")
    private double merchantRpoint;

    @ApiModelProperty(notes = "商家G米数量（成交额*让利比*2得来的G米）在转化成大米时，100%转化")
    private double merchantWpoint;

    @ApiModelProperty(notes = "商家冻结的G米数量（来源成为商家冻结的、平台冻结的）")
    private double merchantLockWpoint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getRpoint() {
        return rpoint;
    }

    public void setRpoint(double rpoint) {
        this.rpoint = rpoint;
    }

    public double getWpoint() {
        return wpoint;
    }

    public void setWpoint(double wpoint) {
        this.wpoint = wpoint;
    }

    public double getLockWpoint() {
        return lockWpoint;
    }

    public void setLockWpoint(double lockWpoint) {
        this.lockWpoint = lockWpoint;
    }

    public double getMerchantRpoint() {
        return merchantRpoint;
    }

    public void setMerchantRpoint(double merchantRpoint) {
        this.merchantRpoint = merchantRpoint;
    }

    public double getMerchantWpoint() {
        return merchantWpoint;
    }

    public void setMerchantWpoint(double merchantWpoint) {
        this.merchantWpoint = merchantWpoint;
    }

    public double getMerchantLockWpoint() {
        return merchantLockWpoint;
    }

    public void setMerchantLockWpoint(double merchantLockWpoint) {
        this.merchantLockWpoint = merchantLockWpoint;
    }

    public double getSurplusGrain() {
        return surplusGrain;
    }

    public void setSurplusGrain(double surplusGrain) {
        this.surplusGrain = surplusGrain;
    }
}
