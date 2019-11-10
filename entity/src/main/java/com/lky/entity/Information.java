package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 资讯
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_information")
@ApiModel(value = "Information", description = "资讯")
public class Information extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "资讯类型，新闻动态、平台公告、商学院", allowableValues = "news,notice,college")
    @Column(length = 32)
    private String type;

    @ApiModelProperty(notes = "标题")
    @Column(length = 32)
    private String title;

    @ApiModelProperty(notes = "icon图标")
    @OneToOne
    private Image icon;

    @ApiModelProperty(notes = "文本内容")
    @Lob
    @Basic
    @Column(name = "content", columnDefinition="TEXT")
    private String content;

    @ApiModelProperty(notes = "是否为弹窗公告")
    private Boolean popUp;

    @ApiModelProperty(notes = "是否为悬浮公告")
    private Boolean suspend;

    @ApiModelProperty(notes = "悬浮公告开始时间")
    private Date beginTime;

    @ApiModelProperty(notes = "悬浮公告结束时间")
    private Date endTime;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
