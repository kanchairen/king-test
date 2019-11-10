package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 商城排行榜
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_rank")
@ApiModel(value = "Rank", description = "商城排行榜")
public class Rank extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "排行榜类型 （G米排行榜、商家销量排行榜）", allowableValues = "wpoint,sold")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "商家名称")
    @Column(length = 128)
    private String shopName;

    @ApiModelProperty(notes = "店铺id")
    private int shopId;

    @ApiModelProperty(notes = "G米个数/商家销量（每天更新一次）")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double num;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
