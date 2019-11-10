package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 店铺头部显示dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@ApiModel(value = "ShopHeadDto", description = "店铺头部显示dto")
public class ShopHeadDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "店铺名称")
    private String name;

    @ApiModelProperty(notes = "店铺地址")
    private String address;

    @ApiModelProperty(notes = "店铺手机")
    private String contactPhone;

    @ApiModelProperty(notes = "店铺头像")
    private Image logoImg;

    @ApiModelProperty(notes = "开通G米店铺 (默认0：否 1：是)")
    private Boolean openWPoint;

    @ApiModelProperty(notes = "开通小米店铺（默认0：否 1：是）")
    private Boolean openRPoint;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Image getLogoImg() {
        return logoImg;
    }

    public void setLogoImg(Image logoImg) {
        this.logoImg = logoImg;
    }

    public Boolean getOpenWPoint() {
        return openWPoint;
    }

    public void setOpenWPoint(Boolean openWPoint) {
        this.openWPoint = openWPoint;
    }

    public Boolean getOpenRPoint() {
        return openRPoint;
    }

    public void setOpenRPoint(Boolean openRPoint) {
        this.openRPoint = openRPoint;
    }

    @Override
    public String toString() {
        return "ShopHeadDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", logoImg=" + logoImg +
                ", openWPoint=" + openWPoint +
                ", openRPoint=" + openRPoint +
                '}';
    }
}
