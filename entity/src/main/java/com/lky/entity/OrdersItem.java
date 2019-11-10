package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 子订单项
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_orders_item")
@ApiModel(value = "OrdersItem", description = "子订单项")
public class OrdersItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "商品id")
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ApiModelProperty(notes = "商品数量")
    @Column(nullable = false)
    private int number;

    @ApiModelProperty(notes = "商品单价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double price;

    @ApiModelProperty(notes = "小米单价，大于零为小米支付")
    @Column(name = "rpoint_price", nullable = false, columnDefinition = "decimal(14,2)")
    private double rpointPrice;

    @ApiModelProperty(notes = "G米单价")
    @Column(name = "wpoint_price", nullable = false, columnDefinition = "decimal(14,2)")
    private double wpointPrice;

    @ApiModelProperty(notes = "是否使用小米，默认否")
    @Column(name = "use_rpoint", nullable = false)
    private Boolean useRPoint = Boolean.FALSE;

    @ApiModelProperty(notes = "消费获得的G米")
    @Column(nullable = false, name = "give_wpoint", columnDefinition = "decimal(14,2)")
    private double giveWPoint;

    @ApiModelProperty(notes = "下单时的让利比")
    @Column(name = "benefit_rate", columnDefinition = "decimal(14,2)")
    private double benefitRate;

    @ApiModelProperty(notes = "折扣价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double discountPrice;

    @ApiModelProperty(notes = "订单号")
    @Column(nullable = false, length = 32)
    private String ordersId;

    @ApiModelProperty(notes = "退款状态（申请、同意、拒绝）", allowableValues = "apply,agree,refuse")
    @Column(length = 32)
    private String returnState;

    @ApiModelProperty(notes = "app用户")
    @Column(name = "m_user_id", nullable = false)
    private Integer userId;

    @ApiModelProperty(notes = "店铺id")
    @Column(name = "shop_id", nullable = false)
    private Integer shopId;

    @ApiModelProperty(notes = "是否评论")
    @Column(name = "is_comment")
    private Boolean comment = Boolean.FALSE;

    @ApiModelProperty(notes = "是否追评")
    @Column(name = "is_append_comment")
    private Boolean appendComment = Boolean.FALSE;

    @ApiModelProperty(notes = "是否回复")
    @Column(name = "is_reply")
    private Boolean reply = Boolean.FALSE;

    @ApiModelProperty(notes = "是否追评回复")
    @Column(name = "is_append_reply")
    private Boolean appendReply = Boolean.FALSE;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
