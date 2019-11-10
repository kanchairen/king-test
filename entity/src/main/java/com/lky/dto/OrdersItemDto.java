package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 订单项dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/2
 */
@ApiModel(value = "OrdersItemDto", description = "订单项dto")
public class OrdersItemDto {

    @ApiModelProperty(value = "订单项id")
    private Integer id;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(notes = "商品名称")
    private String name;

    @ApiModelProperty(notes = "缩略图")
    private Image previewImg;

    @ApiModelProperty(notes = "商品规格（json字符串）")
    private String spec;

    @ApiModelProperty(notes = "商品数量")
    private int number;

    @ApiModelProperty(notes = "商品单价格")
    private double price;

    @ApiModelProperty(notes = "小米单价，大于零为小米支付")
    private double rpointPrice;

    @ApiModelProperty(notes = "消费获得的G米")
    private double giveWPoint;

    @ApiModelProperty(notes = "G米单价")
    private double wpointPrice;

    @ApiModelProperty(notes = "是否使用小米，默认否")
    private Boolean useRPoint;

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

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(Image previewImg) {
        this.previewImg = previewImg;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public double getGiveWPoint() {
        return giveWPoint;
    }

    public void setGiveWPoint(double giveWPoint) {
        this.giveWPoint = giveWPoint;
    }

    public Boolean getUseRPoint() {
        return useRPoint;
    }

    public void setUseRPoint(Boolean useRPoint) {
        this.useRPoint = useRPoint;
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
}
