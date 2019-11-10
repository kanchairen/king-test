package com.lky.mapper;

import com.lky.dto.IndustryDto;
import com.lky.entity.Industry;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 店铺行业dto转换
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@Mapper(componentModel = "jsr330")
public interface IndustryMapper {

    IndustryDto toDto(Industry industry);

    Industry fromDto(IndustryDto industryDto);

    List<IndustryDto> toListDto(List<Industry> industryList);

    List<Industry> fromListDto(List<IndustryDto> industryDtoList);
}
