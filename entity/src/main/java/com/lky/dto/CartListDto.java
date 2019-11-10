package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 购物车dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/25
 */
@ApiModel(value = "CartListDto", description = "购物车dto")
public class CartListDto {

    @ApiModelProperty(notes = "店铺id")
    private Integer shopId;

    @ApiModelProperty(notes = "店铺名称")
    private String shopName;

    @ApiModelProperty(notes = "商品列表")
    private List<CartDto> cartList;

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public List<CartDto> getCartList() {
        return cartList;
    }

    public void setCartList(List<CartDto> cartList) {
        this.cartList = cartList;
    }

    @Override
    public String toString() {
        return "CartListDto{" +
                "shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                ", cartList=" + cartList +
                '}';
    }
}
