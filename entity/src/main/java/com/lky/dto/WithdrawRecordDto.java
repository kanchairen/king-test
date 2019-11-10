package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 大米提现记录Dto
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-16
 */
@ApiModel(value = "WithdrawRecordDto", description = "大米提现记录Dto")
public class WithdrawRecordDto {

    private Integer id;

    @ApiModelProperty(notes = "手机号")
    private String mobile;

    @ApiModelProperty(notes = "真实姓名")
    private String realName;

    @ApiModelProperty(notes = "身份证号")
    private String cardNo;

    @ApiModelProperty(notes = "银行卡号")
    private String bankcard;

    @ApiModelProperty(notes = "卡名称")
    private String cardName;

    @ApiModelProperty(notes = "银行中文名称")
    private String bankName;

    @ApiModelProperty(notes = "开户地区")
    private String bankArea;

    @ApiModelProperty(notes = "开户支行")
    private String branchName;

    @ApiModelProperty(notes = "提现记录状态（提现申请、提现完成）", allowableValues = "apply,finish")
    private String state;

    @ApiModelProperty(notes = "提现之后的当前大米")
    private double currentBalance;

    @ApiModelProperty(notes = "提现的金额")
    private double withdrawAmount;

    @ApiModelProperty(notes = "提现手续费")
    private double withdrawFee;

    @ApiModelProperty(notes = "实际打款金额")
    private double realAmount;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "申请时间")
    private Date applyTime;

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

    @ApiModelProperty(notes = "完成时间")
    private Date finishTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankcard() {
        return bankcard;
    }

    public void setBankcard(String bankcard) {
        this.bankcard = bankcard;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankArea() {
        return bankArea;
    }

    public void setBankArea(String bankArea) {
        this.bankArea = bankArea;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(double withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public double getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(double withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public double getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(double realAmount) {
        this.realAmount = realAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
