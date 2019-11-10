package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理商大米提现记录表
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_withdraw_record")
@ApiModel(value = "AWithdrawRecord", description = "代理商大米提现记录")
public class AWithdrawRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "代理商用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "a_user_id")
    private AUser aUser;

    @ApiModelProperty(notes = "银行卡信息")
    @ManyToOne
    @JoinColumn(nullable = false, name = "a_bank_card_id")
    private ABankCard abankCard;

    @ApiModelProperty(notes = "提现记录状态（提现申请、提现中、提现失败、提现完成）",
            allowableValues = "apply,agree,refuse,finish")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "代付批次号，要求唯一")
    @Column(nullable = false, unique = true, name = "batch_no")
    private String batchNo;

    @ApiModelProperty(notes = "提现之后的当前大米")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double currentBalance = 0;

    @ApiModelProperty(notes = "提现的金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double withdrawAmount = 0;

    @ApiModelProperty(notes = "提现手续费")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double withdrawFee = 0;

    @ApiModelProperty(notes = "实际打款金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double realAmount = 0;

    @ApiModelProperty(notes = "备注")
    @Column
    private String remark;

    @ApiModelProperty(notes = "申请时间")
    @Column(nullable = false)
    private Date applyTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    @Column
    private Date updateTime = new Date();

    @ApiModelProperty(notes = "完成时间")
    @Column
    private Date finishTime;
}
