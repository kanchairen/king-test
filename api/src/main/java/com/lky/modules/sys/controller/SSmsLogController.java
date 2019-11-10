package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.service.SmsLogService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * 发送短信
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@RestController
@RequestMapping("sys/sms")
@Api(value = "sys/sms", description = "发送短信")
public class SSmsLogController extends BaseController {

    @Inject
    private SmsLogService smsLogService;

    @ApiOperation(value = "发送短信验证码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "form", dataType = "string")
    })
    @PostMapping("code")
    public ResponseInfo sendMobileCode(@RequestParam("mobile") String mobile,
                                       @ApiParam(name = "type", value = "短信类型", allowableValues = "sys_forgetPwd, sys_loginPwd")
                                       @RequestParam("type") String type) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, mobile, type);

        smsLogService.sendMobileCode(mobile, type);

        return ResponseUtils.buildResponseInfo();
    }
}
