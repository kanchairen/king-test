package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单任务
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_orders_job")
@ApiModel(value = "OrdersJob", description = "订单任务")
public class OrdersJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "订单号")
    private String ordersId;

    @ApiModelProperty(notes = "任务类型")
    private String type;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime;

    @ApiModelProperty(notes = "执行时间")
    private Date executeTime;
}
