package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 银行卡dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@ApiModel(value = "BankCardDto", description = "银行卡dto")
public class BankCardDto {

    @ApiModelProperty(notes = "银行中文名称")
    private String bankName;

    @ApiModelProperty(notes = "开户地区")
    private String bankArea;

    @ApiModelProperty(notes = "开户支行")
    private String branchName;

    @ApiModelProperty(notes = "手机号")
    private String mobile;

    @ApiModelProperty(notes = "银行卡号")
    private String bankcard;

    @ApiModelProperty(notes = "身份证号")
    private String cardNo;

    @ApiModelProperty(notes = "真实姓名")
    private String realName;

    @ApiModelProperty(notes = "短信验证码")
    private String code;

    @ApiModelProperty(notes = "支付密码")
    private String payPwd;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBankcard() {
        return bankcard;
    }

    public void setBankcard(String bankcard) {
        this.bankcard = bankcard;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }

    @Override
    public String toString() {
        return "BankCardDto{" +
                "bankName='" + bankName + '\'' +
                ", bankArea='" + bankArea + '\'' +
                ", branchName='" + branchName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", bankcard='" + bankcard + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", realName='" + realName + '\'' +
                ", code='" + code + '\'' +
                ", payPwd='" + payPwd + '\'' +
                '}';
    }
}
