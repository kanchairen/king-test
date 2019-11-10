package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 店铺简阅
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/5/8
 */
@ApiModel(value = "ShopSimpleDto", description = "店铺简阅信息")
public class ShopSimpleDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "线下让利比")
    private double benefitRate;

    @ApiModelProperty(notes = "店铺名称")
    private String name;

    @ApiModelProperty(notes = "经度")
    private String lat;

    @ApiModelProperty(notes = "维度")
    private String lng;

    @ApiModelProperty(notes = "店铺地址")
    private String address;

    @ApiModelProperty(notes = "店铺头像")
    private Integer logoImgId;

    @ApiModelProperty(notes = "近期订单数量")
    private Integer recentSumOrder;

    @ApiModelProperty(notes = "距离 千米")
    private Double distance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getBenefitRate() {
        return benefitRate;
    }

    public void setBenefitRate(double benefitRate) {
        this.benefitRate = benefitRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLogoImgId() {
        return logoImgId;
    }

    public void setLogoImgId(Integer logoImgId) {
        this.logoImgId = logoImgId;
    }

    public Integer getRecentSumOrder() {
        return recentSumOrder;
    }

    public void setRecentSumOrder(Integer recentSumOrder) {
        this.recentSumOrder = recentSumOrder;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "ShopSimpleDto{" +
                "id=" + id +
                ", benefitRate=" + benefitRate +
                ", name='" + name + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", address='" + address + '\'' +
                ", logoImgId=" + logoImgId +
                ", recentSumOrder=" + recentSumOrder +
                ", distance=" + distance +
                '}';
    }
}
