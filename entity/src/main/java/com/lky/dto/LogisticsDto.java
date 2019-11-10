package com.lky.dto;

import com.lky.commons.logistics.Trace;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 物流跟踪详情
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@ApiModel(value = "LogisticsDto", description = "物流跟踪详情")
public class LogisticsDto {

    @ApiModelProperty(notes = "快递状态,0:暂无快递信息,2:在途中,3:签收,4:问题件")
    private String state;

    @ApiModelProperty(notes = "快递公司名称")
    private String expressName;

    @ApiModelProperty(notes = "快递单号")
    private String expressOdd;

    @ApiModelProperty(notes = "快递跟踪信息")
    private List<Trace> Traces;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressOdd() {
        return expressOdd;
    }

    public void setExpressOdd(String expressOdd) {
        this.expressOdd = expressOdd;
    }

    public List<Trace> getTraces() {
        return Traces;
    }

    public void setTraces(List<Trace> traces) {
        Traces = traces;
    }

    @Override
    public String toString() {
        return "LogisticsDto{" +
                "state='" + state + '\'' +
                ", expressName='" + expressName + '\'' +
                ", expressOdd='" + expressOdd + '\'' +
                ", Traces=" + Traces +
                '}';
    }
}
