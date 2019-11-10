package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 购物车
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_cart")
@ApiModel(value = "Cart", description = "购物车")
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "app用户")
    @Column(nullable = false, name = "m_user_id")
    private Integer userId;

    @ApiModelProperty(notes = "店铺id")
    @Column(nullable = false)
    private Integer shopId;

    @ApiModelProperty(notes = "商品id")
    @Column(nullable = false)
    private Integer productId;

    @ApiModelProperty(notes = "商品数量")
    @Column(nullable = false)
    private int number;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
