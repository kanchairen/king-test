package com.lky.dto;

import com.lky.entity.SRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 管理员子账号dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/13
 */
@ApiModel(value = "subSUserDto", description = "管理员子账号dto")
public class SubSUserDto {

    @ApiModelProperty(notes = "数据库主键")
    private int id;

    @ApiModelProperty(notes = "父id")
    private Integer parentId;

    @ApiModelProperty(notes = "登录用户名")
    private String username;

    @ApiModelProperty(notes = "登录密码")
    private String password;

    @ApiModelProperty(notes = "手机号码")
    private String mobile;

    @ApiModelProperty(notes = "状态", allowableValues = "lock,active")
    private String state;

    @ApiModelProperty(notes = "权限列表")
    private List<SRole> sroleList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<SRole> getSroleList() {
        return sroleList;
    }

    public void setSroleList(List<SRole> sroleList) {
        this.sroleList = sroleList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SubSUserDto{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mobile='" + mobile + '\'' +
                ", state='" + state + '\'' +
                ", sroleList=" + sroleList +
                '}';
    }
}
