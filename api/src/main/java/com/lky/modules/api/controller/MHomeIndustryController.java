package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.HomeIndustry;
import com.lky.service.HomeIndustryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * 线下店铺首页推荐的店铺行业类型
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@RestController
@RequestMapping("api/home/industry")
@Api(value = "api/home/industry")
public class MHomeIndustryController extends BaseController {

    @Inject
    private HomeIndustryService homeIndustryService;

    @ApiOperation(value = "行业列表", response = HomeIndustry.class, notes = "homeIndustryList", responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list() {

        List<HomeIndustry> homeIndustryList = homeIndustryService.findAll(new Sort(Sort.Direction.DESC, "sortIndex"));

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("homeIndustryList", homeIndustryList);
        return responseInfo;
    }
}
