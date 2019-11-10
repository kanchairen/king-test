package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 商家申请记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@ApiModel(value = "ApplyRecordDto", description = "商家申请记录")
public class ApplyRecordDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "app用户id")
    private Integer userId;

    @ApiModelProperty(notes = "所属行业")
    private IndustryParentDto industryParentDto;

    @ApiModelProperty(notes = "开通申请店铺应付费用", hidden = true)
    private double amount;

    @ApiModelProperty(notes = "申请开店总共的支付费用", hidden = true)
    private double sumPaidAmount;

    @ApiModelProperty(notes = "店铺名称")
    private String shopName;

    @ApiModelProperty(notes = "店铺地址详情")
    private String shopAddress;

    @ApiModelProperty(notes = "店铺联系电话")
    private String shopContactPhone;

    @ApiModelProperty(notes = "店铺头像")
    private Image shopLogoImg;

    @ApiModelProperty(notes = "店铺头图 (可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> shopBannerImgList;

    @ApiModelProperty(notes = "营业执照（可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> shopLicenseImgList;

    @ApiModelProperty(notes = "申请备注")
    private String remark;

    @ApiModelProperty(notes = "审核备注")
    private String auditRemark;

    @ApiModelProperty(notes = "经度")
    private String lat;

    @ApiModelProperty(notes = "维度")
    private String lng;

    @ApiModelProperty(notes = "申请状态，未支付，申请中，同意，拒绝", hidden = true, allowableValues = "unpaid,apply,agree,refuse")
    private String state;

    @ApiModelProperty(notes = "店铺id")
    private Integer shopId;

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


    public IndustryParentDto getIndustryParentDto() {
        return industryParentDto;
    }

    public void setIndustryParentDto(IndustryParentDto industryParentDto) {
        this.industryParentDto = industryParentDto;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSumPaidAmount() {
        return sumPaidAmount;
    }

    public void setSumPaidAmount(double sumPaidAmount) {
        this.sumPaidAmount = sumPaidAmount;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopContactPhone() {
        return shopContactPhone;
    }

    public void setShopContactPhone(String shopContactPhone) {
        this.shopContactPhone = shopContactPhone;
    }

    public Image getShopLogoImg() {
        return shopLogoImg;
    }

    public void setShopLogoImg(Image shopLogoImg) {
        this.shopLogoImg = shopLogoImg;
    }

    public List<Image> getShopBannerImgList() {
        return shopBannerImgList;
    }

    public void setShopBannerImgList(List<Image> shopBannerImgList) {
        this.shopBannerImgList = shopBannerImgList;
    }

    public List<Image> getShopLicenseImgList() {
        return shopLicenseImgList;
    }

    public void setShopLicenseImgList(List<Image> shopLicenseImgList) {
        this.shopLicenseImgList = shopLicenseImgList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "ApplyRecordDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", industryParentDto=" + industryParentDto +
                ", amount=" + amount +
                ", sumPaidAmount=" + sumPaidAmount +
                ", shopName='" + shopName + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", shopContactPhone='" + shopContactPhone + '\'' +
                ", shopLogoImg=" + shopLogoImg +
                ", shopBannerImgList=" + shopBannerImgList +
                ", shopLicenseImgList=" + shopLicenseImgList +
                ", remark='" + remark + '\'' +
                ", auditRemark='" + auditRemark + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", state='" + state + '\'' +
                ", shopId=" + shopId +
                '}';
    }
}
