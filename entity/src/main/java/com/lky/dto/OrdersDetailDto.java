package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 订单详情dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@ApiModel(value = "OrdersDetailDto", description = "线上订单详情dto")
public class OrdersDetailDto {

    @ApiModelProperty(notes = "订单号")
    private String id;

    @ApiModelProperty(notes = "订单状态（待付款、待发货、待收货、已完成、已关闭）", allowableValues = "wait,send,receive,over,close")
    private String state;

    @ApiModelProperty(notes = "收货地址")
    private String receiveAddress;

    @ApiModelProperty(value = "店铺信息")
    private ShopHeadDto shopHeadDto;

    @ApiModelProperty(notes = "订单项列表")
    private List<OrdersItemDto> ordersItemDtoList;

    @ApiModelProperty(notes = "订单总金额")
    private double amount;

    @ApiModelProperty(notes = "需要支付的现金价格")
    private double price;

    @ApiModelProperty(notes = "需要支付的小米")
    private double rpointPrice;

    @ApiModelProperty(notes = "需要支付的G米")
    private double wpointPrice;

    @ApiModelProperty(notes = "需要现金支付的物流价格")
    private double freightPrice;

    @ApiModelProperty(notes = "需要小米支付的物流价格")
    private double rpointFreightPrice;

    @ApiModelProperty(notes = "消费获得的G米")
    private double giveWPoint;

    @ApiModelProperty(notes = "发货类型（快递配送（现金）、快递配送（小米））", allowableValues = "delivery_cash,delivery_rpoint")
    private String sendType;

    @ApiModelProperty(notes = "支付类型（小米、支付宝、微信、银联、线下现金）", allowableValues = "rpoint,alipay,wechat,unipay,cash")
    private String payType;

    @ApiModelProperty(notes = "订单备注")
    private String remark;

    @ApiModelProperty(notes = "退款状态（申请、同意、拒绝）", allowableValues = "apply,agree,refuse")
    private String returnState;

    @ApiModelProperty(notes = "是否评论")
    private Boolean comment;

    @ApiModelProperty(notes = "是否追评")
    private Boolean appendComment;

    @ApiModelProperty(notes = "是否回复")
    private Boolean reply;

    @ApiModelProperty(notes = "是否追评回复")
    private Boolean appendReply;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime = new Date();

    @ApiModelProperty(notes = "付款时间")
    private Date payTime;

    @ApiModelProperty(notes = "发货时间")
    private Date sendTime;

    @ApiModelProperty(notes = "成交时间")
    private Date overTime;

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public ShopHeadDto getShopHeadDto() {
        return shopHeadDto;
    }

    public void setShopHeadDto(ShopHeadDto shopHeadDto) {
        this.shopHeadDto = shopHeadDto;
    }

    public List<OrdersItemDto> getOrdersItemDtoList() {
        return ordersItemDtoList;
    }

    public void setOrdersItemDtoList(List<OrdersItemDto> ordersItemDtoList) {
        this.ordersItemDtoList = ordersItemDtoList;
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

    public double getRpointFreightPrice() {
        return rpointFreightPrice;
    }

    public void setRpointFreightPrice(double rpointFreightPrice) {
        this.rpointFreightPrice = rpointFreightPrice;
    }

    public double getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(double freightPrice) {
        this.freightPrice = freightPrice;
    }

    public double getGiveWPoint() {
        return giveWPoint;
    }

    public void setGiveWPoint(double giveWPoint) {
        this.giveWPoint = giveWPoint;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReturnState() {
        return returnState;
    }

    public void setReturnState(String returnState) {
        this.returnState = returnState;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public Boolean getAppendComment() {
        return appendComment;
    }

    public void setAppendComment(Boolean appendComment) {
        this.appendComment = appendComment;
    }

    public Boolean getReply() {
        return reply;
    }

    public void setReply(Boolean reply) {
        this.reply = reply;
    }

    public Boolean getAppendReply() {
        return appendReply;
    }

    public void setAppendReply(Boolean appendReply) {
        this.appendReply = appendReply;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getOverTime() {
        return overTime;
    }

    public void setOverTime(Date overTime) {
        this.overTime = overTime;
    }
}
