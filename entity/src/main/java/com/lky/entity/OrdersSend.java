package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户发货记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_orders_send")
@ApiModel(value = "OrdersSend", description = "用户发货记录")
public class OrdersSend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "订单号")
    @Column(nullable = false, length = 32)
    private String ordersId;

    @ApiModelProperty(notes = "用户id")
    @Column(name = "m_user_id")
    private Integer userId;

    @ApiModelProperty(notes = "发货类型（快递配送（现金）、快递配送（小米））", allowableValues = "delivery_cash,delivery_rpoint")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "快递公司")
    @ManyToOne
    @JoinColumn(name = "express_id")
    private Express express;

    @ApiModelProperty(notes = "快递单号")
    @Column(name = "express_odd", length = 64)
    private String expressOdd;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
