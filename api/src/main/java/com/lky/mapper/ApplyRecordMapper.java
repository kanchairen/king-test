package com.lky.mapper;

import com.lky.dto.ApplyRecordDto;
import com.lky.dto.IndustryParentDto;
import com.lky.entity.ApplyRecord;
import com.lky.entity.Image;
import com.lky.entity.Industry;
import com.lky.entity.User;
import com.lky.service.IndustryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * 店铺申请dto转换
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@Mapper(componentModel = "jsr330")
public abstract class ApplyRecordMapper {

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private IndustryService industryService;

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "shopBannerImgIds", target = "shopBannerImgList"),
            @Mapping(source = "shopLicenseImgIds", target = "shopLicenseImgList"),
            @Mapping(source = "industry", target = "industryParentDto"),
    })
    public abstract ApplyRecordDto toDto(ApplyRecord applyRecord);

    @Mappings({
            @Mapping(source = "userId", target = "user"),
            @Mapping(source = "shopBannerImgList", target = "shopBannerImgIds"),
            @Mapping(source = "shopLicenseImgList", target = "shopLicenseImgIds"),
            @Mapping(source = "industryParentDto", target = "industry"),
    })
    public abstract ApplyRecord fromDto(ApplyRecordDto applyRecordDto);

    public abstract List<ApplyRecordDto> toPageDto(List<ApplyRecord> applyRecordPage);

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

    public IndustryParentDto toIndustryDto(Industry industry) {
        IndustryParentDto industryParentDto = new IndustryParentDto();
        BeanUtils.copyProperties(industry, industryParentDto);
        industryParentDto.setParent(industryService.findById(industry.getParentId()));
        return industryParentDto;
    }

    public Industry fromIndustryDto(IndustryParentDto industryParentDto) {
        if (industryParentDto.getId() == null) {
            return null;
        }
        return industryService.findById(industryParentDto.getId());
    }

}
