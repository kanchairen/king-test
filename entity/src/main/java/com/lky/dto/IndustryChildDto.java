package com.lky.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 行业及其行业
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/19
 */
public class IndustryChildDto {

    @ApiModelProperty(notes = "行业对象")
    private IndustryDto industry;

    @ApiModelProperty(notes = "子行业列表")
    private List<IndustryDto> childIndustryList;

    public IndustryDto getIndustry() {
        return industry;
    }

    public void setIndustry(IndustryDto industry) {
        this.industry = industry;
    }

    public List<IndustryDto> getChildIndustryList() {
        return childIndustryList;
    }

    public void setChildIndustryList(List<IndustryDto> childIndustryList) {
        this.childIndustryList = childIndustryList;
    }
}
