package com.lky.dto;

import com.lky.entity.SMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 系统角色Dto
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@ApiModel(value = "SRoleDto", description = "系统角色")
public class SRoleDto {

    @ApiModelProperty(notes = "主键")
    private Integer id;

    @ApiModelProperty(notes = "角色名称")
    private String name;

    @ApiModelProperty(notes = "角色代号")
    private String code;

    @ApiModelProperty(notes = "角色描述")
    private String description;

    @ApiModelProperty(notes = "包含的权限列表")
    private List<SMenu> sMenuList;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SMenu> getsMenuList() {
        return sMenuList;
    }

    public void setsMenuList(List<SMenu> sMenuList) {
        this.sMenuList = sMenuList;
    }
}
