package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 确认订单商品
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@ApiModel(value = "ProductConfirmDto", description = "确认订单商品")
public class ProductConfirmDto {

    @ApiModelProperty(notes = "商品id")
    private Integer productId;

    @ApiModelProperty(notes = "商品数量")
    private Integer number;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

}
