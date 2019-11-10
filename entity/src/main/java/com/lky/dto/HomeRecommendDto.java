package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 首页推荐
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/10
 */
@ApiModel(value = "HomeRecommendDto", description = "首页推荐")
public class HomeRecommendDto {

    private Integer id;

    @ApiModelProperty(notes = "宣传图")
    private Image showImg;

    @ApiModelProperty(notes = "类型，商品、店铺", allowableValues = "product,shop")
    private String targetType;

    @ApiModelProperty(notes = "目标id")
    private Integer targetId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Image getShowImg() {
        return showImg;
    }

    public void setShowImg(Image showImg) {
        this.showImg = showImg;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }
}
