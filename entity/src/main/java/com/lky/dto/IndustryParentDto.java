package com.lky.dto;

import com.lky.entity.Image;
import com.lky.entity.Industry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 行业dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@ApiModel(value = "IndustryParentDto", description = "店铺行业")
public class IndustryParentDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "行业名称")
    private String name;

    @ApiModelProperty(notes = "父类")
    private Industry parent;

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

    public Industry getParent() {
        return parent;
    }

    public void setParent(Industry parent) {
        this.parent = parent;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "IndustryParentDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", icon=" + icon +
                '}';
    }
}
