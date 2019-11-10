package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 小米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_rpoint_record")
@ApiModel(value = "RPointRecord", description = "小米变动记录")
public class RPointRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "用户类型，消费者、商家", allowableValues = "consumer,merchant")
    @Column(nullable = false, length = 32)
    private String userType;

    @ApiModelProperty(notes = "小米变动类型（用户收入：G米转化、" +
            "用户支出：线下下单购买、线上下单购买，" +
            "商家收入：线下订单收入、线上订单收入，" +
            "商家支出：转换成大米）",
            allowableValues = "consumer_convert,consumer_offline_orders,consumer_online_orders," +
                    "merchant_offline_orders,merchant_online_orders,merchant_convert_balance")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "变动之后的小米数")
    @Column(name = "current_rpoint", columnDefinition = "decimal(14,2)")
    private double currentRPoint;

    @ApiModelProperty(notes = "变动的小米数（正为加，负为减）")
    @Column(name = "change_rpoint", columnDefinition = "decimal(14,2)")
    private double changeRPoint;

    @ApiModelProperty(notes = "备注")
    @Column(name = "remark")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
