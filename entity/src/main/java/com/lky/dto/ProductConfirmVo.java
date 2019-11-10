package com.lky.dto;

import com.lky.entity.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 确认订单购物车vo
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@ApiModel(value = "CartConfirmVo", description = "确认订单购物车vo")
public class ProductConfirmVo {

    @ApiModelProperty(notes = "商品id")
    private Product product;

    @ApiModelProperty(notes = "商品数量")
    private Integer number;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

}
