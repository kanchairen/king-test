package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 大米，小米，G米明细记录表
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/7
 */
@ApiModel(value = "DetailRecordDto", description = "大米，小米，G米明细记录表")
public class DetailRecordDto {

    @ApiModelProperty(notes = "id")
    private int id;

    @ApiModelProperty(notes = "变动类型")
    private String type;

    @ApiModelProperty(notes = "变动类型-中文显示")
    private String showType;

    @ApiModelProperty(notes = "改变之后的数量")
    private double current = 0;

    @ApiModelProperty(notes = "变动的数量（正为加，负为减）")
    private double change = 0;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime = new Date();

    @ApiModelProperty(notes = "余粮公社开始计算收益时间")
    private Date incomeTime;

    @ApiModelProperty(notes = "余粮公社转入资金状态(已确认，确认中)")
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getIncomeTime() {
        return incomeTime;
    }

    public void setIncomeTime(Date incomeTime) {
        this.incomeTime = incomeTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "DetailRecordDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", showType='" + showType + '\'' +
                ", current=" + current +
                ", change=" + change +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", incomeTime=" + incomeTime +
                ", state='" + state + '\'' +
                '}';
    }
}
