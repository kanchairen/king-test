package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ExceptionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.entity.User;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.constant.Constant;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.service.SmsLogService;
import com.lky.service.UserService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.lky.commons.code.PublicResCode.*;
import static com.lky.enums.code.UserResCode.KAPTCHA_ERROR;

/**
 * 发送短信
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@RestController
@RequestMapping("api/sms")
@Api(value = "api/sms", description = "发送短信")
public class MSmsLogController extends BaseController {

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private UserService userService;

    @Inject
    private TokenManager tokenManager;

    @ApiOperation(value = "发送短信验证码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "form", dataType = "string")
    })
    @PostMapping("code")
    @AuthIgnore
    public ResponseInfo sendMobileCode(HttpServletRequest request,
                                       @RequestParam(required = false) String mobile,
                                       @ApiParam(name = "type", value = "短信类型",
                                               allowableValues = "register,login,forgetPwd,bankPayPwd")
                                       @RequestParam String type) throws Exception {

        if (StringUtils.isEmpty(mobile)) {
            //从header中获取token
            String token = request.getHeader("token");
            //token为空
            if (org.apache.commons.lang.StringUtils.isBlank(token)) {
                ExceptionUtils.throwResponseException(USER_NO_LOGIN);
            }
            //根据token信息查找用户id
            Integer userId = tokenManager.getUserIdByToken(TokenModel.TYPE_APP, token);
            //token失效
            if (userId == null) {
                ExceptionUtils.throwResponseException(USER_NO_LOGIN);
            }
            User user = userService.findById(userId);
            AssertUtils.notNull(OPERATE_FAIL, user);
            mobile = user.getMobile();
        }

        AssertUtils.notNull(PARAMS_IS_NULL, mobile, type);

        smsLogService.sendMobileCode(mobile, type);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "发送短信验证码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, paramType = "form", dataType = "string"),
    })
    @PostMapping("code/register")
    @AuthIgnore
    public ResponseInfo registerCode(HttpSession session,
                                     @RequestParam String mobile,
                                     @RequestParam String captcha) throws Exception {

        AssertUtils.notNull(PARAMS_IS_NULL, mobile, captcha);
        AssertUtils.isMobile(PARAMS_EXCEPTION, mobile);
        AssertUtils.isNull(PARAMS_EXCEPTION, userService.findByMobile(mobile));
        AssertUtils.isTrue(KAPTCHA_ERROR, captcha != null &&
                captcha.equalsIgnoreCase((String) session.getAttribute(Constant.MKAPTCHA_SESSION_KEY)));
        smsLogService.sendMobileCode(mobile, String.valueOf(SmsLogDict.TYPE_REGISTER_H5));

        return ResponseUtils.buildResponseInfo();
    }


}
