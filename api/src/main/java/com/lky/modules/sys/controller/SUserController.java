package com.lky.modules.sys.controller;

import com.google.code.kaptcha.Constants;
import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.SMenu;
import com.lky.entity.SUser;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.constant.Constant;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.service.EnvironmentService;
import com.lky.service.SMenuService;
import com.lky.service.SUserService;
import com.lky.service.SmsLogService;
import com.lky.utils.PasswordUtils;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.SUserDict.STATE_ACTIVE;

/**
 * 系统用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/20
 */
@RestController
@RequestMapping("/sys/user")
@Api(value = "/sys/user", description = "系统用户")
@Transactional
public class SUserController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SUserController.class);

    @Inject
    private SUserService sUserService;

    @Inject
    private TokenManager tokenManager;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private SMenuService sMenuService;

    @ApiOperation(value = "创建系统用户")
    @PostMapping("")
    public ResponseInfo create(@RequestBody SUser sUser) {
        if (environmentService.isTest() || environmentService.isDev()) {
            sUserService.save(sUser);
        }
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "系统用户列表", response = SUser.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping("list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        Page<SUser> sUserList = sUserService.findAll(pageable);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(sUserList);
        return responseInfo;
    }

    @ApiOperation(value = "系统用户登录")
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
        String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        AssertUtils.isTrue(KAPTCHA_ERROR, captcha.equalsIgnoreCase(kaptcha));

        SUser sUser = sUserService.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, sUser);
        AssertUtils.isTrue(PASSWORD_ERROR, PasswordUtils.validatePassword(password, sUser.getPassword()));
        AssertUtils.isTrue(SYS_USER_LOCK, STATE_ACTIVE.compare(sUser.getState()));

        String token = tokenManager.createToken(sUser.getId(), TokenModel.TYPE_SYS);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_SYS));
        return responseInfo;
    }

    @ApiOperation(value = "系统用户信息", response = SUser.class, notes = "user")
    @GetMapping(value = "info")
    public ResponseInfo get() {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        SUser sUser = ShiroUtils.getSUser();
        responseInfo.putBeanData("user", sUser, Boolean.TRUE, "password");
        responseInfo.putData("admin", Constant.ADMIN.equals(sUser.getUsername()));
        List<SMenu> sMenuList = sMenuService.findBySUserId(sUser.getId());
        responseInfo.putData("sRoleList", sMenuService.findSMenuTree(sMenuList));
        return responseInfo;
    }

    @ApiOperation(value = "系统用户登出")
    @GetMapping("logout")
    public ResponseInfo logout() {
        tokenManager.delToken(ShiroUtils.getSUserId(), TokenModel.TYPE_SYS);
        return ResponseInfo.buildSuccessResponseInfo();
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
        String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        AssertUtils.isTrue(KAPTCHA_ERROR, captcha.equalsIgnoreCase(kaptcha));

        SUser sUser = sUserService.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, sUser);
        AssertUtils.isTrue(SYS_USER_LOCK, STATE_ACTIVE.compare(sUser.getState()));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(mobile, code, SmsLogDict.TYPE_SYS_FORGET_PWD));
        }

        sUser.setPassword(PasswordUtils.createHash(password));
        sUserService.update(sUser);

        String token = tokenManager.refreshToken(sUser.getId(), TokenModel.TYPE_SYS);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_SYS));
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

        SUser sUser = ShiroUtils.getSUser();
        String[] checkFields = {"mobile", "code", "password", "repeatPassword"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFields, mobile, code, password, repeatPassword);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 6 && password.length() <= 32);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, password.equals(repeatPassword));
        AssertUtils.isTrue(MOBILE_ERROR, mobile.equals(sUser.getMobile()));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(sUser.getMobile(), code, SmsLogDict.TYPE_SYS_LOGIN_PWD));
        }
        sUserService.forgetPwd(sUser.getMobile(), password);

        String token = tokenManager.refreshToken(sUser.getId(), TokenModel.TYPE_SYS);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_SYS));
        return responseInfo;
    }
}
