package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 店铺数据统计接收类
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/8
 */
public class ShopCountRowDto {

    @ApiModelProperty(notes = "x轴，日期/月份/分类")
    private String lineX;

    @ApiModelProperty(notes = "y轴，统计数据")
    private Object lineY;

    public ShopCountRowDto() {
    }

    public ShopCountRowDto(String lineX, Object lineY) {
        this.lineX = lineX;
        this.lineY = lineY;
    }

    public String getLineX() {
        return lineX;
    }

    public void setLineX(String lineX) {
        this.lineX = lineX;
    }

    public Object getLineY() {
        return lineY;
    }

    public void setLineY(Object lineY) {
        this.lineY = lineY;
    }

    @Override
    public String toString() {
        return "ShopCountRowDto{" +
                "lineX='" + lineX + '\'' +
                ", lineY=" + lineY +
                '}';
    }
}

