package com.lky.mapper;

import com.lky.dto.OfflineOrdersDto;
import com.lky.entity.OfflineOrders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 线下订单
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/2
 */
@Mapper(componentModel = "jsr330")
public abstract class OfflineOrdersMapper {

    @Mappings({
            @Mapping(source = "shop.id", target = "shopId"),
            @Mapping(source = "shop.name", target = "shopName"),
            @Mapping(source = "user.mobile", target = "mobile"),
    })
    public abstract OfflineOrdersDto toDto(OfflineOrders offlineOrders);

    public abstract List<OfflineOrdersDto> toDtoList(List<OfflineOrders> offlineOrdersList);

}
