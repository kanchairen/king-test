package com.lky.dto;

import com.lky.entity.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 购物车列表
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/25
 */
@ApiModel(value = "CartDto", description = "购物车列表")
public class CartDto {

    private Integer id;

    @ApiModelProperty(notes = "app用户")
    private Integer userId;

    @ApiModelProperty(notes = "商品id")
    private Product product;

    @ApiModelProperty(notes = "商品数量")
    private int number;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "CartDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", product=" + product +
                ", number=" + number +
                '}';
    }
}
