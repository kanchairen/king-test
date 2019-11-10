package com.lky.dto;

import com.lky.entity.Area;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 代理商区域
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/22
 */
@ApiModel(value = "AgentArea", description = "代理商区域")
public class AgentArea {

    @ApiModelProperty(notes = "地址所在省")
    private Area province;

    @ApiModelProperty(notes = "地址所在市")
    private Area city;

    @ApiModelProperty(notes = "地址所在区")
    private Area district;

    public Area getProvince() {
        return province;
    }

    public void setProvince(Area province) {
        this.province = province;
    }

    public Area getCity() {
        return city;
    }

    public void setCity(Area city) {
        this.city = city;
    }

    public Area getDistrict() {
        return district;
    }

    public void setDistrict(Area district) {
        this.district = district;
    }
}
