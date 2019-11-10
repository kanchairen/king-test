package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 查看赠送G米记录Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/6
 */
@ApiModel(value = "WPointRecordDto", description = "赠送G米记录Dto")
public class WPointRecordDto {

    @ApiModelProperty(notes = "数据库主键")
    private int id;

    @ApiModelProperty(notes = "用户昵称")
    private String nickname;

    @ApiModelProperty(notes = "手机号")
    private String mobile;

    @ApiModelProperty(notes = "是否加入到代理商收益中计算")
    private Boolean calculated;

    @ApiModelProperty(notes = "赠送数量")
    private double changeWPoint;

    @ApiModelProperty(notes = "赠送时间")
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getChangeWPoint() {
        return changeWPoint;
    }

    public void setChangeWPoint(double changeWPoint) {
        this.changeWPoint = changeWPoint;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getCalculated() {
        return calculated;
    }

    public void setCalculated(Boolean calculated) {
        this.calculated = calculated;
    }

    @Override
    public String toString() {
        return "WPointRecordDto{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", calculated=" + calculated +
                ", changeWPoint=" + changeWPoint +
                ", createTime=" + createTime +
                '}';
    }
}
