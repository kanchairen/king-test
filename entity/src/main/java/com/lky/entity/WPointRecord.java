package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * G米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_wpoint_record")
@ApiModel(value = "WPointRecord", description = "G米变动记录")
public class WPointRecord extends BaseEntity {

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

    @ApiModelProperty(notes = "G米变动类型（用户收入：线上订单获得、线下订单获得、下线分成获得、存量G米转化G米，" +
            "用户支出：G米转化成大米、G米转化成存量G米、G米转化成小米" +
            "商家收入：线上订单获得、线下订单获得，" +
            "商家支出：G米转化成大米、G米转化成存量G米、G米转换成小米）",
            allowableValues = "consumer_online_orders,consumer_offline_orders,consumer_divide," +
                    "consumer_lock_wpoint_convert,consumer_convert_balance,consumer_convert_lock_wpoint,consumer_convert_rpoint" +
                    "merchant_online_orders,merchant_offline_orders,merchant_convert_balance,merchant_convert_lock_wpoint,merchant_convert_rpoint")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "是否加入到代理商收益中计算")
    @Column(nullable = false)
    private Boolean calculated = Boolean.FALSE;

    @ApiModelProperty(notes = "变动之后的G米数")
    @Column(name = "current_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double currentWPoint;

    @ApiModelProperty(notes = "变动的小米数（正为加，负为减）")
    @Column(name = "change_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double changeWPoint;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
