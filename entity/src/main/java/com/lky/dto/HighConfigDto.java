package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

/**
 * 高级配置Dto
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-13
 */
@ApiModel(value = "HighConfigDto", description = "高级配置Dto")
@Setter
@Getter
@ToString
public class HighConfigDto {

    private Integer id;

    @ApiModelProperty(notes = "平台手动设置乐康指数，默认公式 [（前一天所有商家的现金成交额）*15%]/（累计到前一天所有G米）根据公式计算总G米如果小于前一天累计的所有G米冻结，例：12.34%")
    private Double minHealthIndex;

    @ApiModelProperty(notes = "乐康指数是否设置为手动")
    private Boolean manualSetLHealthIndex;

    @ApiModelProperty(notes = "平台手动设置乐康指数，不使用默认公式 [（前一天所有商家的现金成交额）*15%]/（累计到前一天所有G米），例：12.34%")
    private Double lhealthIndex;

    @ApiModelProperty(notes = "乐康指数基数，默认15%")
    private Double lhealthIndexBase;

    @ApiModelProperty(notes = "G米是否自动转换小米和大米（默认1：是）")
    private Boolean wpointAutoConvert;

    @ApiModelProperty(notes = "G米转换大米时间")
    private Date wpointConvertTime;

    @ApiModelProperty(notes = "短信提醒手机号，及是否通知，可设置多个；Map的key为手机号，value为是否通知")
    private Map<String, Object> remindMobileMap;


    @ApiModelProperty(notes = "手动冻结所有用户的G米比例")
    private Double lockWPointRate;

    @ApiModelProperty(notes = "消费G米解冻的比例")
    private Double unlockWPointRate;


    @ApiModelProperty(notes = "消费者G米每天转换成大米比例（默认80%）")
    private Double wpointConvertBalanceRate;

    @ApiModelProperty(notes = "消费者G米每天转换成小米（默认20%，与转换大米相加100%）")
    private Double wpointConvertRPointRate;

    @ApiModelProperty(notes = "商家G米每天转换成大米比例（默认100%）")
    private Double merchantWPointConvertBalanceRate;

    @ApiModelProperty(notes = "商家G米每天定时转换小米（默认0%，与商家转换大米相加100%）")
    private Double merchantWPointConvertRPointRate;

    @ApiModelProperty(notes = "代理商G米每天转换成大米比例（默认100%）")
    private Double agentWPointConvertBalanceRate;

    @ApiModelProperty(notes = "代理商G米每天定时转换小米（默认0%，与代理商G米每天转换成大米相加100%）")
    private Double agentWPointConvertRPointRate;

    @ApiModelProperty(notes = "用户、商家 G米转换大米平台手续费（默认10%）")
    private Double wpointConvertFee;

    @ApiModelProperty(notes = "用户、商家 G米转换小米平台手续费（默认10%）")
    private Double wpointConvertRPointFee;


    @ApiModelProperty(notes = "大米提现手续费(默认10%)")
    private Double balanceWithdrawFee;

    @ApiModelProperty(notes = "商家小米转化成大米手续费（默认10%）")
    private Double merchantRPointConvertBalanceRate;

    @ApiModelProperty(notes = "普通店铺保证金（冻结不可提现，默认1000）")
    private Double shopCashDeposit;

    @ApiModelProperty(notes = "普通店铺是否支持小米购买（默认1：支持）")
    private Boolean shopSupportRPoint;

    @ApiModelProperty(notes = "线下订单是否支持小米购买（默认1：支持）")
    private Boolean offlineOrdersSupportRPoint;

    @ApiModelProperty(notes = "开通普通店铺费用（默认365元）")
    private Double openShopFee;

    @ApiModelProperty(notes = "会员权益:一级下线购买时的G米分成，默认5%")
    private Double firstSharing;

    @ApiModelProperty(notes = "会员权益:二级下线购买时的G米分成，默认2.5%")
    private Double secondSharing;

    @ApiModelProperty(notes = "会员权益:下线为商家，商家获取的商家G米分成为3%")
    private Double merchantSharing;

    @ApiModelProperty(notes = "注册送的存量G米数量")
    private Double registerGiveWPointNum;

    @ApiModelProperty(notes = "注册送的G米数量")
    private Double registerWPoint;

    @ApiModelProperty(notes = "注册送推荐人的G米数量")
    private Double registerParentWPoint;

    @ApiModelProperty(notes = "成为商家送的存量G米")
    private Double beMerchantWPointNum;

    @ApiModelProperty(notes = "成为商家送N倍的商家G米")
    private Double beMerchantWPointRate;

    @ApiModelProperty(notes = "成为商家送推荐人的G米")
    private Double beMerchantParentWPoint;

    @ApiModelProperty(notes = "成为商家送推荐人的大米")
    private Double beMerchantParentBalance;



    @ApiModelProperty(notes = "让利比范围，最小值（默认5%）")
    private Double benefitRateMin;

    @ApiModelProperty(notes = "让利比范围，最大值（默认100%）")
    private Double benefitRateMax;

    @ApiModelProperty(notes = "消费者获得G米的让利比基准 （默认20%）")
    private Double consumerWPointRate;

    @ApiModelProperty(notes = "商家获得的G米基准系数（默认15%）")
    private Double benefitRateDatum;

    @ApiModelProperty(notes = "每个账户最多能存入余粮公社金额")
    private Double maxSurplusGrain;

    @ApiModelProperty(notes = "存入余粮公社后，几天后收益G米")
    private Integer surplusGrainStartDay;

    @ApiModelProperty(notes = "每存入余粮公社1元，每天可获得G米数量")
    private Double surplusGrainRate;
}
