package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 上/下级分销商信息
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/1
 */
public class UserRecommendDto {

    @ApiModelProperty("数据库主键")
    private int id;

    @ApiModelProperty(notes = "头像id")
    private Image avatarImage;

    @ApiModelProperty(notes = "名称")
    private String name;

    @ApiModelProperty(notes = "电话")
    private String mobile;

    @ApiModelProperty(notes = "成为分销商时间")
    private Date createTime;

    @ApiModelProperty(notes = "是否为商家")
    private Boolean merchant;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getMerchant() {
        return merchant;
    }

    public void setMerchant(Boolean merchant) {
        this.merchant = merchant;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "UserRecommendDto{" +
                "id=" + id +
                ", avatarImage=" + avatarImage +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", createTime=" + createTime +
                ", merchant=" + merchant +
                '}';
    }
}
