package com.lky.mapper;

import com.lky.dto.CustomTextDto;
import com.lky.entity.CustomText;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 自定义文本
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Mapper(componentModel = "jsr330")
public interface CustomTextMapper {

    CustomTextDto toDto(CustomText customText);

    List<CustomTextDto> toPageDto(List<CustomText> customTextList);
}
