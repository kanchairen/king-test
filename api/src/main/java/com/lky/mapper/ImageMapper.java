package com.lky.mapper;

import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.ImageDto;
import com.lky.entity.Image;
import com.lky.service.ImageService;
import org.mapstruct.Mapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片dto转换
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/6/6
 */
@Mapper(componentModel = "jsr330")
public abstract class ImageMapper {

    @Inject
    private ImageService imageService;

    public abstract ImageDto toDto(Image image);

    public abstract Image fromDto(ImageDto imageDto);

    public List<Image> imgIdsToList(String imgIds) {
        if (StringUtils.isEmpty(imgIds)) {
            return null;
        }
        String[] imgArray = imgIds.split(",");
        if (CollectionUtils.isEmpty(imgArray)) {
            return null;
        }
        List<Image> imgList = new ArrayList<>(imgArray.length);
        for (String imgId : imgArray) {
            if (StringUtils.isEmpty(imgId)) {
                continue;
            }
            Image image = imageService.findById(Integer.valueOf(imgId));
            if (image == null) {
                continue;
            }
            imgList.add(image);
        }
        return imgList;
    }

    public String imgListToStr(List<Image> imgList) {
        if (CollectionUtils.isEmpty(imgList)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        imgList.forEach(img -> {
            if (img != null) {
                builder.append(",").append(img.getId()).append(",");
            }
        });
        return builder.toString();
    }
}
