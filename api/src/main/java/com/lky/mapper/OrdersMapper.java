package com.lky.mapper;

import com.lky.dto.*;
import com.lky.entity.Orders;
import com.lky.entity.OrdersItem;
import com.lky.service.OrdersItemService;
import com.lky.service.ShopService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.inject.Inject;
import java.util.List;

/**
 * 线上订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/2
 */
@Mapper(componentModel = "jsr330")
public abstract class OrdersMapper {

    @Inject
    private ShopService shopService;

    @Inject
    private ShopMapper shopMapper;

    @Inject
    private OrdersItemService ordersItemService;

    @Inject
    private OrdersItemMapper ordersItemMapper;

    @Mappings({
            @Mapping(source = "shopId", target = "shopHeadDto"),
            @Mapping(source = "id", target = "ordersItemDtoList"),
    })
    public abstract OrdersDetailDto toDetailDto(Orders orders);

    @Mappings({
            @Mapping(source = "shopId", target = "shopHeadDto"),
            @Mapping(source = "id", target = "ordersItemDtoList"),
    })
    public abstract OrdersListDto toListDto(Orders orders);

    public abstract List<OrdersListDto> toListDtoList(List<Orders> ordersList);

    @Mappings({
            @Mapping(source = "id", target = "ordersItemDtoList"),
    })
    public abstract BOrdersListDto toBListDto(Orders orders);

    public abstract List<BOrdersListDto> toBListDtoList(List<Orders> ordersList);

    public ShopHeadDto toShopHeadDto(Integer shopId) {
        return shopMapper.toHeadDto(shopService.findById(shopId));
    }

    public List<OrdersItemDto> toOrdersItemDtoList(String id) {
        List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId(id);
        return ordersItemMapper.toListDto(ordersItemList);
    }
}
