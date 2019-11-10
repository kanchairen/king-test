package com.lky.mapper;

import com.lky.dto.BOrdersReturnDto;
import com.lky.dto.OrdersReturnDto;
import com.lky.entity.Image;
import com.lky.entity.OrdersReturn;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.inject.Inject;
import java.util.List;

/**
 * 订单退款
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@Mapper(componentModel = "jsr330")
public abstract class OrdersReturnMapper {

    @Inject
    private ImageMapper imageMapper;

    @Mappings({
            @Mapping(source = "proofImgList", target = "proofImgIds"),
    })
    public abstract OrdersReturn fromDto(OrdersReturnDto ordersReturnDto);

    @Mappings({
            @Mapping(source = "proofImgIds", target = "proofImgList"),
    })
    public abstract OrdersReturnDto toDto(OrdersReturn ordersReturn);

    @Mappings({
            @Mapping(source = "proofImgIds", target = "proofImgList"),
    })
    public abstract BOrdersReturnDto toBDTO(OrdersReturn ordersReturn);

    public String imgListToStr(List<Image> imgList) {
        return imageMapper.imgListToStr(imgList);
    }

    public List<Image> imgIdsToList(String imgIds) {
        return imageMapper.imgIdsToList(imgIds);
    }
}
