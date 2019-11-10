package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * G米修改变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_change_wpoint_record")
@ApiModel(value = "ChangeWPointRecord", description = "G米修改变动记录")
public class ChangeWPointRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "操作人姓名")
    private String operateName;

    @ApiModelProperty(notes = "操作人手机号")
    private String operateMobile;

    @ApiModelProperty(notes = "类型, 赠送G米", allowableValues = "wpoint")
    private String type;

    @ApiModelProperty(notes = "变动的G米数（正为加，负为减）")
    @Column(name = "number", nullable = false, columnDefinition = "decimal(14,2)")
    private double number;

    @ApiModelProperty(notes = "是否加入到代理商收益中计算")
    @Column(nullable = false)
    private Boolean calculated;

    @ApiModelProperty(notes = "审核状态")
    private String auditState;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "审核时间")
    private Date auditTime;
}
