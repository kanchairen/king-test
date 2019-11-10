package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * 自定义文本dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@ApiModel(value = "CustomTextDto", description = "自定义文本dto")
public class CustomTextDto {

    private Integer id;

    @ApiModelProperty(notes = "标题")
    @Column(length = 32)
    private String title;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
