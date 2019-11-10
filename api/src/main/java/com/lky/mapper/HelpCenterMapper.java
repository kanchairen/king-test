package com.lky.mapper;

import com.lky.dto.HelpCenterDto;
import com.lky.entity.HelpCenter;
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
public interface HelpCenterMapper {

    HelpCenterDto toDto(HelpCenter helpCenter);

    List<HelpCenterDto> toPageDto(List<HelpCenter> helpCenterList);
}
