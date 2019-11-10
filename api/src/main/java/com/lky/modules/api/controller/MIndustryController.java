package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.IndustryChildDto;
import com.lky.entity.Industry;
import com.lky.mapper.IndustryMapper;
import com.lky.service.IndustryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 行业类型
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@RestController
@RequestMapping("api/industry")
@Api(value = "api/industry", description = "行业类型")
public class MIndustryController extends BaseController {

    @Inject
    private IndustryService industryService;

    @Inject
    private IndustryMapper industryMapper;

    @ApiOperation(value = "获取行业信息", response = IndustryChildDto.class, notes = "industryList", responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list() {
        List<IndustryChildDto> industryChildList = new ArrayList<>();
        List<Industry> industryList = industryService.listByParentId(null);
        industryList.forEach(industry -> {
            IndustryChildDto industryChild = new IndustryChildDto();
            List<Industry> industryList1 = industryService.listByParentId(industry.getId());
            industryChild.setIndustry(industryMapper.toDto(industry));
            industryChild.setChildIndustryList(industryMapper.toListDto(industryList1));
            industryChildList.add(industryChild);
        });
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("industryList", industryChildList);
        return responseInfo;
    }
}
