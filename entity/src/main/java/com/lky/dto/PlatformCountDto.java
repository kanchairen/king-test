package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 平台财务Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/9
 */
@ApiModel(value = "PlatformCountDto", description = "平台财务Dto")
public class PlatformCountDto {

    @ApiModelProperty(notes = "总大米")
    private double balance;

    @ApiModelProperty(notes = "总小米个数")
    private double rpoint;

    @ApiModelProperty(notes = "总用户G米数")
    private double wpoint;

    @ApiModelProperty(notes = "总用户存量G米数")
    private double lockWPoint;

    @ApiModelProperty(notes = "总余粮公社数")
    private double surplusGrain;

    @ApiModelProperty(notes = "总商家小米数")
    private double merchantRPoint;

    @ApiModelProperty(notes = "总商家G米数")
    private double merchantWPoint;

    @ApiModelProperty(notes = "总商家存量的G米数")
    private double merchantLockWPoint;

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

    public double getLockWPoint() {
        return lockWPoint;
    }

    public void setLockWPoint(double lockWPoint) {
        this.lockWPoint = lockWPoint;
    }

    public double getMerchantRPoint() {
        return merchantRPoint;
    }

    public void setMerchantRPoint(double merchantRPoint) {
        this.merchantRPoint = merchantRPoint;
    }

    public double getMerchantWPoint() {
        return merchantWPoint;
    }

    public void setMerchantWPoint(double merchantWPoint) {
        this.merchantWPoint = merchantWPoint;
    }

    public double getMerchantLockWPoint() {
        return merchantLockWPoint;
    }

    public void setMerchantLockWPoint(double merchantLockWPoint) {
        this.merchantLockWPoint = merchantLockWPoint;
    }

    public double getSurplusGrain() {
        return surplusGrain;
    }

    public void setSurplusGrain(double surplusGrain) {
        this.surplusGrain = surplusGrain;
    }

    @Override
    public String toString() {
        return "PlatformCountDto{" +
                "balance=" + balance +
                ", rpoint=" + rpoint +
                ", wpoint=" + wpoint +
                ", lockWPoint=" + lockWPoint +
                ", surplusGrain=" + surplusGrain +
                ", merchantRPoint=" + merchantRPoint +
                ", merchantWPoint=" + merchantWPoint +
                ", merchantLockWPoint=" + merchantLockWPoint +
                '}';
    }
}
