package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户、商家存量G米变动记录
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_lock_wpoint_record")
@ApiModel(value = "LockWPointRecord", description = "用户、商家存量G米变动记录")
public class LockWPointRecord extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "用户类型，消费者、商家", allowableValues = "consumer,merchant")
    @Column(nullable = false, length = 32)
    private String userType;

    @ApiModelProperty(notes = "存量G米变动类型（用户：注册赠予、系统赠予、消费带出" +
            "商家：注册赠予、系统赠予、消费带出）")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "是否加入到代理商收益中计算")
    private Boolean calculated = Boolean.FALSE;

    @ApiModelProperty(notes = "变动之后的存量G米数")
    @Column(name = "current_lock_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double currentLockWPoint;

    @ApiModelProperty(notes = "变动的存量G米数（正为加，负为减）")
    @Column(name = "change_lock_wpoint", nullable = false, columnDefinition = "decimal(14,2)")
    private double changeLockWPoint;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

}
