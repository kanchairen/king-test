package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * app版本
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_app_version")
@ApiModel(value = "AppVersion", description = "APP版本")
public class AppVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "版本号（GNU版本格式：主版本号.次版本号.修订号，主版本号：当你做了不兼容的 API 修改，次版本号：当你做了向下兼容的功能性新增，修订号：当你做了向下兼容的问题修正）")
    @Column(nullable = false, length = 20)
    private String version;

    @ApiModelProperty(notes = "更新编号")
    @Column(nullable = false)
    private Integer updateVersion;

    @ApiModelProperty(notes = "版本名")
    @Column(length = 20)
    private String name;

    @ApiModelProperty(notes = "url")
    @Column(nullable = false)
    private String url;

    @ApiModelProperty(notes = "设备类型（android，ios）", allowableValues = "android,ios")
    @Column(nullable = false, length = 32)
    private String deviceType;

    @ApiModelProperty(notes = "市场渠道（91助手、应用宝、百度助手、App Store等）")
    @Column(length = 32)
    private String marketChannel;

    @ApiModelProperty(notes = "版本说明")
    private String description;

    @ApiModelProperty(notes = "是否强制更新（0：否 1：是 ）")
    @Column(nullable = false)
    private Boolean forcedUpdate = Boolean.FALSE;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
