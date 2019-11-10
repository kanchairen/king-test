package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 活动二维码可跳转URL条件Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/2/28
 */
public class QrJumpDto {

    @ApiModelProperty(notes = "是否跳转URL连接")
    private Boolean redirect;

    @ApiModelProperty(notes = "跳转URL门槛（元）")
    private Double threshold;

    @ApiModelProperty(notes = "跳转URL")
    private String url;

    public Boolean getRedirect() {
        return redirect;
    }

    public void setRedirect(Boolean redirect) {
        this.redirect = redirect;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "QrJumpDto{" +
                "redirect=" + redirect +
                ", threshold=" + threshold +
                ", url='" + url + '\'' +
                '}';
    }
}
