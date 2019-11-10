package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 商城用户资产Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/2
 */
public class UserAssetDto {

    @ApiModelProperty(notes = "数据库主键")
    private int id;

    @ApiModelProperty(notes = "可用大米")
    private double balance;

    @ApiModelProperty(notes = "保证金（冻结大米，默认1000）")
    private double cashDeposit;

    @ApiModelProperty(notes = "小米个数")
    private double rpoint;

    @ApiModelProperty(notes = "G米个数")
    private double wpoint;

    @ApiModelProperty(notes = "冻结G米数量")
    private double lockWPoint;

    @ApiModelProperty(notes = "商家小米数量（卖出去商品获得的小米）")
    private double merchantRPoint;

    @ApiModelProperty(notes = "商家G米数量（成交额*让利比*2得来的G米）在转化成大米时，100%转化")
    private double merchantWPoint;

    @ApiModelProperty(notes = "商家冻结的G米数量（来源成为商家冻结的、平台冻结的）")
    private double merchantLockWPoint;

    @ApiModelProperty(notes = "今日新增G米个数")
    private double wpointToday;

    @ApiModelProperty(notes = "今日新增小米个数")
    private double rpointToday;

    @ApiModelProperty(notes = "商家今日新增G米个数")
    private double merchantWPointToday;

    @ApiModelProperty(notes = "商家今日新增小米个数")
    private double merchantRPointToday;

    @ApiModelProperty(notes = "今日激励金额-G米转化成的金额")
    private double convertBalanceToday;

    @ApiModelProperty(notes = "今日激励小米-G米转化成的小米")
    private double convertRPointToday;

    @ApiModelProperty(notes = "商家今日激励金额-G米转化成的金额")
    private double merchantConvertBalanceToday;

    @ApiModelProperty(notes = "商家今日激励小米-G米转化成的小米")
    private double merchantConvertRPointToday;

    @ApiModelProperty(notes = "用户累计获得小米个数")
    private double rpointAddSum;

    @ApiModelProperty(notes = "商家累计获得小米个数")
    private double merchantRPointAddSum;

    @ApiModelProperty("商家可激励G米---今天能分配的G米 除开冻结的和今天新增的")
    private double merchantTransWPoint;

    @ApiModelProperty("用户可激励G米---今天能分配的G米 除开冻结的和今天新增的")
    private double userTransWPoint;

    @ApiModelProperty(notes = "今天G米转化为小米数量")
    private double wpointConvertRPoint;

    @ApiModelProperty(notes = "是否是商家")
    private boolean merchant;

    @ApiModelProperty(notes = "余粮公社")
    private double surplusGrain;

    @ApiModelProperty(notes = "是否开通余粮公社")
    private boolean openSurplusGrain = Boolean.FALSE;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public double getWpointToday() {
        return wpointToday;
    }

    public void setWpointToday(double wpointToday) {
        this.wpointToday = wpointToday;
    }

    public double getRpointToday() {
        return rpointToday;
    }

    public void setRpointToday(double rpointToday) {
        this.rpointToday = rpointToday;
    }

    public double getMerchantWPointToday() {
        return merchantWPointToday;
    }

    public void setMerchantWPointToday(double merchantWPointToday) {
        this.merchantWPointToday = merchantWPointToday;
    }

    public double getMerchantRPointToday() {
        return merchantRPointToday;
    }

    public void setMerchantRPointToday(double merchantRPointToday) {
        this.merchantRPointToday = merchantRPointToday;
    }

    public double getRpointAddSum() {
        return rpointAddSum;
    }

    public void setRpointAddSum(double rpointAddSum) {
        this.rpointAddSum = rpointAddSum;
    }

    public double getMerchantRPointAddSum() {
        return merchantRPointAddSum;
    }

    public void setMerchantRPointAddSum(double merchantRPointAddSum) {
        this.merchantRPointAddSum = merchantRPointAddSum;
    }

    public double getWpointConvertRPoint() {
        return wpointConvertRPoint;
    }

    public void setWpointConvertRPoint(double wpointConvertRPoint) {
        this.wpointConvertRPoint = wpointConvertRPoint;
    }

    public double getConvertBalanceToday() {
        return convertBalanceToday;
    }

    public void setConvertBalanceToday(double convertBalanceToday) {
        this.convertBalanceToday = convertBalanceToday;
    }

    public double getConvertRPointToday() {
        return convertRPointToday;
    }

    public void setConvertRPointToday(double convertRPointToday) {
        this.convertRPointToday = convertRPointToday;
    }

    public double getMerchantConvertBalanceToday() {
        return merchantConvertBalanceToday;
    }

    public void setMerchantConvertBalanceToday(double merchantConvertBalanceToday) {
        this.merchantConvertBalanceToday = merchantConvertBalanceToday;
    }

    public double getMerchantConvertRPointToday() {
        return merchantConvertRPointToday;
    }

    public void setMerchantConvertRPointToday(double merchantConvertRPointToday) {
        this.merchantConvertRPointToday = merchantConvertRPointToday;
    }

    public double getMerchantTransWPoint() {
        return merchantTransWPoint;
    }

    public void setMerchantTransWPoint(double merchantTransWPoint) {
        this.merchantTransWPoint = merchantTransWPoint;
    }

    public double getUserTransWPoint() {
        return userTransWPoint;
    }

    public void setUserTransWPoint(double userTransWPoint) {
        this.userTransWPoint = userTransWPoint;
    }

    public boolean isMerchant() {
        return merchant;
    }

    public void setMerchant(boolean merchant) {
        this.merchant = merchant;
    }

    public double getSurplusGrain() {
        return surplusGrain;
    }

    public void setSurplusGrain(double surplusGrain) {
        this.surplusGrain = surplusGrain;
    }

    public boolean isOpenSurplusGrain() {
        return openSurplusGrain;
    }

    public void setOpenSurplusGrain(boolean openSurplusGrain) {
        this.openSurplusGrain = openSurplusGrain;
    }

    @Override
    public String toString() {
        return "UserAssetDto{" +
                "id=" + id +
                ", balance=" + balance +
                ", cashDeposit=" + cashDeposit +
                ", rpoint=" + rpoint +
                ", wpoint=" + wpoint +
                ", lockWPoint=" + lockWPoint +
                ", merchantRPoint=" + merchantRPoint +
                ", merchantWPoint=" + merchantWPoint +
                ", merchantLockWPoint=" + merchantLockWPoint +
                ", wpointToday=" + wpointToday +
                ", rpointToday=" + rpointToday +
                ", merchantWPointToday=" + merchantWPointToday +
                ", merchantRPointToday=" + merchantRPointToday +
                ", convertBalanceToday=" + convertBalanceToday +
                ", convertRPointToday=" + convertRPointToday +
                ", merchantConvertBalanceToday=" + merchantConvertBalanceToday +
                ", merchantConvertRPointToday=" + merchantConvertRPointToday +
                ", rpointAddSum=" + rpointAddSum +
                ", merchantRPointAddSum=" + merchantRPointAddSum +
                ", merchantTransWPoint=" + merchantTransWPoint +
                ", userTransWPoint=" + userTransWPoint +
                ", wpointConvertRPoint=" + wpointConvertRPoint +
                ", merchant=" + merchant +
                ", surplusGrain=" + surplusGrain +
                ", openSurplusGrain=" + openSurplusGrain +
                '}';
    }
}
