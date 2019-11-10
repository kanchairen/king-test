package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 商城店铺配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_shop_config")
@ApiModel(value = "ShopConfig", description = "商城店铺配置")
public class ShopConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "线下店铺的让利比（所有的线下支付商品都按照这个比例计算，大于等于5%小于等于100%）")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double benefitRate = 20;

    @ApiModelProperty(notes = "开通店铺（默认0：否 1：是）")
    @Column(nullable = false)
    private Boolean openShop = Boolean.FALSE;

    @ApiModelProperty(notes = "开通小米（默认0：否 1：是）")
    @Column(name = "open_rpoint", nullable = false)
    private Boolean openRPoint = Boolean.FALSE;

    @ApiModelProperty(notes = "开通G米 (默认0：否 1：是)")
    @Column(name = "open_wpoint", nullable = false)
    private Boolean openWPoint = Boolean.FALSE;

    @ApiModelProperty(notes = "是否显示banner（默认0：否 1：是）")
    private Boolean showBanner = Boolean.FALSE;

    @ApiModelProperty(notes = "是否显示店铺内商品分类（默认0：否 1：是）")
    private Boolean showKind = Boolean.FALSE;
}
