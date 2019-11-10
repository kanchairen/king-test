package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 店铺内分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@ApiModel(value = "ShopKindDto", description = "店铺内分类")
public class ShopKindDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "店铺内分类名称")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ShopKindDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
