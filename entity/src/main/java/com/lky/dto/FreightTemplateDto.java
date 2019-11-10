package com.lky.dto;

import com.lky.entity.FreightRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import java.util.List;

/**
 * 运费模板Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/23
 */
@ApiModel(value = "FreightTemplateDto", description = "运费模板")
public class FreightTemplateDto {

    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "模板名称")
    private String name;

    @ApiModelProperty(notes = "发货地址")
    private AddressDto addressDto;

    @ApiModelProperty(notes = "配送时间")
    private int deliveryTime;

    @ApiModelProperty(notes = "价格类型（计件、重量、体积）", allowableValues = "num,weight,volume")
    private String priceType;

    @ApiModelProperty(notes = "店铺id")
    @Column(nullable = false)
    private int shopId;

    @ApiModelProperty(notes = "运费规则列表，单向一对多")
    private List<FreightRule> freightRuleList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressDto getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(AddressDto addressDto) {
        this.addressDto = addressDto;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public List<FreightRule> getFreightRuleList() {
        return freightRuleList;
    }

    public void setFreightRuleList(List<FreightRule> freightRuleList) {
        this.freightRuleList = freightRuleList;
    }

    @Override
    public String toString() {
        return "FreightTemplateDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sendAddress=" + addressDto +
                ", deliveryTime=" + deliveryTime +
                ", priceType='" + priceType + '\'' +
                ", shopId=" + shopId +
                ", freightRuleList=" + freightRuleList +
                '}';
    }
}
