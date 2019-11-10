package com.lky.dto;

import com.lky.entity.Banner;
import io.swagger.annotations.ApiModelProperty;

/**
 * bannerDto 增加是否为积分商城
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/12
 */
public class BannerDto extends Banner {

    @ApiModelProperty(notes = "目标开通小米商城")
    private Boolean openRPoint;

    @ApiModelProperty(notes = "目标开通G米商城")
    private Boolean openWPoint;

    public Boolean getOpenRPoint() {
        return openRPoint;
    }

    public void setOpenRPoint(Boolean openRPoint) {
        this.openRPoint = openRPoint;
    }

    public Boolean getOpenWPoint() {
        return openWPoint;
    }

    public void setOpenWPoint(Boolean openWPoint) {
        this.openWPoint = openWPoint;
    }
}
