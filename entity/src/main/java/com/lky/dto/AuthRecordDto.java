package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 实名认证申请记录Dto
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-8
 */
@ApiModel(value = "AuthRecordDto", description = "实名认证申请记录Dto")
public class AuthRecordDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "app用户id")
    private Integer userId;

    @ApiModelProperty(notes = "真是姓名")
    private String realName;

    @ApiModelProperty(notes = "身份证号")
    private String cardNumber;

    @ApiModelProperty(notes = "实名认证图 (可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> authImgList;

    @ApiModelProperty(notes = "申请，同意，拒绝", allowableValues = "apply,agree,refuse")
    private String state;

    @ApiModelProperty(notes = "申请备注")
    private String remark;

    @ApiModelProperty(notes = "审核备注（拒绝理由）")
    private String auditRemark;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public List<Image> getAuthImgList() {
        return authImgList;
    }

    public void setAuthImgList(List<Image> authImgList) {
        this.authImgList = authImgList;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    @Override
    public String toString() {
        return "AuthRecordDto{" +
                "id=" + id +
                ", user=" + userId +
                ", realName='" + realName + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", authImgList=" + authImgList +
                ", state='" + state + '\'' +
                ", remark='" + remark + '\'' +
                ", auditRemark='" + auditRemark + '\'' +
                '}';
    }
}
