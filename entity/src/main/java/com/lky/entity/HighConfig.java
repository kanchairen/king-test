package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 高级配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_high_config")
@ApiModel(value = "HighConfig", description = "高级配置")
public class HighConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;




    @ApiModelProperty(notes = "平台手动设置乐康指数，默认公式 [（前一天所有商家的现金成交额）*15%]/（累计到前一天所有G米）根据公式计算总G米如果小于前一天累计的所有G米冻结，例：12.34%")
    @Column(name = "min_health_index", columnDefinition = "decimal(14,2)")
    private Double minHealthIndex;

    @ApiModelProperty(notes = "平台手动设置乐康指数，不使用默认公式 [（前一天所有商家的现金成交额）*15%]/（累计到前一天所有G米），例：12.34%")
    @Column(name = "lhealth_index", columnDefinition = "decimal(14,2)")
    private Double lhealthIndex;

    @ApiModelProperty(notes = "乐康指数基数，默认15%")
    @Column(name = "lhealth_index_base", columnDefinition = "decimal(14,2)")
    private double lhealthIndexBase = 15;

    @ApiModelProperty(notes = "G米是否自动转换小米和大米（默认1：是）")
    @Column(name = "wpoint_auto_convert")
    private Boolean wpointAutoConvert = Boolean.TRUE;

    @ApiModelProperty(notes = "G米转换大米时间")
    @Column(name = "wpoint_convert_time")
    private Date wpointConvertTime;



    @ApiModelProperty(notes = "短信提醒手机号，可设置多个")
    @Column(name = "remind_mobile")
    private String remindMobile;



    @ApiModelProperty(notes = "手动冻结所有用户的G米比例")
    @Column(name = "lock_wpoint_rate", columnDefinition = "decimal(14,2)")
    private double lockWPointRate = 0;

    @ApiModelProperty(notes = "消费G米解冻的比例")
    @Column(name = "unlock_wpoint_rate", columnDefinition = "decimal(14,2)")
    private double unlockWPointRate = 0;


    @ApiModelProperty(notes = "消费者G米每天转换成大米比例（默认80%）")
    @Column(name = "wpoint_convert_balance_rate", columnDefinition = "decimal(14,2)")
    private double wpointConvertBalanceRate = 80;

    @ApiModelProperty(notes = "消费者G米每天转换成小米（默认20%，与转换大米相加100%）")
    @Column(name = "wpoint_convert_rpoint_rate", columnDefinition = "decimal(14,2)")
    private double wpointConvertRPointRate = 20;

    @ApiModelProperty(notes = "商家G米每天转换成大米比例（默认100%）")
    @Column(name = "merchant_wpoint_convert_balance_rate", columnDefinition = "decimal(14,2)")
    private double merchantWPointConvertBalanceRate = 100;

    @ApiModelProperty(notes = "商家G米每天定时转换小米（默认0%，与商家转换大米相加100%）")
    @Column(name = "merchant_wpoint_convert_rpoint_rate", columnDefinition = "decimal(14,2)")
    private double merchantWPointConvertRPointRate = 0;

    @ApiModelProperty(notes = "代理商G米每天转换成大米比例（默认100%）")
    @Column(name = "agent_wpoint_convert_balance_rate", columnDefinition = "decimal(14,2)")
    private double agentWPointConvertBalanceRate = 100;

    @ApiModelProperty(notes = "代理商G米每天定时转换小米（默认0%，与代理商G米每天转换成小米相加100%）")
    @Column(name = "agent_wpoint_convert_rpoint_rate", columnDefinition = "decimal(14,2)")
    private double agentWPointConvertRPointRate = 0;

    @ApiModelProperty(notes = "用户、商家 G米转换小米平台手续费（默认10%）")
    @Column(name = "wpoint_convert_fee", columnDefinition = "decimal(14,2)")
    private double wpointConvertFee = 10;

    @ApiModelProperty(notes = "用户、商家 G米转换小米平台手续费（默认10%）")
    @Column(name = "wpoint_convert_rpoint_fee", columnDefinition = "decimal(14,2)")
    private double wpointConvertRPointFee = 10;





    @ApiModelProperty(notes = "大米提现手续费(默认10%)")
    @Column(name = "balance_withdraw_fee", columnDefinition = "decimal(14,2)")
    private double balanceWithdrawFee = 10;

    @ApiModelProperty(notes = "商家小米转化成大米手续费（默认10%）")
    @Column(name = "merchant_rpoint_convert_balance_rate", columnDefinition = "decimal(14,2)")
    private double merchantRPointConvertBalanceRate = 10;





    @ApiModelProperty(notes = "普通店铺保证金（冻结不可提现，默认1000）")
    @Column(name = "shop_cash_deposit", columnDefinition = "decimal(14,2)")
    private double shopCashDeposit = 1000;





    @ApiModelProperty(notes = "开通普通店铺费用（默认365元）")
    @Column(name = "open_shop_fee", columnDefinition = "decimal(14,2)")
    private double openShopFee = 365;




    @ApiModelProperty(notes = "推广下线权益（" +
            "1、一级下线购买时的G米分成，默认5%" +
            "2、二级下线购买时的G米分成，默认2.5%" +
            "3、下线为商家，商家获取的商家G米分成为3%)，json格式数据")
    @Column(name = "member_rights")
    private String memberRights;

    @ApiModelProperty(notes = "注册送的存量G米数量")
    @Column(name = "register_give_wpoint_num", columnDefinition = "decimal(14,2)")
    private double registerGiveWPointNum = 0;

    @ApiModelProperty(notes = "注册送的G米数量")
    @Column(name = "register_wpoint", columnDefinition = "decimal(14,2)")
    private double registerWPoint = 0;

    @ApiModelProperty(notes = "注册送推荐人的G米数量")
    @Column(name = "register_parent_wpoint", columnDefinition = "decimal(14,2)")
    private double registerParentWPoint = 0;

    @ApiModelProperty(notes = "成为商家送的存量G米")
    @Column(name = "be_merchant_wpoint_num", columnDefinition = "decimal(14,2)")
    private double beMerchantWPointNum = 0;

    @ApiModelProperty(notes = "成为商家送N倍的商家G米")
    @Column(name = "be_merchant_wpoint_rate", columnDefinition = "decimal(14,2)")
    private double beMerchantWPointRate = 0;

    @ApiModelProperty(notes = "成为商家送推荐人的G米")
    @Column(name = "be_merchant_parent_wpoint", columnDefinition = "decimal(14,2)")
    private double beMerchantParentWPoint = 0;

    @ApiModelProperty(notes = "成为商家送推荐人的大米")
    @Column(name = "be_merchant_parent_balance", columnDefinition = "decimal(14,2)")
    private double beMerchantParentBalance = 0;




    @ApiModelProperty(notes = "让利比范围，最小值（默认5%）")
    @Column(name = "benefit_rate_min", columnDefinition = "decimal(14,2)")
    private double benefitRateMin = 5;

    @ApiModelProperty(notes = "让利比范围，最大值（默认100%）")
    @Column(name = "benefit_rate_max", columnDefinition = "decimal(14,2)")
    private double benefitRateMax = 100;

    @ApiModelProperty(notes = "消费者获得G米的让利比基准 （默认20%）")
    @Column(name = "consumer_wpoint_rate", columnDefinition = "decimal(14,2)")
    private double consumerWPointRate = 20;

    @ApiModelProperty(notes = "商家获得的G米基准系数（默认15%）")
    @Column(name = "benefit_rate_datum", columnDefinition = "decimal(14,2)")
    private double benefitRateDatum = 15;

    @ApiModelProperty(notes = "每个账户最多能存入余粮公社金额")
    @Column(name = "max_surplus_grain", columnDefinition = "decimal(14,2)")
    private double maxSurplusGrain = 0;

    @ApiModelProperty(notes = "存入余粮公社后，几天后收益G米")
    @Column(name = "surplus_grain_start_day")
    private int surplusGrainStartDay = 0;

    @ApiModelProperty(notes = "每存入余粮公社1元，每天可获得G米数量")
    @Column(name = "surplus_grain_rate", columnDefinition = "decimal(14,2)")
    private double surplusGrainRate = 0;
}
