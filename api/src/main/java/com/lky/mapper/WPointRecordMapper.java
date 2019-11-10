package com.lky.mapper;

import com.lky.dto.WPointRecordDto;
import com.lky.entity.WPointRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 赠送G米记录dto转换
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/6
 */
@Mapper(componentModel = "jsr330")
public abstract class WPointRecordMapper {

    @Mappings({
            @Mapping(source = "user.mobile", target = "mobile"),
            @Mapping(source = "user.nickname", target = "nickname"),
    })
    public abstract WPointRecordDto toDto(WPointRecord wPointRecord);

    public abstract List<WPointRecordDto> toDtoList(List<WPointRecord> wPointRecordList);

}
