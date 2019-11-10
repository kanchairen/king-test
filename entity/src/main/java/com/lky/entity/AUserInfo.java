package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理商信息
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_user_info")
@ApiModel(value = "AUserInfo", description = "代理商信息")
public class AUserInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(value = "是否全部出资")
    @Column(name = "pay_all", nullable = false)
    private Boolean payAll;

    @ApiModelProperty(value = "代理级别(省级代理，市级代理，区级代理)", allowableValues = "province,city,district")
    @Column(name = "level", length = 32)
    private String level;

    @ApiModelProperty(value = "代理区域")
    private String area;

    @ApiModelProperty(value = "收益率")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double incomeRate;

    @ApiModelProperty(value = "开始代理时间")
    @Column(name = "begin_agent_date")
    private Date beginAgentDate;

    @ApiModelProperty(value = "代理金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(value = "出资金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double payAmount;

    @ApiModelProperty(value = "出资比例")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double payRate;

    @ApiModelProperty(value = "倒扣开始时间")
    private Date backBeginDate;

    @ApiModelProperty(value = "倒扣的百分比")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double backRate;
}
