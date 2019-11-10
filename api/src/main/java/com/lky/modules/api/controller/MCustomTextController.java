package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.CustomText;
import com.lky.service.CustomTextService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * 自定义文本
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/8
 */
@RestController
@RequestMapping("api/custom/text")
@Api(value = "api/custom/text", description = "自定义文本")
public class MCustomTextController extends BaseController {

    @Inject
    private CustomTextService customTextService;

    @ApiOperation(value = "自定义页面详情", response = CustomText.class, notes = "customText")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        CustomText customText = customTextService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("customText", customText);
        return responseInfo;
    }
}
