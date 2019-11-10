package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 确认订单列表dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@ApiModel(value = "OrdersConfirmListDto", description = "确认订单列表dto")
public class OrdersConfirmListDto {

    @ApiModelProperty(notes = "选中的地址id, 如果没有默认地址")
    private Integer addressId;

    @ApiModelProperty(notes = "确认订单dto列表")
    private List<OrdersConfirmDto> ordersConfirmDtos;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public List<OrdersConfirmDto> getOrdersConfirmDtos() {
        return ordersConfirmDtos;
    }

    public void setOrdersConfirmDtos(List<OrdersConfirmDto> ordersConfirmDtos) {
        this.ordersConfirmDtos = ordersConfirmDtos;
    }
}
