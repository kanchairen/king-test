package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 实名认证记录
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-8
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "auth_record")
@ApiModel(value = "AuthRecord", description = "实名认证申请记录")
public class AuthRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "真是姓名")
    @Column(name = "real_name", length = 32)
    private String realName;

    @ApiModelProperty(notes = "身份证号")
    @Column(name = "card_number", length = 18)
    private String cardNumber;

    @ApiModelProperty(notes = "实名认证图 (可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "auth_img_ids")
    private String authImgIds;

    @ApiModelProperty(notes = "申请，同意，拒绝", allowableValues = "apply,agree,refuse")
    @Column(nullable = false)
    private String state;

    @ApiModelProperty(notes = "申请备注")
    private String remark;

    @ApiModelProperty(notes = "审核备注（拒绝理由）")
    private String auditRemark;

    @ApiModelProperty(notes = "审核时间")
    private Date auditTime;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
