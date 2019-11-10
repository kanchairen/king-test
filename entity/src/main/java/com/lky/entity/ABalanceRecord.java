package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理商大米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_balance_record")
@ApiModel(value = "ABalanceRecord", description = "代理商大米变动记录")
public class ABalanceRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(value = "代理商id")
    @Column(nullable = false, name = "a_user_id")
    private Integer aUserId;

    @ApiModelProperty(notes = "小米变动类型（收入：G米转化，收益，提现退回 支出：提现）",
            allowableValues = "wpoint_convert_balance,income,withdraw_back,withdraw")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "改变之后的当前大米")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double currentBalance;

    @ApiModelProperty(notes = "变动的大米（正为加，负为减）")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double changeBalance;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
