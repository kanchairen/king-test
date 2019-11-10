package com.lky.dto;

import com.lky.entity.Category;
import com.lky.entity.Image;
import com.lky.entity.ShopKind;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 商品组dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/24
 */
@Api(value = "ProductGroupDto", description = "商品组dto")
public class ProductGroupDto {

    private Integer id;

    @ApiModelProperty(notes = "商品显示名称")
    private String name;

    @ApiModelProperty(notes = "商品显示价格")
    private double price;

    @ApiModelProperty(notes = "商品类目")
    private Category category;

    @ApiModelProperty(notes = "店铺内分类")
    private List<ShopKind> shopKindList;

    @ApiModelProperty(notes = "物流模板")
    private Integer freightTemplateId;

    @ApiModelProperty(notes = "商品列表")
    private List<ProductDto> productDtoList;

    @ApiModelProperty(notes = "商品显示照片列表")
    private List<Image> showImgList;

    @ApiModelProperty(notes = "商品详情描述")
    private String detail;

    @ApiModelProperty(notes = "是否支持小米购买")
    private Boolean supportRPoint;

    @ApiModelProperty(notes = "商品显示G米价格")
    private double wpointPrice;

    @ApiModelProperty(notes = "可获G米，根据公式计算消费额 *（让利比/比例基数）* 100，取商品中第一个，每次修改保存商品时重新计算")
    private double getWPoint;

    @ApiModelProperty(notes = "商品总销量")
    private int totalSold;

    @ApiModelProperty(notes = "商品月销量")
    private int monthSold;

    @ApiModelProperty(notes = "最近销量")
    private int recentSold;

    @ApiModelProperty(notes = "店铺")
    private ShopDto shopDto;

    @ApiModelProperty(notes = "评论数")
    private int commentNumber;

    @ApiModelProperty(notes = "综合评价")
    private Double evaluate;

    @ApiModelProperty(notes = "运费")
    private double freightPrice;

    @ApiModelProperty(notes = "审核状态")
    private String auditState;

    @ApiModelProperty(notes = "审核备注，未通过的理由")
    private String auditRemark;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ShopKind> getShopKindList() {
        return shopKindList;
    }

    public void setShopKindList(List<ShopKind> shopKindList) {
        this.shopKindList = shopKindList;
    }

    public Integer getFreightTemplateId() {
        return freightTemplateId;
    }

    public void setFreightTemplateId(Integer freightTemplateId) {
        this.freightTemplateId = freightTemplateId;
    }

    public List<ProductDto> getProductDtoList() {
        return productDtoList;
    }

    public void setProductDtoList(List<ProductDto> productDtoList) {
        this.productDtoList = productDtoList;
    }

    public List<Image> getShowImgList() {
        return showImgList;
    }

    public void setShowImgList(List<Image> showImgList) {
        this.showImgList = showImgList;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getGetWPoint() {
        return getWPoint;
    }

    public void setGetWPoint(double getWPoint) {
        this.getWPoint = getWPoint;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public int getMonthSold() {
        return monthSold;
    }

    public void setMonthSold(int monthSold) {
        this.monthSold = monthSold;
    }

    public int getRecentSold() {
        return recentSold;
    }

    public void setRecentSold(int recentSold) {
        this.recentSold = recentSold;
    }

    public ShopDto getShopDto() {
        return shopDto;
    }

    public void setShopDto(ShopDto shopDto) {
        this.shopDto = shopDto;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public Double getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(Double evaluate) {
        this.evaluate = evaluate;
    }

    public double getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(double freightPrice) {
        this.freightPrice = freightPrice;
    }

    public String getAuditState() {
        return auditState;
    }

    public void setAuditState(String auditState) {
        this.auditState = auditState;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getSupportRPoint() {
        return supportRPoint;
    }

    public void setSupportRPoint(Boolean supportRPoint) {
        this.supportRPoint = supportRPoint;
    }

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }

    @Override
    public String toString() {
        return "ProductGroupDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", shopKindList=" + shopKindList +
                ", freightTemplateId=" + freightTemplateId +
                ", productDtoList=" + productDtoList +
                ", showImgList=" + showImgList +
                ", detail='" + detail + '\'' +
                ", supportRPoint=" + supportRPoint +
                ", wpointPrice=" + wpointPrice +
                ", getWPoint=" + getWPoint +
                ", totalSold=" + totalSold +
                ", monthSold=" + monthSold +
                ", recentSold=" + recentSold +
                ", shopDto=" + shopDto +
                ", commentNumber=" + commentNumber +
                ", evaluate=" + evaluate +
                ", freightPrice=" + freightPrice +
                ", auditState='" + auditState + '\'' +
                ", auditRemark='" + auditRemark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
