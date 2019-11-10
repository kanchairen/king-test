package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 充值大米记录表
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/7
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_recharge_record")
@ApiModel(value = "RechargeRecord", description = "充值大米记录表")
public class RechargeRecord extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "充值金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(notes = "支付状态（待付款、已付款）", allowableValues = "unpaid,paid")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
