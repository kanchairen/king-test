package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 店铺dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/20
 */

@ApiModel(value = "ShopDto", description = "修改店铺信息")
public class ShopDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "联系电话")
    private String contactPhone;

    @ApiModelProperty(notes = "接收通知手机号码")
    private String notifyPhone;

    @ApiModelProperty(notes = "联系QQ")
    private String contactQq;

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
    private Image shopLogoImg;

    @ApiModelProperty(notes = "店铺头图 (可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> shopBannerImgList;

    @ApiModelProperty(notes = "分享文字")
    private String shareText;

    @ApiModelProperty(notes = "分享图片")
    private Image shareImg;

    @ApiModelProperty(notes = "分享标题")
    private String shareTitle;

    @ApiModelProperty(notes = "营业执照（可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> shopLicenseImgList;

    @ApiModelProperty(notes = "近期订单数量")
    private Integer recentSumOrder;

    @ApiModelProperty(notes = "全部商品数量")
    private Integer sumProduct;

    @ApiModelProperty(notes = "距离 千米")
    private Double distance;

    @ApiModelProperty(notes = "开通店铺有效期")
    private Date openShopExpire;

    @ApiModelProperty(notes = "用户id")
    private Integer userId;

    @ApiModelProperty(notes = "是否显示banner（默认0：否 1：是）")
    private Boolean showBanner;

    @ApiModelProperty(notes = "是否显示店铺内商品分类（默认0：否 1：是）")
    private Boolean showKind;

    @ApiModelProperty(notes = "开通G米 (默认0：否 1：是)")
    private Boolean openWPoint;

    @ApiModelProperty(notes = "开通小米（默认0：否 1：是）")
    private Boolean openRPoint;

    @ApiModelProperty(notes = "店铺封停状态（默认0：否 1：是）")
    private Boolean locked;

    public String getNotifyPhone() {
        return notifyPhone;
    }

    public void setNotifyPhone(String notifyPhone) {
        this.notifyPhone = notifyPhone;
    }

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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public Image getShareImg() {
        return shareImg;
    }

    public void setShareImg(Image shareImg) {
        this.shareImg = shareImg;
    }

    public List<Image> getShopLicenseImgList() {
        return shopLicenseImgList;
    }

    public void setShopLicenseImgList(List<Image> shopLicenseImgList) {
        this.shopLicenseImgList = shopLicenseImgList;
    }

    public Integer getRecentSumOrder() {
        return recentSumOrder;
    }

    public void setRecentSumOrder(Integer recentSumOrder) {
        this.recentSumOrder = recentSumOrder;
    }

    public Integer getSumProduct() {
        return sumProduct;
    }

    public void setSumProduct(Integer sumProduct) {
        this.sumProduct = sumProduct;
    }

    public String getContactQq() {
        return contactQq;
    }

    public void setContactQq(String contactQq) {
        this.contactQq = contactQq;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Date getOpenShopExpire() {
        return openShopExpire;
    }

    public void setOpenShopExpire(Date openShopExpire) {
        this.openShopExpire = openShopExpire;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getShowBanner() {
        return showBanner;
    }

    public void setShowBanner(Boolean showBanner) {
        this.showBanner = showBanner;
    }

    public Boolean getShowKind() {
        return showKind;
    }

    public void setShowKind(Boolean showKind) {
        this.showKind = showKind;
    }

    public Boolean getOpenRPoint() {
        return openRPoint;
    }

    public void setOpenRPoint(Boolean openRPoint) {
        this.openRPoint = openRPoint;
    }

    public Boolean getOpenWPoint() {
        return openWPoint;
    }

    public void setOpenWPoint(Boolean openWPoint) {
        this.openWPoint = openWPoint;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "ShopDto{" +
                "id=" + id +
                ", contactPhone='" + contactPhone + '\'' +
                ", notifyPhone='" + notifyPhone + '\'' +
                ", contactQq='" + contactQq + '\'' +
                ", benefitRate=" + benefitRate +
                ", name='" + name + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", address='" + address + '\'' +
                ", shopLogoImg=" + shopLogoImg +
                ", shopBannerImgList=" + shopBannerImgList +
                ", shareText='" + shareText + '\'' +
                ", shareImg=" + shareImg +
                ", shareTitle='" + shareTitle + '\'' +
                ", shopLicenseImgList=" + shopLicenseImgList +
                ", recentSumOrder=" + recentSumOrder +
                ", sumProduct=" + sumProduct +
                ", distance=" + distance +
                ", openShopExpire=" + openShopExpire +
                ", userId=" + userId +
                ", showBanner=" + showBanner +
                ", showKind=" + showKind +
                ", openWPoint=" + openWPoint +
                ", openRPoint=" + openRPoint +
                ", locked=" + locked +
                '}';
    }
}
