package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 系统菜单表
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_menu")
@ApiModel(value = "SMenu", description = "系统菜单表")
public class SMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "父id")
    private Integer parentId;

    @ApiModelProperty(notes = "菜单名称")
    @Column(length = 64, nullable = false)
    private String name;

    @ApiModelProperty(notes = "类型，目录、菜单、按钮", allowableValues = "dir,menu,button")
    private String type;

    @ApiModelProperty(notes = "权限码，可以多个用逗号隔开", example = "sys:config:list,sys:config:info")
    private String perms;

    @ApiModelProperty(notes = "访问链接")
    private String url;

    @ApiModelProperty(notes = "图标")
    @OneToOne
    private Image iconImg;

    @ApiModelProperty(notes = "排序号")
    private Integer sortIndex;
}
