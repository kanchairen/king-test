package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 商品dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/24
 */
@ApiModel(value = "ProductDto", description = "商品dto")
public class ProductDto {

    private Integer id;

    @ApiModelProperty(notes = "商品名称")
    private String name;

    @ApiModelProperty(notes = "商品规格")
    private String spec;

    @ApiModelProperty(notes = "商品价格")
    private double price;

    @ApiModelProperty(notes = "线上商品让利比")
    private double benefitRate;

    @ApiModelProperty(notes = "商品G米价格")
    private double wpointPrice;

    @ApiModelProperty(notes = "是否支持小米购买")
    private Boolean supportRPoint;

    @ApiModelProperty(notes = "可获G米，根据公式计算消费额 *（让利比/比例基数）* 100，取商品中第一个，每次修改保存商品时重新计算")
    private double getWPoint;

    @ApiModelProperty(notes = "商品重量")
    private Double weight;

    @ApiModelProperty(notes = "商品体积")
    private Double volume;

    @ApiModelProperty(notes = "商品库存")
    private int stock;

    @ApiModelProperty(notes = "总销量")
    private int totalSold;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime = new Date();

    @ApiModelProperty(notes = "缩略图")
    private Image previewImg;

    @ApiModelProperty(notes = "商品条形码")
    private String code;

    @ApiModelProperty(notes = "是否下线（默认0：否 1：是）")
    private Boolean offline;

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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBenefitRate() {
        return benefitRate;
    }

    public void setBenefitRate(double benefitRate) {
        this.benefitRate = benefitRate;
    }

    public double getGetWPoint() {
        return getWPoint;
    }

    public void setGetWPoint(double getWPoint) {
        this.getWPoint = getWPoint;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Image getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(Image previewImg) {
        this.previewImg = previewImg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }

    public Boolean getSupportRPoint() {
        return supportRPoint;
    }

    public void setSupportRPoint(Boolean supportRPoint) {
        this.supportRPoint = supportRPoint;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", spec='" + spec + '\'' +
                ", price=" + price +
                ", benefitRate=" + benefitRate +
                ", wpointPrice=" + wpointPrice +
                ", supportRPoint=" + supportRPoint +
                ", getWPoint=" + getWPoint +
                ", weight=" + weight +
                ", volume=" + volume +
                ", stock=" + stock +
                ", totalSold=" + totalSold +
                ", createTime=" + createTime +
                ", previewImg=" + previewImg +
                ", code='" + code + '\'' +
                ", offline=" + offline +
                '}';
    }
}
