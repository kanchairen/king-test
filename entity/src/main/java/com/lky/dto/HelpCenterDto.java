package com.lky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 帮助中心dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@ApiModel(value = "HelpCenterDto", description = "帮助中心dto")
public class HelpCenterDto {

    private int id;

    @ApiModelProperty(notes = "类型，用户、商家、运营", allowableValues = "user,merchant,operation")
    private String type;

    @ApiModelProperty(notes = "标题")
    private String title;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
