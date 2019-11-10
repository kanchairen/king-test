package com.lky.dto;

import com.lky.entity.Area;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 地址Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/17
 */
@ApiModel(value = "AddressDto", description = "地址dto")
public class AddressDto {

    @ApiModelProperty(notes = "地址所在省")
    private Area province;

    @ApiModelProperty(notes = "地址所在市")
    private Area city;

    @ApiModelProperty(notes = "地址所在区")
    private Area district;

    @ApiModelProperty(notes = "详细地址")
    private String detail;

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
