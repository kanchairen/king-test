package com.lky.dto;

import com.lky.entity.Category;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 一级分类下的所有分类详情
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/21
 */
@ApiModel(value = "CategoryDetailDto", description = "一级分类下的所有分类详情")
public class CategoryDetailDto {

    @ApiModelProperty(notes = "数据库主键")
    private Integer id;

    @ApiModelProperty(notes = "分类名称")
    private String name;

    @ApiModelProperty(notes = "三级分类列表")
    private List<Category> childCategory;

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

    public List<Category> getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(List<Category> childCategory) {
        this.childCategory = childCategory;
    }

    @Override
    public String toString() {
        return "CategoryDetailDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", childCategory=" + childCategory +
                '}';
    }
}
