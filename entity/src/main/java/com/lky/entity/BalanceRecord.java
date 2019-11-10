package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户大米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_balance_record")
@ApiModel(value = "BalanceRecord", description = "用户大米变动记录")
public class BalanceRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "大米变动类型（" +
            "收入：用户G米转大米、商家G米转大米、用户小米转大米、商家小米转大米、小米转大米、线上店铺订单收入、线下店铺订单收入，" +
            "支出：提现）", allowableValues = "consumer_wpoint_convert_balance,merchant_wpoint_convert_balance" +
            "consumer_rpoint_convert_balance,merchant_rpoint_convert_balance" +
            "rpoint_convert_balance,online_orders,offline_orders,withdraw")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "改变之后的当前大米")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double currentBalance = 0;

    @ApiModelProperty(notes = "变动的大米（正为加，负为减）")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double changeBalance = 0;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
