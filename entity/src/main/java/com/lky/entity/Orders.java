package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 店铺订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_orders")
@ApiModel(value = "Orders", description = "店铺订单")
public class Orders extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false, length = 18, unique = true)
    private String id;

    @ApiModelProperty(notes = "发货类型（快递配送（现金）、快递配送（小米））", allowableValues = "delivery_cash,delivery_rpoint")
    @Column(nullable = false, length = 32)
    private String sendType;

    @ApiModelProperty(notes = "订单状态（待付款、待发货、待收货、已完成、已关闭）", allowableValues = "wait,send,receive,over,close")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "店铺id")
    @Column(nullable = false)
    private Integer shopId;

    @ApiModelProperty(notes = "订单总金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(notes = "需要支付的现金价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double price;

    @ApiModelProperty(notes = "需要支付的小米")
    @Column(name = "rpoint_price", nullable = false, columnDefinition = "decimal(14,2)")
    private double rpointPrice;

    @ApiModelProperty(notes = "需要支付的G米")
    @Column(name = "wpoint_price", nullable = false, columnDefinition = "decimal(14,2)")
    private double wpointPrice;

    @ApiModelProperty(notes = "需要现金支付的商品价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double productPrice;

    @ApiModelProperty(notes = "需要现金支付的物流价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double freightPrice;

    @ApiModelProperty(notes = "折扣价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double discountPrice;

    @ApiModelProperty(notes = "消费获得的G米")
    @Column(nullable = false, name = "give_wpoint", columnDefinition = "decimal(14,2)")
    private double giveWPoint;

    @ApiModelProperty(notes = "收货地址")
    @Column(length = 512)
    private String receiveAddress;

    @ApiModelProperty(notes = "支付类型（小米、支付宝、微信、银联、线下现金）", allowableValues = "rpoint,alipay,wechat,unipay,cash")
    @Column(length = 32)
    private String payType;

    @ApiModelProperty(notes = "是否删除")
    private Boolean deleted = Boolean.FALSE;

    @ApiModelProperty(notes = "是否退换货")
    private Boolean returned = Boolean.FALSE;

    @ApiModelProperty(notes = "退款状态（申请、同意、拒绝）", allowableValues = "apply,agree,refuse")
    @Column(length = 32)
    private String returnState;

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

    @ApiModelProperty(notes = "订单备注")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

    @ApiModelProperty(notes = "付款时间")
    private Date payTime;

    @ApiModelProperty(notes = "发货时间")
    private Date sendTime;

    @ApiModelProperty(notes = "成交时间")
    private Date overTime;
}
