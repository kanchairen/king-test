package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理商收益记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_income_record")
@ApiModel(value = "AIncomeRecord", description = "代理商收益记录")
public class AIncomeRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(value = "代理商id")
    @Column(nullable = false, name = "a_user_id")
    private Integer aUserId;

    @ApiModelProperty(notes = "营业额")
    @Column(name = "consumer_amount", columnDefinition = "decimal(14,2)")
    private double consumerAmount;

    @ApiModelProperty(notes = "收益总额（未减倒扣金额）")
    @Column(name = "income_sum_amount", columnDefinition = "decimal(14,2)")
    private double incomeSumAmount;

    @ApiModelProperty(notes = "收益金额")
    @Column(name = "income_amount", columnDefinition = "decimal(14,2)")
    private double incomeAmount;

    @ApiModelProperty(notes = "收益G米")
    @Column(name = "income_wpoint", columnDefinition = "decimal(14,2)")
    private double incomeWPoint;

    @ApiModelProperty(notes = "倒扣金额")
    @Column(name = "back_amount", columnDefinition = "decimal(14,2)")
    private double backAmount;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
