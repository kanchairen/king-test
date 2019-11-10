package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 资讯管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@ApiModel(value = "InformationDto", description = "资讯管理")
public class InformationDto {

    private Integer id;

    @ApiModelProperty(notes = "资讯类型，新闻动态、平台公告、商学院", allowableValues = "news,notice,college")
    private String type;

    @ApiModelProperty(notes = "标题")
    private String title;

    @ApiModelProperty(notes = "icon图标")
    private Image icon;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    @ApiModelProperty(notes = "是否为弹窗公告")
    private Boolean popUp;

    @ApiModelProperty(notes = "是否为悬浮公告")
    private Boolean suspend;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getPopUp() {
        return popUp;
    }

    public void setPopUp(Boolean popUp) {
        this.popUp = popUp;
    }

    public Boolean getSuspend() {
        return suspend;
    }

    public void setSuspend(Boolean suspend) {
        this.suspend = suspend;
    }
}
