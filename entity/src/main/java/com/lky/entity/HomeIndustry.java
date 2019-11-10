package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 线下店铺首页推荐的店铺行业类型
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_home_industry")
@ApiModel(value = "HomeIndustry", description = "线下店铺首页推荐的店铺行业类型")
public class HomeIndustry extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "行业名称")
    private String name;

    @ApiModelProperty(notes = "行业")
    private Integer industryId;

    @ApiModelProperty(notes = "行业图片")
    @OneToOne
    @JoinColumn(name = "icon_image_id")
    private Image iconImage;

    @ApiModelProperty(notes = "排序")
    @Column(name = "sort_index")
    private Integer sortIndex;
}
