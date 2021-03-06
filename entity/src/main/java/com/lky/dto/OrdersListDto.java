package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 线上订单列表dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/2
 */
@ApiModel(value = "OrdersListDto", description = "线上订单列表dto")
public class OrdersListDto {

    @ApiModelProperty(notes = "订单号")
    private String id;

    @ApiModelProperty(value = "店铺信息")
    private ShopHeadDto shopHeadDto;

    @ApiModelProperty(notes = "订单状态（待付款、待发货、待收货、已完成、已关闭）", allowableValues = "wait,send,receive,over,close")
    private String state;

    @ApiModelProperty(notes = "订单项列表")
    private List<OrdersItemDto> ordersItemDtoList;

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

    @ApiModelProperty(notes = "发货类型（快递配送（现金）、快递配送（小米））", allowableValues = "delivery_cash,delivery_rpoint")
    private String sendType;

    @ApiModelProperty(notes = "退款状态（申请、同意、拒绝）", allowableValues = "apply,agree,refuse")
    private String returnState;

    @ApiModelProperty(notes = "是否评论")
    private Boolean comment;

    @ApiModelProperty(notes = "是否追评")
    private Boolean appendComment;

    @ApiModelProperty(notes = "是否回复")
    private Boolean reply;

    @ApiModelProperty(notes = "消费获得的G米")
    private double giveWPoint;

    @ApiModelProperty(notes = "是否追评回复")
    private Boolean appendReply;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ShopHeadDto getShopHeadDto() {
        return shopHeadDto;
    }

    public void setShopHeadDto(ShopHeadDto shopHeadDto) {
        this.shopHeadDto = shopHeadDto;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<OrdersItemDto> getOrdersItemDtoList() {
        return ordersItemDtoList;
    }

    public void setOrdersItemDtoList(List<OrdersItemDto> ordersItemDtoList) {
        this.ordersItemDtoList = ordersItemDtoList;
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

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }

    public double getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(double freightPrice) {
        this.freightPrice = freightPrice;
    }

    public double getRpointFreightPrice() {
        return rpointFreightPrice;
    }

    public void setRpointFreightPrice(double rpointFreightPrice) {
        this.rpointFreightPrice = rpointFreightPrice;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
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

    public double getGiveWPoint() {
        return giveWPoint;
    }

    public void setGiveWPoint(double giveWPoint) {
        this.giveWPoint = giveWPoint;
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
}
