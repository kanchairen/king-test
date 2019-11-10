package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户收藏dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@ApiModel(value = "UserCollectDto", description = "用户收藏dto")
public class UserCollectDto {

    private Integer id;

    @ApiModelProperty(notes = "店铺")
    private ShopHeadDto shop;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ShopHeadDto getShop() {
        return shop;
    }

    public void setShop(ShopHeadDto shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {
        return "UserCollectDto{" +
                "id=" + id +
                ", shop=" + shop +
                '}';
    }
}
