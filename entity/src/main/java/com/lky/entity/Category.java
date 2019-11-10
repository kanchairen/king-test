package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 商城类目
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_category")
@ApiModel(value = "Category", description = "类目")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "名称")
    @Column(nullable = false, length = 32)
    private String name;

    @ApiModelProperty(notes = "父类目id")
    private Integer parentId;

    @ApiModelProperty(notes = "类目logo,如果后台维护的话，写成img对象")
    @OneToOne
    @JoinColumn(name = "logo_img_id")
    private Image logoImg;

    @ApiModelProperty(notes = "类目等级", allowableValues = "1,2,3")
    @Column(nullable = false)
    private int level;
}
