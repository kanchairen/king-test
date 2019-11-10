package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 线下店铺订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_offline_orders")
@ApiModel(value = "OfflineOrders", description = "线下店铺订单")
public class OfflineOrders extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, length = 18, unique = true)
    private String id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "店铺id")
    @ManyToOne
    @JoinColumn(nullable = false, name = "shop_id")
    private Shop shop;

    @ApiModelProperty(notes = "订单状态（待付款、已付款）", allowableValues = "unpaid,paid")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "订单总金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(notes = "需要支付的现金价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double price;

    @ApiModelProperty(notes = "下单时的让利比")
    @Column(name = "benefit_rate", columnDefinition = "decimal(14,2)")
    private double benefitRate;

    @ApiModelProperty(notes = "消费获得的G米")
    @Column(nullable = false, name = "give_wpoint", columnDefinition = "decimal(14,2)")
    private double giveWPoint;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
