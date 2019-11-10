package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 订单退款dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@ApiModel(value = "OrdersReturnDto", description = "订单退款dto")
public class OrdersReturnDto {

    @ApiModelProperty(notes = "订单号")
    private String ordersId;

    @ApiModelProperty(notes = "子订单项")
    private Integer ordersItemId;

    @ApiModelProperty(notes = "退款类型,退款、换货、全退", allowableValues = "refund,exchange,all")
    private String returnType;

    @ApiModelProperty(notes = "退款理由")
    private String returnReason;

    @ApiModelProperty(notes = "退换货说明")
    private String remark;

    @ApiModelProperty(notes = "证据图片")
    private List<Image> proofImgList;

    public String getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(String ordersId) {
        this.ordersId = ordersId;
    }

    public Integer getOrdersItemId() {
        return ordersItemId;
    }

    public void setOrdersItemId(Integer ordersItemId) {
        this.ordersItemId = ordersItemId;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Image> getProofImgList() {
        return proofImgList;
    }

    public void setProofImgList(List<Image> proofImgList) {
        this.proofImgList = proofImgList;
    }
}
