package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理商G米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_wpoint_record")
@ApiModel(value = "AWPointRecord", description = "代理商G米变动记录")
public class AWPointRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(value = "代理商id")
    @Column(nullable = false, name = "a_user_id")
    private Integer aUserId;

    @ApiModelProperty(notes = "G米变动类型（收入：收益，支出：转换为大米、转换为小米",
            allowableValues = "income,convert_balance,convert_rpoint")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "变动之后的G米数")
    @Column(name = "current_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double currentWPoint;

    @ApiModelProperty(notes = "变动的G米数（正为加，负为减）")
    @Column(name = "change_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double changeWPoint;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
