package com.lky.dto;

import com.lky.entity.SMenu;
import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * 权限导航
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/4/9
 */
@ApiModel(value = "SMenuDto", description = "权限导航")
public class SMenuDto{

    private SMenu sMenu;

    private List<SMenuDto> childList;

    private SMenu parent;

    private SMenu grandpa;

    public List<SMenuDto> getChildList() {
        return childList;
    }

    public void setChildList(List<SMenuDto> childList) {
        this.childList = childList;
    }

    public SMenu getsMenu() {
        return sMenu;
    }

    public void setsMenu(SMenu sMenu) {
        this.sMenu = sMenu;
    }

    public SMenu getParent() {
        return parent;
    }

    public void setParent(SMenu parent) {
        this.parent = parent;
    }

    public SMenu getGrandpa() {
        return grandpa;
    }

    public void setGrandpa(SMenu grandpa) {
        this.grandpa = grandpa;
    }
}
