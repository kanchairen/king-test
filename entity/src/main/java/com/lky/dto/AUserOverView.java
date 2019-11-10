package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 代理商总览
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/26
 */
public class AUserOverView {

    @ApiModelProperty(notes = "id")
    private Integer id;

    @ApiModelProperty(value = "代理级别(省级代理，市级代理，区级代理)", allowableValues = "province,city,district")
    private String level;

    @ApiModelProperty(value = "代理区域")
    private String area;

    @ApiModelProperty(notes = "董事长姓名")
    private String chairmanName;

    @ApiModelProperty(notes = "状态", allowableValues = "lock,active")
    private String state;

    @ApiModelProperty(notes = "大米")
    private double balance;

    @ApiModelProperty(notes = "G米个数")
    private double wpoint;

    @ApiModelProperty(notes = "小米个数")
    private double rpoint;

    @ApiModelProperty(value = "代理金额")
    private double amount;

    @ApiModelProperty(value = "出资金额")
    private double payAmount;

    @ApiModelProperty(notes = "总收益金额")
    private double sumIncomeAmount;

    @ApiModelProperty(notes = "已倒扣金额")
    private double sumBackAmount;

    @ApiModelProperty(notes = "待倒扣金额")
    private double waitBackAmount;

    @ApiModelProperty(notes = "昨日收益金额")
    private double yesterdayIncome;

    @ApiModelProperty(notes = "昨日营业额")
    private double yesterdayConsumerAmount;

    @ApiModelProperty(notes = "近7日营业额")
    private double weekConsumerAmount;

    @ApiModelProperty(notes = "近30日营业额")
    private double monthConsumerAmount;

    @ApiModelProperty("可转化的G米")
    private double transWPoint;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getChairmanName() {
        return chairmanName;
    }

    public void setChairmanName(String chairmanName) {
        this.chairmanName = chairmanName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getWpoint() {
        return wpoint;
    }

    public void setWpoint(double wpoint) {
        this.wpoint = wpoint;
    }

    public double getRpoint() {
        return rpoint;
    }

    public void setRpoint(double rpoint) {
        this.rpoint = rpoint;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public double getSumIncomeAmount() {
        return sumIncomeAmount;
    }

    public void setSumIncomeAmount(double sumIncomeAmount) {
        this.sumIncomeAmount = sumIncomeAmount;
    }

    public double getSumBackAmount() {
        return sumBackAmount;
    }

    public void setSumBackAmount(double sumBackAmount) {
        this.sumBackAmount = sumBackAmount;
    }

    public double getWaitBackAmount() {
        return waitBackAmount;
    }

    public void setWaitBackAmount(double waitBackAmount) {
        this.waitBackAmount = waitBackAmount;
    }

    public double getYesterdayIncome() {
        return yesterdayIncome;
    }

    public void setYesterdayIncome(double yesterdayIncome) {
        this.yesterdayIncome = yesterdayIncome;
    }

    public double getYesterdayConsumerAmount() {
        return yesterdayConsumerAmount;
    }

    public void setYesterdayConsumerAmount(double yesterdayConsumerAmount) {
        this.yesterdayConsumerAmount = yesterdayConsumerAmount;
    }

    public double getWeekConsumerAmount() {
        return weekConsumerAmount;
    }

    public void setWeekConsumerAmount(double weekConsumerAmount) {
        this.weekConsumerAmount = weekConsumerAmount;
    }

    public double getMonthConsumerAmount() {
        return monthConsumerAmount;
    }

    public void setMonthConsumerAmount(double monthConsumerAmount) {
        this.monthConsumerAmount = monthConsumerAmount;
    }

    public double getTransWPoint() {
        return transWPoint;
    }

    public void setTransWPoint(double transWPoint) {
        this.transWPoint = transWPoint;
    }
}
