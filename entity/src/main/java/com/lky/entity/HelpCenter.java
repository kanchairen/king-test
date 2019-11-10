package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 帮助中心
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_help_center")
@ApiModel(value = "HelpCenter", description = "帮助中心")
public class HelpCenter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "类型，用户、商家、运营", allowableValues = "user,merchant,operation")
    @Column(length = 32)
    private String type;

    @ApiModelProperty(notes = "标题")
    @Column(length = 32)
    private String title;

    @ApiModelProperty(notes = "文本内容")
    @Lob
    @Basic
    @Column(name = "content", columnDefinition="TEXT")
    private String content;

    @ApiModelProperty(notes = "排序")
    @Column(name = "sort_index")
    private Integer sortIndex;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
