package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.dto.AreaChildDto;
import com.lky.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * 地区管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/26
 */
@RestController
@RequestMapping("biz/area")
@Api(value = "biz/area", description = "地区管理")
public class BAreaController extends BaseController {

    @Inject
    private AreaService areaService;

    @ApiOperation(value = "获取所有省市区", notes = "areaList", response = AreaChildDto.class, responseContainer = "List")
    @GetMapping(value = "list/all")
    public ResponseInfo listAll() {
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("areaList", areaService.findListAll());
        return responseInfo;
    }
}
