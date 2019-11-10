package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 店铺商品组
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_product_group")
@ApiModel(value = "ProductGroup", description = "店铺商品组")
public class ProductGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "商品显示名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(notes = "商品显示G米价格")
    @Column(name = "wpoint_price", columnDefinition = "decimal(14,2)")
    private double wpointPrice;

    @ApiModelProperty(notes = "商品显示价格")
    @Column(name = "price", columnDefinition = "decimal(14,2)")
    private double price;

    @ApiModelProperty(notes = "是否支持小米购买")
    @Column(name = "support_rpoint")
    private Boolean supportRPoint = Boolean.FALSE;

    @ApiModelProperty(notes = "商品显示图片,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "show_img_ids", nullable = false)
    private String showImgIds;

    @ApiModelProperty(notes = "商品详情")
    @Lob
    @Basic
    @Column(name = "detail")
    private String detail;

    @ApiModelProperty(notes = "是否下架（默认0：否 1：是）")
    @Column(nullable = false)
    private Boolean offline = Boolean.FALSE;

    @ApiModelProperty(notes = "商品总销量")
    @Column(nullable = false)
    private int totalSold;

    @ApiModelProperty(notes = "商品月销量")
    @Column(nullable = false)
    private int monthSold;

    @ApiModelProperty(notes = "最近销量")
    @Column(nullable = false)
    private int recentSold;

    @ApiModelProperty(notes = "可获G米，根据公式计算消费额 *（让利比/比例基数）* 100，取商品中第一个，每次修改保存商品时重新计算")
    @Column(name = "get_wpoint", columnDefinition = "decimal(14,2)")
    private double getWPoint;

    @ApiModelProperty(notes = "评论数")
    @Column(nullable = false)
    private int commentNumber;

    @ApiModelProperty(notes = "在售的同款商品个数")
    @Column(nullable = false)
    private int onSellNumber;

    @ApiModelProperty(notes = "售完的同款商品个数")
    @Column(nullable = false)
    private int sellOutNumber;

    @ApiModelProperty(notes = "下架的同款商品个数")
    @Column(nullable = false)
    private int offlineNumber;

    @ApiModelProperty(notes = "店铺id")
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ApiModelProperty(notes = "商品类目")
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ApiModelProperty(notes = "店铺内分类id,逗号隔开")
    @Column(name = "shop_kind_ids")
    private String shopKindIds;

    @ApiModelProperty(notes = "物流模板id")
    @ManyToOne
    @JoinColumn(name = "freight_template_id")
    private FreightTemplate freightTemplate;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

    @ApiModelProperty(notes = "商品列表")
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "product_group_id")
    private List<Product> productList;

    @ApiModelProperty(notes = "审核状态")
    private String auditState;

    @ApiModelProperty(notes = "审核备注，未通过的理由")
    private String auditRemark;
}
