package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 用户信息
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/16
 */
@ApiModel(value = "UserInfoDto", description = "用户信息dto")
public class UserInfoDto {

    @ApiModelProperty("数据库主键")
    private int id;

    @ApiModelProperty(notes = "头像id")
    private Image avatarImage;

    @ApiModelProperty(notes = "真实姓名")
    private String nickname;

    @ApiModelProperty(notes = "手机号")
    private String mobile;

    @ApiModelProperty(notes = "邮箱")
    private String email;

    @ApiModelProperty(notes = "是否冻结（默认0：不冻结，1：冻结）")
    private Boolean locked = Boolean.FALSE;

    @ApiModelProperty(notes = "用户性别")
    private String sex;

    @ApiModelProperty(notes = "推荐码")
    private String recommendCode;

    @ApiModelProperty(notes = "用户所在地区")
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Image getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(Image avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecommendCode() {
        return recommendCode;
    }

    public void setRecommendCode(String recommendCode) {
        this.recommendCode = recommendCode;
    }

    @Override
    public String toString() {
        return "UserInfoDto{" +
                "id=" + id +
                ", avatarImage=" + avatarImage +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", locked=" + locked +
                ", sex='" + sex + '\'' +
                ", recommendCode='" + recommendCode + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
