package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 排行榜Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/13
 */
public class RankDto {

    @ApiModelProperty(notes = "数据库id")
    private int id;

    @ApiModelProperty(notes = "排行榜类型 （G米排行榜、商家销量排行榜）", allowableValues = "wpoint,sold")
    private String type;

    @ApiModelProperty(notes = "用户电话")
    private String userName;

    @ApiModelProperty(notes = "G米个数/商家销量（每天更新一次）")
    private double num;

    @ApiModelProperty(notes = "商家名称")
    private String shopName;

    @ApiModelProperty(notes = "用户id/shopId")
    private int targetId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "RankDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", userName='" + userName + '\'' +
                ", num=" + num +
                ", shopName='" + shopName + '\'' +
                ", targetId=" + targetId +
                '}';
    }
}
