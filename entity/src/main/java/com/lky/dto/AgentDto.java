package com.lky.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lky.entity.AUserMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 代理商Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/21
 */
@ApiModel(value = "AgentDto", description = "代理商Dto")
public class AgentDto {

    @ApiModelProperty(notes = "id")
    private Integer id;

    @ApiModelProperty(notes = "父id")
    private Integer parentId;

    @ApiModelProperty(notes = "用户名称")
    private String username;

    @ApiModelProperty(notes = "登录密码，初始化密码12345678")
    @JsonIgnore
    private String password;

    @ApiModelProperty(notes = "董事长姓名，冗余添加/编辑不用传")
    private String chairmanName;

    @ApiModelProperty(notes = "董事长手机号码，冗余添加/编辑不用传")
    private String chairmanMobile;

    @ApiModelProperty(notes = "代理职位")
    private List<AUserMember> memberList;

    @ApiModelProperty(notes = "登录手机号码")
    private String mobile;

    @ApiModelProperty(notes = "状态", allowableValues = "lock,active")
    private String state;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否全部出资")
    private Boolean payAll;

    @ApiModelProperty(value = "代理级别(省级代理，市级代理，区级代理)", allowableValues = "province,city,district")
    private String level;

    @ApiModelProperty(value = "代理区域")
    private AgentArea area;

    @ApiModelProperty(value = "收益率")
    private double incomeRate;

    @ApiModelProperty(value = "开始代理时间")
    private Date beginAgentDate;

    @ApiModelProperty(value = "代理金额")
    private double amount;

    @ApiModelProperty(value = "出资金额")
    private double payAmount;

    @ApiModelProperty(value = "出资比例")
    private double payRate;

    @ApiModelProperty(value = "倒扣开始时间")
    private Date backBeginDate;

    @ApiModelProperty(value = "倒扣的百分比")
    private double backRate;

    @ApiModelProperty(notes = "已倒扣金额")
    private double sumBackAmount;

    @ApiModelProperty(notes = "待倒扣金额")
    private double waitBackAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AUserMember> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<AUserMember> memberList) {
        this.memberList = memberList;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getPayAll() {
        return payAll;
    }

    public void setPayAll(Boolean payAll) {
        this.payAll = payAll;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public AgentArea getArea() {
        return area;
    }

    public void setArea(AgentArea area) {
        this.area = area;
    }

    public double getIncomeRate() {
        return incomeRate;
    }

    public void setIncomeRate(double incomeRate) {
        this.incomeRate = incomeRate;
    }

    public Date getBeginAgentDate() {
        return beginAgentDate;
    }

    public void setBeginAgentDate(Date beginAgentDate) {
        this.beginAgentDate = beginAgentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public Date getBackBeginDate() {
        return backBeginDate;
    }

    public void setBackBeginDate(Date backBeginDate) {
        this.backBeginDate = backBeginDate;
    }

    public double getBackRate() {
        return backRate;
    }

    public void setBackRate(double backRate) {
        this.backRate = backRate;
    }

    public String getChairmanName() {
        return chairmanName;
    }

    public void setChairmanName(String chairmanName) {
        this.chairmanName = chairmanName;
    }

    public String getChairmanMobile() {
        return chairmanMobile;
    }

    public void setChairmanMobile(String chairmanMobile) {
        this.chairmanMobile = chairmanMobile;
    }

    public double getSumBackAmount() {
        return sumBackAmount;
    }

    public void setSumBackAmount(double sumBackAmount) {
        this.sumBackAmount = sumBackAmount;
    }

    public double getWaitBackAmount() {
        return waitBackAmount;
    }

    public void setWaitBackAmount(double waitBackAmount) {
        this.waitBackAmount = waitBackAmount;
    }

    public double getPayRate() {
        return payRate;
    }

    public void setPayRate(double payRate) {
        this.payRate = payRate;
    }

    @Override
    public String toString() {
        return "AgentDto{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", chairmanName='" + chairmanName + '\'' +
                ", chairmanMobile='" + chairmanMobile + '\'' +
                ", memberList=" + memberList +
                ", mobile='" + mobile + '\'' +
                ", state='" + state + '\'' +
                ", createTime=" + createTime +
                ", payAll=" + payAll +
                ", level='" + level + '\'' +
                ", area=" + area +
                ", incomeRate=" + incomeRate +
                ", beginAgentDate=" + beginAgentDate +
                ", amount=" + amount +
                ", payAmount=" + payAmount +
                ", payRate=" + payRate +
                ", backBeginDate=" + backBeginDate +
                ", backRate=" + backRate +
                ", sumBackAmount=" + sumBackAmount +
                ", waitBackAmount=" + waitBackAmount +
                '}';
    }


}

