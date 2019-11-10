package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.BaseConfig;
import com.lky.service.BaseConfigService;
import com.lky.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * 基础配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@RestController
@RequestMapping(value = "sys/base/config")
@Api(value = "sys/base/config", description = "基础配置，客服配置，久天下")
public class SBaseConfigController extends BaseController {

    @Inject
    private BaseConfigService baseConfigService;

    @ApiOperation(value = "基础配置详情", response = BaseConfig.class, notes = "baseConfig")
    @GetMapping(value = "")
    public ResponseInfo get() {

        BaseConfig baseConfig = baseConfigService.find();

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("baseConfig", baseConfig);
        return responseInfo;
    }

    @ApiOperation(value = "保存基础配置", response = BaseConfig.class, notes = "baseConfig")
    @PutMapping("")
    public ResponseInfo save(@ApiParam(name = "baseConfig", value = "基础配置") @RequestBody BaseConfig baseConfig) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, baseConfig);

        BaseConfig baseConfigSource = baseConfigService.find();
        if (baseConfigSource == null) {
            baseConfigSource = new BaseConfig();
        }
        BeanUtils.copyPropertiesIgnoreNull(baseConfig, baseConfigSource, "id");
        baseConfig = baseConfigService.saveOrUpdate(baseConfigSource);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("baseConfig", baseConfig);
        return responseInfo;
    }
}
