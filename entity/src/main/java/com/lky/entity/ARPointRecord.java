package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理商小米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_rpoint_record")
@ApiModel(value = "ARPointRecord", description = "代理商小米变动记录")
public class ARPointRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(value = "代理商id")
    @Column(nullable = false, name = "a_user_id")
    private Integer aUserId;

    @ApiModelProperty(notes = "小米变动类型（收入：G米转换，支出：转出",
            allowableValues = "wpoint_convert,roll_out")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "变动之后的小米数")
    @Column(name = "current_rpoint", columnDefinition = "decimal(14,2)")
    private double currentRPoint;

    @ApiModelProperty(notes = "变动的小米数（正为加，负为减）")
    @Column(name = "change_rpoint", columnDefinition = "decimal(14,2)")
    private double changeRPoint;

    @ApiModelProperty(notes = "备注")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
