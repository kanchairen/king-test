package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单支付记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_payment_record")
@ApiModel(value = "PaymentRecord", description = "订单支付记录")
public class PaymentRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "交易流水号")
    @Column(nullable = false, length = 32)
    private String transactionCode;

    @ApiModelProperty(notes = "支付类型（小米、支付宝、微信、银联、线下现金）", allowableValues = "rpoint,alipay,wechat,unipay,cash")
    @Column(nullable = false, length = 32)
    private String payType;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "订单号、申请开店记录id、子订单项")
    private String targetId;

    @ApiModelProperty(notes = "子订单项")
    @Column(name = "target_type")
    private String targetType;

    @ApiModelProperty(notes = "支付金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(notes = "支付状态（未支付、已支付）", allowableValues = "unpaid,paid")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
