package com.lky.mapper;

import com.lky.dto.OrdersItemDto;
import com.lky.entity.OrdersItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 订单子项
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@Mapper(componentModel = "jsr330")
public abstract class OrdersItemMapper {

    @Mappings({
            @Mapping(source = "product.id", target = "productId"),
            @Mapping(source = "product.name", target = "name"),
            @Mapping(source = "product.previewImg", target = "previewImg"),
            @Mapping(source = "product.spec", target = "spec"),
    })
    public abstract OrdersItemDto toDto(OrdersItem ordersItem);

    public abstract List<OrdersItemDto> toListDto(List<OrdersItem> ordersItemList);
}
