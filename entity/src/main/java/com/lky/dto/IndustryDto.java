package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 行业dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@ApiModel(value = "IndustryDto", description = "店铺行业")
public class IndustryDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "行业名称")
    private String name;

    @ApiModelProperty(notes = "父类id，支持多级")
    private Integer parentId;

    @ApiModelProperty(notes = "行业图片")
    private Image icon;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "IndustryDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", icon=" + icon +
                '}';
    }
}
