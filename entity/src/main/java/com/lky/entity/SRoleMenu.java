package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 系统角色菜单中间表
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_role_menu")
@ApiModel(value = "SRoleMenu", description = "系统角色菜单中间表")
public class SRoleMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "角色id")
    @Column(name = "s_role_id")
    private Integer roleId;

    @ApiModelProperty(notes = "菜单id")
    @Column(name = "s_menu_id")
    private Integer menuId;
}
