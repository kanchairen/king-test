package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ConfigDto;
import com.lky.entity.BaseConfig;
import com.lky.entity.HighConfig;
import com.lky.service.BaseConfigService;
import com.lky.service.ComputeService;
import com.lky.service.ConvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * 高级配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/11
 */
@RestController
@RequestMapping("api/high/config")
@Api(value = "api/high/config", description = "高级配置")
public class MHighConfigController extends BaseController {

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private ConvertService convertService;

    @ApiOperation(value = "配置详情", response = ConfigDto.class, notes = "config")
    @GetMapping(value = "")
    public ResponseInfo get() {

        HighConfig highConfig = baseConfigService.findH();
        BaseConfig baseConfig = baseConfigService.find();

        ConfigDto configDto = new ConfigDto();
        configDto.setHighConfig(convertService.toDto(highConfig));
        configDto.setBaseConfig(baseConfig);
        configDto.setRpointRate(ComputeService.R_POINT_RATE);
        configDto.setWpointRate(ComputeService.W_POINT_RATE);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("config", configDto);
        return responseInfo;
    }
}
