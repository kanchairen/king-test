package com.lky.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_user")
@ApiModel(value = "SUser", description = "系统用户")
public class SUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "父id")
    private Integer parentId;

    @ApiModelProperty(notes = "登录用户名")
    @Column(nullable = false, length = 32)
    private String username;

    @ApiModelProperty(notes = "登录密码")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ApiModelProperty(notes = "手机号码")
    @Column(length = 20)
    private String mobile;

    @ApiModelProperty(notes = "邮箱")
    @Column(length = 64)
    private String email;

    @ApiModelProperty(notes = "状态", allowableValues = "lock,active")
    @Column(length = 20, nullable = false)
    private String state;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
