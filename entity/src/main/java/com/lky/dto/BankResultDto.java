package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 校验四要素返回的银行信息
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
public class BankResultDto {

    @ApiModelProperty(notes = "银行编号")
    private String banknum;

    @ApiModelProperty(notes = "银行中文名称")
    private String bankname;

    @ApiModelProperty(notes = "卡号前缀号码")
    private String cardprefixnum;

    @ApiModelProperty(notes = "卡名称")
    private String cardname;

    @ApiModelProperty(notes = "卡类型")
    private String cardtype;

    @ApiModelProperty(notes = "卡号前缀长度")
    private String cardprefixlength;

    @ApiModelProperty(notes = "卡号长度")
    private String cardlength;

    @ApiModelProperty(notes = "是否采用luhn算法")
    private Boolean isLuhn = Boolean.TRUE;

    @ApiModelProperty(notes = "是否是信用卡, 1：借记卡 2：贷记卡")
    private Integer iscreditcard;

    @ApiModelProperty(notes = "银行官网链接")
    private String bankurl;

    @ApiModelProperty(notes = "银行英文名称")
    private String enbankname;

    @ApiModelProperty(notes = "缩写")
    private String abbreviation;

    @ApiModelProperty(notes = "银行image图片链接")
    private String bankimage;

    @ApiModelProperty(notes = "银行服务电话")
    private String servicephone;

    public String getBanknum() {
        return banknum;
    }

    public void setBanknum(String banknum) {
        this.banknum = banknum;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getCardprefixnum() {
        return cardprefixnum;
    }

    public void setCardprefixnum(String cardprefixnum) {
        this.cardprefixnum = cardprefixnum;
    }

    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getCardprefixlength() {
        return cardprefixlength;
    }

    public void setCardprefixlength(String cardprefixlength) {
        this.cardprefixlength = cardprefixlength;
    }

    public String getCardlength() {
        return cardlength;
    }

    public void setCardlength(String cardlength) {
        this.cardlength = cardlength;
    }

    public Boolean getLuhn() {
        return isLuhn;
    }

    public void setLuhn(Boolean luhn) {
        isLuhn = luhn;
    }

    public Integer getIscreditcard() {
        return iscreditcard;
    }

    public void setIscreditcard(Integer iscreditcard) {
        this.iscreditcard = iscreditcard;
    }

    public String getBankurl() {
        return bankurl;
    }

    public void setBankurl(String bankurl) {
        this.bankurl = bankurl;
    }

    public String getEnbankname() {
        return enbankname;
    }

    public void setEnbankname(String enbankname) {
        this.enbankname = enbankname;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getBankimage() {
        return bankimage;
    }

    public void setBankimage(String bankimage) {
        this.bankimage = bankimage;
    }

    public String getServicephone() {
        return servicephone;
    }

    public void setServicephone(String servicephone) {
        this.servicephone = servicephone;
    }

    @Override
    public String toString() {
        return "BankResultDto{" +
                "banknum='" + banknum + '\'' +
                ", bankname='" + bankname + '\'' +
                ", cardprefixnum='" + cardprefixnum + '\'' +
                ", cardname='" + cardname + '\'' +
                ", cardtype='" + cardtype + '\'' +
                ", cardprefixlength='" + cardprefixlength + '\'' +
                ", cardlength='" + cardlength + '\'' +
                ", isLuhn=" + isLuhn +
                ", iscreditcard=" + iscreditcard +
                ", bankurl='" + bankurl + '\'' +
                ", enbankname='" + enbankname + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", bankimage='" + bankimage + '\'' +
                ", servicephone='" + servicephone + '\'' +
                '}';
    }
}
