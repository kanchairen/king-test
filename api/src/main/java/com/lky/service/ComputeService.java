package com.lky.service;

import com.lky.commons.utils.ArithUtils;
import com.lky.entity.HighConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 计算逻辑
 * <p>
 * public BigDecimal add(BigDecimal value);                        //加法
 * public BigDecimal subtract(BigDecimal value);                   //减法
 * public BigDecimal multiply(BigDecimal value);                   //乘法
 * public BigDecimal divide(BigDecimal value);                     //除法
 * </p>
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/30
 */
@Service
@Transactional
public class ComputeService {

    /**
     * 小米和现金（元）比例：1：0.01
     */
    public static final double R_POINT_RATE = 0.01;

    /**
     * G米和现金（元）比例：1：0.01
     */
    public static final double W_POINT_RATE = 0.01;

    @Inject
    private BaseConfigService baseConfigService;

    /**
     * 小米根据比例转金额
     *
     * @param rPoint 小米
     * @return 金额
     */
    public double rPoint2Price(double rPoint) {
        return ArithUtils.calculator(rPoint + "*" + R_POINT_RATE);
    }

    /**
     * 金额根据比例转换成小米
     *
     * @param price 金额
     * @return 小米
     */
    public double price2RPoint(double price) {
        return ArithUtils.calculator(price + "/" + R_POINT_RATE);
    }

    /**
     * G米根据比例转金额
     *
     * @param wPoint G米
     * @return 金额
     */
    public double wPoint2Price(double wPoint) {
        return ArithUtils.calculator(wPoint + "*" + W_POINT_RATE);
    }

    /**
     * 金额根据比例转换成G米
     *
     * @param price 金额
     * @return 金额
     */
    public double price2WPoint(double price) {
        return ArithUtils.calculator(price + "/" + W_POINT_RATE);
    }

    /**
     * 计算消费者获得的G米
     * 公式：消费额*（让利比/比例基数）*100
     *
     * @param price       消费额
     * @param benefitRate 让利比
     * @return 消费者获取的G米数量
     */
    public double consumerGiveWPoint(double price, double benefitRate) {
        //获取消费者获取G米比例基数
        HighConfig highConfig = baseConfigService.findH();
        double consumerWPointRate = highConfig.getConsumerWPointRate();
        return ArithUtils.calculator(price + "*((" + benefitRate + "/100)" + "/(" + consumerWPointRate + "/100))*100");
    }

    /**
     * 计算商家获得的G米
     * 公式：商家的到的G米 = 顾客消费额*让利比*2 *100
     * (ps:商家让利比大于等于5，小于15时，乘1；大于等于15时乘2)
     *
     * @param price       消费额
     * @param benefitRate 让利比
     * @return 商家获取的G米数量
     */
    public double merchantGiveWPoint(double price, double benefitRate) {
        //获取高级配置中让利比范围
        HighConfig highConfig = baseConfigService.findH();
        double benefitRateDatum = highConfig.getBenefitRateDatum();
        int var = 1;
        if (benefitRate < benefitRateDatum) {
            var = 1;
        } else if (benefitRate >= benefitRateDatum) {
            var = 2;
        }
        return ArithUtils.calculator(price + "*(" + benefitRate + "/100)*" + var + "*100");
    }

    /**
     * 计算商家获得的金额
     * 公式：商家得到的金额 = 顾客消费额*（1-让利比）
     *
     * @param price       消费额
     * @param benefitRate 让利比
     * @return 商家获取的现金金额
     */
    public double merchantGiveCash(double price, double benefitRate) {
        return ArithUtils.calculator(price + "*(1-(" + benefitRate + "/100))");
    }

    /**
     * 计算商家获得的小米
     * 公式：商家得到的小米 = 顾客消费小米*（1-让利比）
     *
     * @param rPoint      消费小米
     * @param benefitRate 让利比
     * @return 商家获取的小米
     */
    public double merchantGiveRPoint(double rPoint, double benefitRate) {
        return ArithUtils.calculator(rPoint + "*(1-(" + benefitRate + "/100))");
    }

    /**
     * 计算当天乐康指数
     * 公式：（前一天所有商家的现金成交额）*15%] /（累计到前一天所有G米）
     * 计算逻辑：
     * 如果手动设置了乐康指数，则取设置的值，
     * 否则根据公式计算出乐康指数，和设置的最小值进行比较，
     * 小于最小值，则取最小值，否则取公式计算的值
     *
     * @param sumMerchantOrdersPrice 前一天所有商家的现金成交额
     * @param sumTransWPoint         累计到前一天所有G米
     * @return 当天指数
     */
    public double lHealthIndex(double sumMerchantOrdersPrice, double sumTransWPoint) {
        HighConfig highConfig = baseConfigService.findH();
        double lhealthIndexBase = highConfig.getLhealthIndexBase();
        double lhealthIndex = (sumMerchantOrdersPrice * (lhealthIndexBase / 100)) / sumTransWPoint;
        BigDecimal bg = new BigDecimal(lhealthIndex);
        return bg.setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 计算商家G米转化成小米,定时发放
     * 公式：商家可激励G米*转化比例0%*(1-平台手续费10%)
     * 商家可激励G米---今天能分配的G米 除开冻结的和今天新增的
     *
     * @param transWPoint  商家可激励G米
     * @param lHealthIndex 乐康指数
     * @return 小米
     */
    public double merchantWPointConvertRPoint(double transWPoint, double lHealthIndex) {
        HighConfig highConfig = baseConfigService.findH();
        //转化平台手续费
        double wPointConvertFee = highConfig.getWpointConvertRPointFee();
        //转化比例
        double merchantWPointConvertRPointRate = highConfig.getMerchantWPointConvertRPointRate();
        return ArithUtils.calculator(transWPoint + "*" + lHealthIndex + "*(" + merchantWPointConvertRPointRate + "/100)*(1-(" + wPointConvertFee + "/100))");
    }

    /**
     * 计算商家G米转化成大米,定时发放
     * 公式：商家可激励G米*转化比例100%*(1-平台手续费10%)
     * 商家可激励G米---今天能分配的G米 除开冻结的和今天新增的
     *
     * @param transWPoint  商家可激励G米
     * @param lHealthIndex 乐康指数
     * @return 大米
     */
    public double merchantWPointConvertBalance(double transWPoint, double lHealthIndex) {
        HighConfig highConfig = baseConfigService.findH();
        //转化平台手续费
        double wPointConvertFee = highConfig.getWpointConvertFee();
        //转化比例
        double merchantWPointConvertBalanceRate = highConfig.getMerchantWPointConvertBalanceRate();
        return ArithUtils.calculator(transWPoint + "*" + W_POINT_RATE + "*" + lHealthIndex + "*(" + merchantWPointConvertBalanceRate + "/100)*(1-(" + wPointConvertFee + "/100))");
    }

    /**
     * 计算用户G米转化成小米,定时发放
     * 公式：用户可激励G米*乐康指数*转化比例20%*(1-平台手续费10%)
     * 用户可激励G米---今天能分配的G米 除开冻结的和今天新增的
     *
     * @param transWPoint  用户可激励G米
     * @param lHealthIndex 乐康指数
     * @return 小米
     */
    public double consumerWPointConvertRPoint(double transWPoint, double lHealthIndex) {
        HighConfig highConfig = baseConfigService.findH();
        //转化平台手续费
        double wPointConvertFee = highConfig.getWpointConvertRPointFee();
        //转化比例
        double wPointConvertRPointRate = highConfig.getWpointConvertRPointRate();
        return ArithUtils.calculator(transWPoint + "*" + lHealthIndex + "*(" + wPointConvertRPointRate + "/100)*(1-(" + wPointConvertFee + "/100))");
    }

    /**
     * 计算用户G米转化成大米,定时发放
     * 公式：用户可激励G米*0.01现金比例*乐康指数*转化比例80%*（1-平台手续费10%）
     * 用户可激励G米---今天能分配的G米 除开冻结的和今天新增的
     *
     * @param transWPoint  用户可激励G米
     * @param lHealthIndex 乐康指数
     * @return 大米
     */
    public double consumerWPointConvertBalance(double transWPoint, double lHealthIndex) {
        HighConfig highConfig = baseConfigService.findH();
        //转化平台手续费
        double wPointConvertFee = highConfig.getWpointConvertFee();
        //转化比例
        double wPointConvertBalanceRate = highConfig.getWpointConvertBalanceRate();
        return ArithUtils.calculator(transWPoint + "*" + W_POINT_RATE + "*" + lHealthIndex + "*(" + wPointConvertBalanceRate + "/100)*(1-(" + wPointConvertFee + "/100))");
    }

    /**
     * 计算商家小米转换成大米
     * 公式：商家需要转换的小米*(1-平台手续费10%）
     * 计算代理商用户G米转化成小米,定时发放
     * 公式：代理商用户可激励G米*乐康指数*转化比例20%*(1-平台手续费10%)
     * 代理商用户可激励G米---今天能分配的G米 除开冻结的和今天新增的
     *
     * @param transWPoint  用户可激励G米
     * @param lHealthIndex 乐康指数
     * @return 小米
     */
    public double agentWPointConvertRPoint(double transWPoint, double lHealthIndex) {
        HighConfig highConfig = baseConfigService.findH();
        //转化平台手续费
        double wPointConvertFee = highConfig.getWpointConvertRPointFee();
        //转化比例
        double wPointConvertRPointRate = highConfig.getAgentWPointConvertRPointRate();
        return ArithUtils.calculator(transWPoint + "*" + lHealthIndex + "*(" + wPointConvertRPointRate + "/100)*(1-(" + wPointConvertFee + "/100))");
    }

    /**
     * 计算代理商用户G米转化成大米,定时发放
     * 公式：代理商用户可激励G米*0.01现金比例*乐康指数*转化比例80%*（1-平台手续费10%）
     * 代理商用户可激励G米---今天能分配的G米 除开冻结的和今天新增的
     *
     * @param transWPoint  用户可激励G米
     * @param lHealthIndex 乐康指数
     * @return 大米
     */
    public double agentWPointConvertBalance(double transWPoint, double lHealthIndex) {
        HighConfig highConfig = baseConfigService.findH();
        //转化平台手续费
        double wPointConvertFee = highConfig.getWpointConvertFee();
        //转化比例
        double wPointConvertBalanceRate = highConfig.getAgentWPointConvertBalanceRate();
        return ArithUtils.calculator(transWPoint + "*" + W_POINT_RATE + "*" + lHealthIndex + "*(" + wPointConvertBalanceRate + "/100)*(1-(" + wPointConvertFee + "/100))");
    }

    /**
     * 计算商家小米转换成大米
     * 公式：商家需要转换的小米*(1-平台手续费10%）
     *
     * @param rPoint 商家需要转换的小米
     * @return 大米
     */
    public double merchantRPointConvertBalance(double rPoint) {
        HighConfig highConfig = baseConfigService.findH();
        double merchantRPointConvertBalanceRate = highConfig.getMerchantRPointConvertBalanceRate();
        return ArithUtils.calculator(rPoint + "*(1-(" + merchantRPointConvertBalanceRate + "/100))*" + R_POINT_RATE);
    }

    /**
     * 可激励G米转换掉的G米
     *
     * @param transWPoint  可激励的G米
     * @param lHealthIndex 乐康指数
     * @return 转换掉的G米
     */
    public double convertOverWPoint(double transWPoint, double lHealthIndex) {
        return ArithUtils.calculator(transWPoint + "*" + lHealthIndex);
    }

    /**
     * 转换掉的G米值
     *
     * @param convertOverWPoint         转换掉的用户G米值
     * @param merchantConvertOverWPoint 转换掉的商家G米值
     * @return map
     */
    public Map<String, Double> convertOverWPointMap(double convertOverWPoint, double merchantConvertOverWPoint) {
        HighConfig highConfig = baseConfigService.findH();

        Map<String, Double> map = new HashMap<>();
        if (convertOverWPoint > 0) {
            //转化比例
            double wPointConvertRPointRate = highConfig.getWpointConvertRPointRate();
            double wPointConvertBalanceRate = highConfig.getWpointConvertBalanceRate();

            map.put("consumerConvertRPoint", ArithUtils.calculator(convertOverWPoint + "*(" + wPointConvertRPointRate + "/100)"));
            map.put("consumerConvertBalance", ArithUtils.calculator(convertOverWPoint + "*(" + wPointConvertBalanceRate + "/100)"));
        }
        if (merchantConvertOverWPoint > 0) {
            //转化比例
            double merchantWPointConvertRPointRate = highConfig.getMerchantWPointConvertRPointRate();
            double merchantWPointConvertBalanceRate = highConfig.getMerchantWPointConvertBalanceRate();

            map.put("merchantConvertRPoint", ArithUtils.calculator(merchantConvertOverWPoint + "*(" + merchantWPointConvertRPointRate + "/100)"));
            map.put("merchantConvertBalance", ArithUtils.calculator(merchantConvertOverWPoint + "*(" + merchantWPointConvertBalanceRate + "/100)"));
        }
        return map;
    }

    /**
     * 转换掉的代理商G米值
     * 手动冻结G米
     * 公式：需要冻结的G米*冻结比例
     *
     * @param convertOverWPoint 转换掉的用户G米值
     * @return map
     */
    public Map<String, Double> convertOverAWPointMap(double convertOverWPoint) {
        HighConfig highConfig = baseConfigService.findH();

        Map<String, Double> map = new HashMap<>();
        if (convertOverWPoint > 0) {
            //转化比例
            double wPointConvertRPointRate = highConfig.getAgentWPointConvertRPointRate();
            double wPointConvertBalanceRate = highConfig.getAgentWPointConvertBalanceRate();

            map.put("agentConvertRPoint", ArithUtils.calculator(convertOverWPoint + "*(" + wPointConvertRPointRate + "/100)"));
            map.put("agentConvertBalance", ArithUtils.calculator(convertOverWPoint + "*(" + wPointConvertBalanceRate + "/100)"));
        }
        return map;
    }

    /**
     * 手动冻结G米
     * 公式：需要冻结的G米*冻结比例
     *
     * @param wPoint         需要冻结的G米
     * @param lockWPointRate 冻结比例
     * @return 冻结的G米
     */
    public double lockWPoint(double wPoint, double lockWPointRate) {
        return ArithUtils.calculator(wPoint + "*(" + lockWPointRate + "/100)");
    }

    /**
     * 计算下线分成、商家分成
     * 公式：需要分成的G米 * 分成比例
     *
     * @param wPoint      需要分成的G米
     * @param sharingRate 分成比例
     * @return 分成得到的G米
     */
    public double sharingWPoint(double wPoint, double sharingRate) {
        return ArithUtils.calculator(wPoint + "*(" + sharingRate + "/100)");
    }

    /**
     * 计算用户提现手续费
     *
     * @param amount       提现金额
     * @param withdrawRate 提现手续费率
     * @return 提现手续费
     */
    public double withdrawFee(double amount, double withdrawRate) {
        return ArithUtils.calculator(amount + "*(" + withdrawRate + "/100)");
    }

    /**
     * 计算代理商总收益金额
     *
     * @param wPoint 提现金额
     * @return 收益金额
     */
    public double agentSumIncomeAmount(double wPoint) {
        return ArithUtils.round(ArithUtils.mul(wPoint, W_POINT_RATE), 2);
    }

    /**
     * 计算代理商收益金额
     *
     * @param sumConsumerAmount 营业额
     * @param incomeRate        收益率
     * @param payRate           出资比例
     * @return 收益金额
     */
    public double agentIncomeAmount(double sumConsumerAmount, double incomeRate, double payRate) {
        return ArithUtils.round(ArithUtils.mul(sumConsumerAmount, ArithUtils.mul(ArithUtils.div(incomeRate, 100),
                ArithUtils.div(payRate, 100))), 2);
//        return ArithUtils.calculator(sumConsumerAmount + "*(" + incomeRate + "/100)*(" + payRate + "/100)");
    }

    /**
     * 计算代理商收益金额
     *
     * @param sumConsumerAmount 营业额
     * @param incomeRate        收益率
     * @param payRate           出资比例
     * @return 收益金额
     */
    public double agentIncomeWPoint(double sumConsumerAmount, double incomeRate, double payRate) {
        //a * (b / 100) * (1 - (c/100)) * 100
        return ArithUtils.round(ArithUtils.mul(ArithUtils.mul(ArithUtils.mul(sumConsumerAmount, ArithUtils.div(incomeRate, 100)),
                ArithUtils.sub(1, ArithUtils.div(payRate, 100))), 100), 2);
//        return ArithUtils.calculator(sumConsumerAmount + "*(" + incomeRate + "/100)*(1-(" + payRate + "/100))*100");
    }

    /**
     * 计算代理商倒扣金额
     *
     * @param incomeAmount 收益金额
     * @param backRate     倒扣比例
     * @return 倒扣金额
     */
    public double agentBackAmount(double incomeAmount, double backRate) {
        //a * (b/100)
        return ArithUtils.round(ArithUtils.mul(incomeAmount, ArithUtils.div(backRate, 100)), 2);
//        return ArithUtils.calculator(incomeAmount + "*(" + backRate + "/100)");
    }

    public static void main(String[] args) {
        double price = 100;
        double benefitRate = 5;
        double consumerWPointRate = 20;
        double rPoint = ArithUtils.calculator(price + "/" + R_POINT_RATE);
        //100元价格的商品，消耗的小米
        System.out.println(rPoint);
        //消费者获取的G米
        System.out.println(ArithUtils.calculator(price + "*((" + benefitRate + "/100)" + "/(" + consumerWPointRate + "/100))*100"));
        //商家获取的G米
        System.out.println(ArithUtils.calculator(price + "*(" + benefitRate + "/100)*" + 1 + "*100"));
        //商家获取的现金
        System.out.println(ArithUtils.calculator(price + "*(1-(" + benefitRate + "/100))"));
        //商家获取的小米
        System.out.println(ArithUtils.calculator(rPoint + "*(1-(" + benefitRate + "/100))"));

        System.out.println(ArithUtils.calculator(9002.9 + "*(1-(" + 10 + "/100))*" + R_POINT_RATE));

        System.out.println(ArithUtils.calculator("9498.6*0.0007"));

        System.out.println(ArithUtils.calculator(6.65 + "*(30/100)"));

        System.out.println(ArithUtils.calculator("9498.6*0.0007*(30/100)"));
    }
}
