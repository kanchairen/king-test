package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 扫码支付记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_qr_code_record")
@ApiModel(value = "QrCodeRecord", description = "扫码支付记录")
public class QrCodeRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "付款金额")
    @Column(name = "amount", columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(notes = "G米数量")
    @Column(name = "number", columnDefinition = "decimal(14,2)")
    private double number;

    @ApiModelProperty(notes = "是否加入到代理商收益中计算")
    @Column(nullable = false)
    private Boolean calculated;

    @ApiModelProperty(notes = "活动比例")
    @Column(name = "rate", columnDefinition = "decimal(14,2)")
    private double rate;

    @ApiModelProperty(notes = "支付状态，未支付，已支付", allowableValues = "unpaid,paid")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "是否跳转URL连接")
    private Boolean redirect;

    @ApiModelProperty(notes = "跳转URL门槛（元）")
    @Column(columnDefinition = "decimal(14,2)")
    private Double threshold;

    @ApiModelProperty(notes = "跳转URL")
    private String url;
}
