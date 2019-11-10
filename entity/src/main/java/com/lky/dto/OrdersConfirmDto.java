package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 订单确认dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@ApiModel(value = "OrdersConfirmDto", description = "订单确认dto")
public class OrdersConfirmDto {

    @ApiModelProperty(notes = "店铺id")
    private Integer shopId;

    @ApiModelProperty(notes = "确认订单购物车")
    private List<ProductConfirmDto> productConfirmDtos;

    @ApiModelProperty(notes = "发货类型,快递现金", allowableValues = "delivery_cash")
    private String sendType;

    @ApiModelProperty(notes = "使用抵扣小米数量")
    private double rpointNum;

    @ApiModelProperty(notes = "订单备注")
    private String remark;

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public List<ProductConfirmDto> getProductConfirmDtos() {
        return productConfirmDtos;
    }

    public void setProductConfirmDtos(List<ProductConfirmDto> productConfirmDtos) {
        this.productConfirmDtos = productConfirmDtos;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getRpointNum() {
        return rpointNum;
    }

    public void setRpointNum(double rpointNum) {
        this.rpointNum = rpointNum;
    }
}
