package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 系统角色表
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_role")
@ApiModel(value = "SRole", description = "系统角色表")
public class SRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "角色名称")
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @ApiModelProperty(notes = "角色代号")
    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @ApiModelProperty(notes = "角色描述")
    @Column(name = "description")
    private String description;
}
