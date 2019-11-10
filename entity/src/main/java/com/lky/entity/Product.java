package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 店铺商品
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_product", indexes = {@Index(name = "product_sort_index", columnList = "sort_index")})
@ApiModel(value = "Product", description = "店铺商品")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "商品名称")
    @Column(nullable = false)
    private String name;

    @ApiModelProperty(notes = "商品规格（json字符串）")
    @Column(length = 1024)
    private String spec;

    @ApiModelProperty(notes = "商品G米价格")
    @Column(name = "wpoint_price", columnDefinition = "decimal(14,2)")
    private double wpointPrice;

    @ApiModelProperty(notes = "是否支持小米购买")
    @Column(name = "support_rpoint")
    private Boolean supportRPoint = Boolean.FALSE;

    @ApiModelProperty(notes = "商品价格")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double price;

    @ApiModelProperty(notes = "线上商品让利比")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double benefitRate;

    @ApiModelProperty(notes = "商品库存")
    @Column(nullable = false)
    private int stock;

    @ApiModelProperty(notes = "缩略图")
    @OneToOne
    @JoinColumn(name = "preview_img_id")
    private Image previewImg;

    @ApiModelProperty(notes = "是否下线（默认0：否 1：是）")
    @Column(nullable = false)
    private Boolean offline = Boolean.FALSE;

    @ApiModelProperty(notes = "总销量")
    @Column(nullable = false)
    private int totalSold;

    @ApiModelProperty(notes = "月销量")
    @Column(nullable = false)
    private int monthSold;

    @ApiModelProperty(notes = "可获G米，根据公式计算消费额 *（让利比/比例基数）* 100，取商品中第一个，每次修改保存商品时重新计算")
    @Column(name = "get_wpoint", columnDefinition = "decimal(14,2)")
    private double getWPoint;

    @ApiModelProperty(notes = "评论数")
    @Column(nullable = false)
    private int commentNumber;

    @ApiModelProperty(notes = "商品条形码")
    @Column(length = 64)
    private String code;

    @ApiModelProperty(notes = "商品重量")
    @Column(name = "weight", columnDefinition = "decimal(14,2)")
    private Double weight = 0.0;

    @ApiModelProperty(notes = "商品体积")
    @Column(name = "volume", columnDefinition = "decimal(14,2)")
    private Double volume = 0.0;

    @ApiModelProperty(notes = "店铺id")
    @Column(name = "shop_id", nullable = false)
    private int shopId;

    @ApiModelProperty(notes = "商品组id")
    @Column(name = "product_group_id")
    private Integer productGroupId;

    @ApiModelProperty(notes = "排序")
    @Column(name = "sort_index")
    private Integer sortIndex;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

}
