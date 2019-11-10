package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 店铺内分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_shop_kind", indexes = {@Index(name = "kind_sort_index", columnList = "sort_index")})
@ApiModel(value = "ShopKind", description = "店铺内分类")
public class ShopKind extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "店铺内分类名称")
    @Column(nullable = false, length = 128)
    private String name;

    @ApiModelProperty(notes = "店铺id")
    @Column(nullable = false)
    private int shopId;

    @ApiModelProperty(notes = "排序")
    @Column(name = "sort_index")
    private int sortIndex;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
