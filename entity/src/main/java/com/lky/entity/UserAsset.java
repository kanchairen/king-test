package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 商城用户资产
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_user_asset")
@ApiModel(value = "UserAsset", description = "商城用户资产")
public class UserAsset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "可用大米")
    @Column(name = "balance", nullable = false, columnDefinition = "decimal(14,2)")
    private double balance;

    @ApiModelProperty(notes = "余粮公社")
    @Column(name = "surplus_grain", nullable = false, columnDefinition = "decimal(14,2)")
    private double surplusGrain;

    @ApiModelProperty(notes = "保证金（冻结大米，默认1000）")
    @Column(name = "cash_deposit", nullable = false, columnDefinition = "decimal(14,2)")
    private double cashDeposit;

    @ApiModelProperty(notes = "小米个数")
    @Column(name = "rpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double rpoint;

    @ApiModelProperty(notes = "G米个数")
    @Column(name = "wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double wpoint;

    @ApiModelProperty(notes = "用户可激励G米")
    @Column(name = "trans_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double transWPoint;

    @ApiModelProperty(notes = "冻结G米数量")
    @Column(name = "lock_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double lockWPoint;

    @ApiModelProperty(notes = "商家小米数量（卖出去商品获得的小米）")
    @Column(name = "merchant_rpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double merchantRPoint;

    @ApiModelProperty(notes = "商家G米数量（成交额*让利比*2得来的G米）在转化成大米时，100%转化")
    @Column(name = "merchant_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double merchantWPoint;

    @ApiModelProperty(notes = "商家冻结的G米数量（来源成为商家冻结的、平台冻结的）")
    @Column(name = "merchant_lock_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double merchantLockWPoint;

    @ApiModelProperty(notes = "商家可激励G米")
    @Column(name = "merchant_trans_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double merchantTransWPoint;
}
