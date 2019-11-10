package com.lky.dto;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 图片dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/6/6
 */
@ApiModel(value = "ImageDto", description = "系统图片库")
public class ImageDto extends BaseEntity {

    @ApiModelProperty("主键")
    private int id;

    @ApiModelProperty("图片url")
    private String url;

    @ApiModelProperty(notes = "图片link")
    private String link;

    @ApiModelProperty(notes = "图片类型")
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
