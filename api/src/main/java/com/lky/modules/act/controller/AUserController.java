package com.lky.modules.act.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.entity.AUser;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.constant.Constant;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.service.*;
import com.lky.utils.PasswordUtils;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.SUserDict.STATE_ACTIVE;

/**
 * 代理商
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/21
 */
@RestController
@RequestMapping("/act/user")
@Api(value = "/act/user", description = "代理商")
public class AUserController extends BaseController {

    @Inject
    private AUserService aUserService;

    @Inject
    private TokenManager tokenManager;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private BaseConfigService baseConfigService;

    @ApiOperation(value = "代理商用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "用户名", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, paramType = "form", dataType = "string"),
    })
    @PostMapping("login")
    public ResponseInfo login(@RequestParam String mobile,
                              @RequestParam String password,
                              @RequestParam String captcha) {

        AssertUtils.notNull(PARAMS_IS_NULL, mobile, password, captcha);
        String kaptcha = ShiroUtils.getKaptcha(Constant.AKAPTCHA_SESSION_KEY);
        AssertUtils.isTrue(KAPTCHA_ERROR, captcha.equalsIgnoreCase(kaptcha));

        AUser aUser = aUserService.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, aUser);
        AssertUtils.isTrue(PASSWORD_ERROR, PasswordUtils.validatePassword(password, aUser.getPassword()));
        AssertUtils.isTrue(SYS_USER_LOCK, STATE_ACTIVE.compare(aUser.getState()));

        String token = tokenManager.createToken(aUser.getId(), TokenModel.TYPE_ACT);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_ACT));
        return responseInfo;
    }

    @ApiOperation(value = "忘记密码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "手机号验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "新密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping("forget/password")
    public ResponseInfo forgetPwd(@RequestParam String mobile,
                                  @RequestParam String code,
                                  @RequestParam String password,
                                  @RequestParam String captcha) {

        String[] checkFiled = {"mobile", "code", "password", "captcha"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, mobile, code, password, captcha);
        String kaptcha = ShiroUtils.getKaptcha(Constant.AKAPTCHA_SESSION_KEY);
        AssertUtils.isTrue(KAPTCHA_ERROR, captcha.equalsIgnoreCase(kaptcha));

        AUser aUser = aUserService.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, aUser);
        AssertUtils.isTrue(SYS_USER_LOCK, STATE_ACTIVE.compare(aUser.getState()));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(mobile, code, SmsLogDict.TYPE_ACT_FORGET_PWD));
        }

        aUser.setPassword(PasswordUtils.createHash(password));
        aUserService.save(aUser);

        String token = tokenManager.refreshToken(aUser.getId(), TokenModel.TYPE_ACT);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_ACT));
        return responseInfo;
    }

    @ApiOperation(value = "修改登录密码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "手机验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "新登录密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "repeatPassword", value = "重复新密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "loginPwd")
    public ResponseInfo editLoginPwd(@RequestParam String mobile,
                                     @RequestParam String code,
                                     @RequestParam String password,
                                     @RequestParam String repeatPassword) {

        AUser aUser = ShiroUtils.getAUser();
        String[] checkFields = {"mobile", "code", "password", "repeatPassword"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFields, mobile, code, password, repeatPassword);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 6 && password.length() <= 32);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, password.equals(repeatPassword));
        AssertUtils.isTrue(MOBILE_ERROR, mobile.equals(aUser.getMobile()));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(aUser.getMobile(), code, SmsLogDict.TYPE_ACT_LOGIN_PWD));
        }
        aUserService.forgetPwd(aUser.getMobile(), password);

        String token = tokenManager.refreshToken(aUser.getId(), TokenModel.TYPE_ACT);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_ACT));
        return responseInfo;
    }

    @ApiOperation(value = "代理商用户信息", response = AUser.class, notes = "user")
    @GetMapping(value = "info")
    public ResponseInfo get() {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        AUser aUser = ShiroUtils.getAUser();
        responseInfo.putData("user", aUser);
        responseInfo.putData("payPwd", !StringUtils.isEmpty(aUser.getPayPwd()));
        responseInfo.putData("aUserOverView",  aUserService.findOverView(aUser));
        responseInfo.putData("withdrawFee", baseConfigService.findH().getBalanceWithdrawFee());
        return responseInfo;
    }

    @ApiOperation(value = "代理商用户登出")
    @GetMapping("logout")
    public ResponseInfo logout() {
        tokenManager.delToken(ShiroUtils.getAUserId(), TokenModel.TYPE_ACT);
        return ResponseInfo.buildSuccessResponseInfo();
    }
}
