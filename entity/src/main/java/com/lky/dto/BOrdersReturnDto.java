package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import java.util.List;

/**
 * B端订单退款dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@ApiModel(value = "BOrdersReturnDto", description = "B端订单退款dto")
public class BOrdersReturnDto {

    @ApiModelProperty(notes = "订单退款id")
    private Integer id;

    @ApiModelProperty(notes = "退款类型,退款、换货、全退", allowableValues = "refund,exchange,all")
    private String returnType;

    @ApiModelProperty(notes = "退款理由")
    private String returnReason;

    @ApiModelProperty(notes = "退换货说明")
    private String remark;

    @ApiModelProperty(notes = "证据图片")
    private List<Image> proofImgList;

    @ApiModelProperty(notes = "退款金额")
    @Column(nullable = false)
    private double price;

    @ApiModelProperty(notes = "退款小米")
    private double rpointPrice;

    @ApiModelProperty(notes = "退款G米")
    private double wpointPrice;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRpointPrice() {
        return rpointPrice;
    }

    public void setRpointPrice(double rpointPrice) {
        this.rpointPrice = rpointPrice;
    }

    public double getWpointPrice() {
        return wpointPrice;
    }

    public void setWpointPrice(double wpointPrice) {
        this.wpointPrice = wpointPrice;
    }
}
