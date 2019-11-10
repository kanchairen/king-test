package com.lky.dto;

import com.lky.entity.ReceiveAddress;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 确认订单列表vo
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@ApiModel(value = "OrdersConfirmListVo", description = "确认订单列表vo")
public class OrdersConfirmListVo {

    @ApiModelProperty(notes = "选中的地址, 如果没有默认地址")
    private ReceiveAddress receiveAddress;

    @ApiModelProperty(notes = "确认订单vo列表")
    private List<OrdersConfirmVo> ordersConfirmVos;

    public ReceiveAddress getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(ReceiveAddress receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public List<OrdersConfirmVo> getOrdersConfirmVos() {
        return ordersConfirmVos;
    }

    public void setOrdersConfirmVos(List<OrdersConfirmVo> ordersConfirmVos) {
        this.ordersConfirmVos = ordersConfirmVos;
    }
}
