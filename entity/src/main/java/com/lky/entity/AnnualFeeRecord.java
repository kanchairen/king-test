package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 缴纳年费记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_annual_fee_record")
@ApiModel(value = "AnnualFeeRecord", description = "缴纳年费记录")
public class AnnualFeeRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "店铺id")
    private Integer shopId;

    @ApiModelProperty(notes = "金额")
    @Column(name = "amount", columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(notes = "年数")
    private Integer number;

    @ApiModelProperty(notes = "缴纳年费类型，普通店铺", allowableValues = "shop")
    @Column(length = 32)
    private String type;

    @ApiModelProperty(notes = "支付状态，未支付，已支付", allowableValues = "unpaid,paid")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
