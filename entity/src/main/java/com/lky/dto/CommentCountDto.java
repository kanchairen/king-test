package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 商品各维度评论计数Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/30
 */
public class CommentCountDto {

    @ApiModelProperty(notes = "好评数")
    private Integer high = 0;

    @ApiModelProperty(notes = "中评数")
    private Integer middle = 0;

    @ApiModelProperty(notes = "差评数")
    private Integer low = 0;

    @ApiModelProperty(notes = "追加评论数")
    private Integer append = 0;

    @ApiModelProperty(notes = "有图评论数")
    private Integer haveImage = 0;

    @ApiModelProperty(notes = "总评论数")
    private Integer allNumber = 0;

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getMiddle() {
        return middle;
    }

    public void setMiddle(Integer middle) {
        this.middle = middle;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getAppend() {
        return append;
    }

    public void setAppend(Integer append) {
        this.append = append;
    }

    public Integer getHaveImage() {
        return haveImage;
    }

    public void setHaveImage(Integer haveImage) {
        this.haveImage = haveImage;
    }

    public Integer getAllNumber() {
        return allNumber;
    }

    public void setAllNumber(Integer allNumber) {
        this.allNumber = allNumber;
    }
}
