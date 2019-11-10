package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 余粮公社Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/27
 */
public class SurplusGrainDto {

    @ApiModelProperty(notes = "用户id")
    private int id;

    @ApiModelProperty(notes = "大米数量")
    private double balance;

    @ApiModelProperty(notes = "用户账户余粮公社")
    private double surplusGrain;

    @ApiModelProperty(notes = "确认中余粮公社")
    private double confirmSurplusGrain;

    @ApiModelProperty(notes = "昨日收益G米")
    private double yesterdayIncomeWPoint;

    @ApiModelProperty(notes = "计算收益中金额")
    private double calculateSurplusGrain;

    @ApiModelProperty(notes = "大米自动转余粮公社")
    private Boolean automaticSurplusGrain;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSurplusGrain() {
        return surplusGrain;
    }

    public void setSurplusGrain(double surplusGrain) {
        this.surplusGrain = surplusGrain;
    }

    public double getConfirmSurplusGrain() {
        return confirmSurplusGrain;
    }

    public void setConfirmSurplusGrain(double confirmSurplusGrain) {
        this.confirmSurplusGrain = confirmSurplusGrain;
    }

    public double getYesterdayIncomeWPoint() {
        return yesterdayIncomeWPoint;
    }

    public void setYesterdayIncomeWPoint(double yesterdayIncomeWPoint) {
        this.yesterdayIncomeWPoint = yesterdayIncomeWPoint;
    }

    public double getCalculateSurplusGrain() {
        return calculateSurplusGrain;
    }

    public void setCalculateSurplusGrain(double calculateSurplusGrain) {
        this.calculateSurplusGrain = calculateSurplusGrain;
    }

    public Boolean getAutomaticSurplusGrain() {
        return automaticSurplusGrain;
    }

    public void setAutomaticSurplusGrain(Boolean automaticSurplusGrain) {
        this.automaticSurplusGrain = automaticSurplusGrain;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "SurplusGrainDto{" +
                "id=" + id +
                ", balance=" + balance +
                ", surplusGrain=" + surplusGrain +
                ", confirmSurplusGrain=" + confirmSurplusGrain +
                ", yesterdayIncomeWPoint=" + yesterdayIncomeWPoint +
                ", calculateSurplusGrain=" + calculateSurplusGrain +
                ", automaticSurplusGrain=" + automaticSurplusGrain +
                '}';
    }
}
