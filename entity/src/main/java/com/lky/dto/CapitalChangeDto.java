package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 资金修改Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/6
 */
@ApiModel(value = "CapitalChangeDto", description = "资金修改" )
public class CapitalChangeDto {

    @ApiModelProperty(notes = "用户手机号码")
    private String mobile;

    @ApiModelProperty(notes = "增减类型")
    private String type;

    @ApiModelProperty(notes = "变化数值")
    private double number;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "CapitalChangeDto{" +
                "mobile='" + mobile + '\'' +
                ", type='" + type + '\'' +
                ", number=" + number +
                '}';
    }
}
