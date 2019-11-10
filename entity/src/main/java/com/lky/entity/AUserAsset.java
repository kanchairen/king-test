package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 代理商资产
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_user_asset")
@ApiModel(value = "AUserAsset", description = "代理商资产")
public class AUserAsset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "大米")
    @Column(name = "balance", nullable = false, columnDefinition = "decimal(14,2)")
    private double balance;

    @ApiModelProperty(notes = "G米个数")
    @Column(name = "wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double wpoint;

    @ApiModelProperty(notes = "小米个数")
    @Column(name = "rpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double rpoint;

    @ApiModelProperty(notes = "代理商可激励G米")
    @Column(name = "trans_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double transWPoint;

    @ApiModelProperty(notes = "已提现金额")
    @Column(name = "withdraw_balance", nullable = false, columnDefinition = "decimal(14,2)")
    private double withdrawBalance;

    @ApiModelProperty(notes = "G米转换的金额")
    @Column(name = "convert_amount", nullable = false, columnDefinition = "decimal(14,2)")
    private double convertAmount;

    @ApiModelProperty(notes = "总收益金额")
    @Column(name = "sum_income_amount", nullable = false, columnDefinition = "decimal(14,2)")
    private double sumIncomeAmount;

    @ApiModelProperty(notes = "已倒扣金额")
    @Column(name = "sum_back_amount", nullable = false, columnDefinition = "decimal(14,2)")
    private double sumBackAmount;
}
