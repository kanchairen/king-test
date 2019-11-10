package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 余粮公社变动记录
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_surplus_grain_record")
@ApiModel(value = "SurplusGrainRecord", description = "余粮公社变动记录")
public class SurplusGrainRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;
    @ApiModelProperty(notes = "余粮公社变动类型（" +
            "收入：大米转入获得的余粮公社，支出：余粮公社转到大米）", allowableValues = "from_balance，to_balance")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "改变之后的当前余粮公社")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double currentSurplusGrain = 0;

    @ApiModelProperty(notes = "变动的余粮公社（正为加，负为减）")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double changeSurplusGrain = 0;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "开始计算收益时间")
    private Date incomeTime;

}
