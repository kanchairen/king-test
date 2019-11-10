package com.lky.dto;

import com.lky.entity.Area;

import java.util.List;

/**
 * 地区及其子地区
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/19
 */
public class AreaChildDto {

    private Area area;

    private List<AreaChildDto> areaChild;

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public List<AreaChildDto> getAreaChild() {
        return areaChild;
    }

    public void setAreaChild(List<AreaChildDto> areaChild) {
        this.areaChild = areaChild;
    }
}
