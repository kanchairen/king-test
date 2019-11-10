package com.lky.mapper;

import com.lky.dto.HomeRecommendDto;
import com.lky.entity.HomeRecommend;
import org.mapstruct.Mapper;

/**
 * 首页推荐
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/10
 */
@Mapper(componentModel = "jsr330")
public interface HomeRecommendMapper {

    HomeRecommend formDto(HomeRecommendDto homeRecommendDto);

}
