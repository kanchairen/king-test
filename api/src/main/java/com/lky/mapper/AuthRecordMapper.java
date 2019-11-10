package com.lky.mapper;

import com.lky.dto.AuthRecordDto;
import com.lky.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.inject.Inject;
import java.util.List;

/**
 * 实名认证申请记录dto转换
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@Mapper(componentModel = "jsr330")
public abstract class AuthRecordMapper {

    @Inject
    private ImageMapper imageMapper;

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "authImgIds", target = "authImgList"),
    })
    public abstract AuthRecordDto toDto(AuthRecord authRecord);

    @Mappings({
            @Mapping(source = "userId", target = "user"),
            @Mapping(source = "authImgList", target = "authImgIds"),
    })
    public abstract AuthRecord fromDto(AuthRecordDto authRecordDto);

    public User idToUser(Integer id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }

    public String imgListToStr(List<Image> imgList) {
        return imageMapper.imgListToStr(imgList);
    }

    public List<Image> imgIdsToList(String imgIds) {
        return imageMapper.imgIdsToList(imgIds);
    }
}
