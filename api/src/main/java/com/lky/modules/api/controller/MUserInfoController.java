package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.AuthRecordDto;
import com.lky.dto.UserInfoDto;
import com.lky.entity.AuthRecord;
import com.lky.entity.User;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.annotation.LoginUser;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.mapper.AuthRecordMapper;
import com.lky.service.*;
import com.lky.utils.PasswordUtils;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Date;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.AuthRecordDict.STATE_APPLY;
import static com.lky.enums.dict.AuthRecordDict.STATE_UNAUTHORIZED;

/**
 * 用户信息控制类
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/16
 */
@RestController
@RequestMapping("api/userInfo")
@Api(value = "api/userInfo", description = "app用户信息")
public class MUserInfoController extends BaseController {

    @Inject
    private UserInfoService userInfoService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private UserService userService;

    @Inject
    private TokenManager tokenManager;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private AuthRecordService authRecordService;

    @Inject
    private AuthRecordMapper authRecordMapper;


    @ApiOperation(value = "修改用户信息", response = ResponseInfo.class)
    @PutMapping(value = "")
    public ResponseInfo edit(@ApiIgnore @LoginUser User user,
                             @ApiParam(name = "userInfoDto", value = "用户信息dto") @RequestBody UserInfoDto userInfoDto) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, user.getId(), userInfoDto);

        if (userInfoService.editUserInfo(user, userInfoDto) != null) {
            return ResponseInfo.buildSuccessResponseInfo();
        }
        return ResponseInfo.buildErrorResponseInfo();
    }


    @ApiOperation(value = "修改用户登录密码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "手机验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "新登录密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "repeatPassword", value = "重复新密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "loginPwd")
    public ResponseInfo editLoginPwd(@ApiIgnore @LoginUser User user,
                                     @RequestParam String code,
                                     @RequestParam String password,
                                     @RequestParam String repeatPassword) {

        AssertUtils.notNull(PARAMS_IS_NULL, password, repeatPassword);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 8 && password.length() <= 32);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, password.equals(repeatPassword));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(user.getMobile(), code, SmsLogDict.TYPE_EDIT_PWD));
        }

        userService.forgetPwd(user.getMobile(), password);

        //生成token
        String token = tokenManager.refreshToken(user.getId(), TokenModel.TYPE_APP);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_APP));
        return responseInfo;

    }

    @ApiOperation(value = "添加/编辑用户支付密码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "手机验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "payPassword", value = "支付密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "repeatPayPassword", value = "重复支付密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "payPwd")
    public ResponseInfo ediPayPwd(@ApiIgnore @LoginUser User user,
                                  @RequestParam String code,
                                  @RequestParam String payPassword,
                                  @RequestParam String repeatPayPassword) {

        AssertUtils.notNull(PARAMS_IS_NULL, payPassword, repeatPayPassword);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, payPassword.length() == 6);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, payPassword.equals(repeatPayPassword));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(user.getMobile(), code, SmsLogDict.TYPE_EDIT_PAY_PWD));
        }
        user.setPayPwd(PasswordUtils.createHash(payPassword));
        userService.update(user);

        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "实名认证申请", response = AuthRecordDto.class, notes = "authRecord")
    @PostMapping(value = "auth/apply")
    public ResponseInfo authApply(@ApiIgnore @LoginUser User user,
                                  @ApiParam(name = "authRecordDto", value = "实名认证信息dto")
                                  @RequestBody AuthRecordDto authRecordDto) {
        //参数校验
        String[] authRecordFiled = {"realName", "cardNumber", "authImgList"};
        AssertUtils.notNull(PARAMS_IS_NULL, authRecordFiled, authRecordDto.getRealName(),
                authRecordDto.getCardNumber(), authRecordDto.getAuthImgList());

        authRecordDto.setUserId(user.getId());
        AuthRecord authRecord = authRecordService.authApply(user, authRecordDto);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("authRecord", authRecordMapper.toDto(authRecord));
        return responseInfo;
    }

    @ApiOperation(value = "实名认证申请撤销", response = AuthRecordDto.class)
    @PutMapping(value = "auth/cancel")
    public ResponseInfo authApply(@ApiIgnore @LoginUser User user) {
        //参数校验
        AuthRecord record = authRecordService.findByUser(user);
        AssertUtils.notNull(PARAMS_EXCEPTION, record);
        AssertUtils.isTrue(AUTH_HAS_AUDIT, STATE_APPLY.getKey().equals(record.getState()));

        //撤销实名认证
        authRecordService.delete(record);

        //更新用户认证状态
        user.setAuthState(STATE_UNAUTHORIZED.getKey());
        user.setUpdateTime(new Date());
        userService.update(user);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "获取实名认证申请记录", response = AuthRecordDto.class, notes = "authRecord")
    @GetMapping(value = "auth/record")
    public ResponseInfo getAuthRecord(@ApiIgnore @LoginUser User user) {
        AuthRecord authRecord = authRecordService.findByUser(user);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("authRecord", authRecordMapper.toDto(authRecord));
        return responseInfo;
    }
}
