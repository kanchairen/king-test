package com.lky.mapper;

import com.lky.dto.InformationDto;
import com.lky.entity.Information;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 帮助中心
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Mapper(componentModel = "jsr330")
public interface InformationMapper {

    InformationDto toDto(Information information);

    List<InformationDto> toPageDto(List<Information> informationList);
}
