package com.lky.mapper;

import com.lky.dto.ShopKindDto;
import com.lky.entity.ShopKind;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 店铺内分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/23
 */
@Mapper(componentModel = "jsr330")
public interface ShopKindMapper {

    ShopKindDto toDto(ShopKind shopKind);

    ShopKind fromDto(ShopKindDto shopKindDto);

    List<ShopKindDto> toDtoList(List<ShopKind> shopKindList);
}
