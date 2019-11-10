package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.dto.AreaChildDto;
import com.lky.entity.Area;
import com.lky.global.annotation.AuthIgnore;
import com.lky.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 地区管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/16
 */
@RestController
@RequestMapping("api/area")
@Api(value = "api/area", description = "地区管理")
public class MAreaController extends BaseController {

    @Inject
    private AreaService areaService;

    @ApiOperation(value = "获取所有省市区", notes = "areaList", response = AreaChildDto.class, responseContainer = "List")
    @GetMapping(value = "list/all")
    @AuthIgnore
    public ResponseInfo listAll() {
        List<AreaChildDto> areaChildDtoList = new ArrayList<>();
        List<Area> areaListAll = areaService.findAll();
        areaListAll.forEach(areaProvince -> {
            //找到省份
            if ("province".equals(areaProvince.getType())) {
                areaChildDtoList.add(areaService.findByProvince(areaListAll, areaProvince));
            }
        });
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("areaList", areaChildDtoList);
        return responseInfo;
    }
}
