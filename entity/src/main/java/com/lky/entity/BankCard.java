package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 银行卡
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_bank_card")
@ApiModel(value = "BankCard", description = "银行卡")
public class BankCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "手机号")
    private String mobile;

    @ApiModelProperty(notes = "银行卡号")
    private String bankcard;

    @ApiModelProperty(notes = "身份证号")
    private String cardNo;

    @ApiModelProperty(notes = "真实姓名")
    private String realName;

    @ApiModelProperty(notes = "银行编号")
    private String bankNum;

    @ApiModelProperty(notes = "银行中文名称")
    private String bankName;

    @ApiModelProperty(notes = "开户地区")
    private String bankArea;

    @ApiModelProperty(notes = "开户支行")
    private String branchName;

    @ApiModelProperty(notes = "卡号前缀号码")
    private String cardPrefixNum;

    @ApiModelProperty(notes = "卡名称")
    private String cardName;

    @ApiModelProperty(notes = "卡类型")
    private String cardType;

    @ApiModelProperty(notes = "卡号前缀长度")
    private String cardPrefixLength;

    @ApiModelProperty(notes = "卡号长度")
    private String cardLength;

    @ApiModelProperty(notes = "是否采用luhn算法")
    private Boolean isLuhn = Boolean.TRUE;

    @ApiModelProperty(notes = "是否是信用卡, 1：借记卡 2：贷记卡")
    private Integer isCreditCard;

    @ApiModelProperty(notes = "银行官网链接")
    private String bankUrl;

    @ApiModelProperty(notes = "银行英文名称")
    private String enBankName;

    @ApiModelProperty(notes = "缩写")
    private String abbreviation;

    @ApiModelProperty(notes = "银行image图片链接")
    private String bankImage;

    @ApiModelProperty(notes = "银行服务电话")
    private String servicePhone;

    @ApiModelProperty(notes = "银行卡绑定")
    private Boolean bind = Boolean.TRUE;
}
