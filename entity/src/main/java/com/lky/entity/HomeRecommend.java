package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 首页推荐
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_home_recommend")
@ApiModel(value = "HomeRecommend", description = "首页推荐")
public class HomeRecommend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "宣传图")
    @OneToOne
    @JoinColumn(name = "show_img_id")
    private Image showImg;

    @ApiModelProperty(notes = "类型，商品、店铺", allowableValues = "product,shop")
    @Column(length = 32)
    private String targetType;

    @ApiModelProperty(notes = "目标id")
    private Integer targetId;

    @ApiModelProperty(notes = "名称")
    private String name;

    @ApiModelProperty(notes = "缩略图")
    @OneToOne
    @JoinColumn(name = "preview_img_id")
    private Image previewImg;

    @ApiModelProperty(notes = "排序")
    @Column(name = "sort_index")
    private Integer sortIndex;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
