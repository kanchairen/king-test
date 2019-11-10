package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 订单确认vo
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@ApiModel(value = "OrdersConfirmVo", description = "订单确认vo")
public class OrdersConfirmVo {

    @ApiModelProperty(notes = "店铺信息")
    private ShopHeadDto shopHeadDto;

    @ApiModelProperty(notes = "商品列表vo")
    private List<ProductConfirmVo> productConfirmVos;

    @ApiModelProperty(notes = "发货类型", allowableValues = "delivery_cash,delivery_rpoint")
    private String sendType;

    @ApiModelProperty(notes = "物流费")
    private double freightPrice;

    @ApiModelProperty(notes = "使用抵扣小米数量")
    private double rpointNum;

    @ApiModelProperty(notes = "订单备注")
    private String remark;

    public ShopHeadDto getShopHeadDto() {
        return shopHeadDto;
    }

    public void setShopHeadDto(ShopHeadDto shopHeadDto) {
        this.shopHeadDto = shopHeadDto;
    }

    public List<ProductConfirmVo> getProductConfirmVos() {
        return productConfirmVos;
    }

    public void setProductConfirmVos(List<ProductConfirmVo> productConfirmVos) {
        this.productConfirmVos = productConfirmVos;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public double getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(double freightPrice) {
        this.freightPrice = freightPrice;
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
