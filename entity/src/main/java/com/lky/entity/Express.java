package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 系统快递公司
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_express")
@ApiModel(value = "Express", description = "系统快递公司")
public class Express extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "快递公司名称")
    @Column(length = 32, nullable = false)
    private String name;

    @ApiModelProperty(notes = "快递公司代号")
    @Column(length = 32, nullable = false)
    private String code;
}
