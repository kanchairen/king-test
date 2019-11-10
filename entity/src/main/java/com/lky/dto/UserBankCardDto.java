package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户银行卡dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
@ApiModel(value = "UserBankCardDto", description = "用户银行卡dto")
public class UserBankCardDto {

    private Integer id;

    @ApiModelProperty(notes = "手机号")
    private String mobile;

    @ApiModelProperty(notes = "银行卡号")
    private String bankcard;

    @ApiModelProperty(notes = "身份证号")
    private String cardNo;

    @ApiModelProperty(notes = "真实姓名")
    private String realName;

    @ApiModelProperty(notes = "银行编号")
    private String bankNum;

    @ApiModelProperty(notes = "银行中文名称")
    private String bankName;

    @ApiModelProperty(notes = "开户地区")
    private String bankArea;

    @ApiModelProperty(notes = "开户支行")
    private String branchName;

    @ApiModelProperty(notes = "卡号前缀号码")
    private String cardPrefixNum;

    @ApiModelProperty(notes = "卡名称")
    private String cardName;

    @ApiModelProperty(notes = "卡类型")
    private String cardType;

    @ApiModelProperty(notes = "卡号前缀长度")
    private String cardPrefixLength;

    @ApiModelProperty(notes = "卡号长度")
    private String cardLength;

    @ApiModelProperty(notes = "是否采用luhn算法")
    private Boolean isLuhn;

    @ApiModelProperty(notes = "是否是信用卡, 1：借记卡 2：贷记卡")
    private Integer isCreditCard;

    @ApiModelProperty(notes = "银行官网链接")
    private String bankUrl;

    @ApiModelProperty(notes = "银行英文名称")
    private String enBankName;

    @ApiModelProperty(notes = "缩写")
    private String abbreviation;

    @ApiModelProperty(notes = "银行image图片链接")
    private String bankImage;

    @ApiModelProperty(notes = "银行服务电话")
    private String servicePhone;

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

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
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

    public String getCardPrefixNum() {
        return cardPrefixNum;
    }

    public void setCardPrefixNum(String cardPrefixNum) {
        this.cardPrefixNum = cardPrefixNum;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardPrefixLength() {
        return cardPrefixLength;
    }

    public void setCardPrefixLength(String cardPrefixLength) {
        this.cardPrefixLength = cardPrefixLength;
    }

    public String getCardLength() {
        return cardLength;
    }

    public void setCardLength(String cardLength) {
        this.cardLength = cardLength;
    }

    public Boolean getLuhn() {
        return isLuhn;
    }

    public void setLuhn(Boolean luhn) {
        isLuhn = luhn;
    }

    public Integer getIsCreditCard() {
        return isCreditCard;
    }

    public void setIsCreditCard(Integer isCreditCard) {
        this.isCreditCard = isCreditCard;
    }

    public String getBankUrl() {
        return bankUrl;
    }

    public void setBankUrl(String bankUrl) {
        this.bankUrl = bankUrl;
    }

    public String getEnBankName() {
        return enBankName;
    }

    public void setEnBankName(String enBankName) {
        this.enBankName = enBankName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getBankImage() {
        return bankImage;
    }

    public void setBankImage(String bankImage) {
        this.bankImage = bankImage;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }
}
