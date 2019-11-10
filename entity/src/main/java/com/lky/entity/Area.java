package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 系统地区
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_area")
@ApiModel(value = "Area", description = "系统地区")
public class Area extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "父id")
    private Integer parent;

    @ApiModelProperty(notes = "名称")
    @Column(length = 20)
    private String name;

    @ApiModelProperty(notes = "类型（地区、省、市、区）", allowableValues = "region,province,city,district")
    @Column(length = 32)
    private String type;
}
