package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * banner广告
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_banner")
@ApiModel(value = "Banner", description = "banner广告")
public class Banner extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "类型，首页、线上商城、小米商城、G米商城", allowableValues = "home,online，rPoint, wPoint")
    @Column(nullable = false, length = 20)
    private String type;

    @ApiModelProperty(notes = "链接目标类型，商品、店铺、自定义页面、自定义链接",
    allowableValues = "product,shop,custom_text,custom_link")
    private String linkType;

    @ApiModelProperty(notes = "链接目标值")
    private String linkValue;

    @ApiModelProperty(notes = "链接目标名称")
    private String linkName;

    @ApiModelProperty(notes = "图片")
    @OneToOne
    @JoinColumn(name = "banner_img_id")
    private Image bannerImg;

    @ApiModelProperty(notes = "排序")
    @Column(name = "sort_index")
    private Integer sortIndex;

    @ApiModelProperty(notes = "小米商城id/G米商城id")
    @Column(name = "shop_id")
    private Integer shopId;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
