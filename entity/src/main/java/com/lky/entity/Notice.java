package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 平台公告
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_notice")
@ApiModel(value = "Notice", description = "平台公告")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "公告标题")
    @Column(nullable = false, length = 32)
    private String title;

    @ApiModelProperty(notes = "公告内容")
    @Column(length = 1024, nullable = false)
    private String context;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
