package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 店铺行业类型
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_industry")
@ApiModel(value = "Industry", description = "店铺行业类型")
public class Industry extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "行业名称")
    @Column(nullable = false, length = 64)
    private String name;

    @ApiModelProperty(notes = "父类id，支持多级")
    private Integer parentId;

    @ApiModelProperty(notes = "行业等级")
    private Integer level;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "icon")
    @OneToOne
    @JoinColumn(name = "icon_img_id")
    private Image icon;
}
