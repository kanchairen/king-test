package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.HomeIndustry;
import com.lky.service.HomeIndustryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("sys/home/industry")
@Api(value = "sys/home/industry", description = "线下店铺首页推荐的店铺行业类型")
public class SHomeIndustryController extends BaseController {

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

    @ApiOperation(value = "保存行业列表", response = HomeIndustry.class, notes = "homeIndustryList", responseContainer = "List")
    @PutMapping(value = "")
    public ResponseInfo save(@ApiParam(name = "homeIndustryList", value = "行业列表")
                             @RequestBody List<HomeIndustry> homeIndustryList) {

        if (!CollectionUtils.isEmpty(homeIndustryList)) {
            AssertUtils.isTrue(PublicResCode.PARAMS_EXCEPTION, homeIndustryList.size() <= 7);
        }

        homeIndustryService.modify(homeIndustryList);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("homeIndustryList", homeIndustryList);
        return responseInfo;
    }
}
