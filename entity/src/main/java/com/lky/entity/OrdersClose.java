package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户取消记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_orders_close")
@ApiModel(value = "OrdersClose", description = "用户取消记录")
public class OrdersClose extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "订单号")
    @Column(nullable = false, length = 32)
    private String ordersId;

    @ApiModelProperty(notes = "取消类型（系统自动、手动）", allowableValues = "auto,manual")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "用户id")
    @Column(name = "m_user_id")
    private Integer userId;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
