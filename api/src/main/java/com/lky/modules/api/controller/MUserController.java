package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.RequestUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.UserRecommendDto;
import com.lky.entity.User;
import com.lky.entity.UserRole;
import com.lky.enums.dict.AreaDict;
import com.lky.enums.dict.RoleDict;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.annotation.LoginUser;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.mapper.UserMapper;
import com.lky.service.AreaService;
import com.lky.service.EnvironmentService;
import com.lky.service.SmsLogService;
import com.lky.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.UserDict.*;

/**
 * app用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/10
 */
@RestController
@RequestMapping("api/user")
@Api(value = "api/user", description = "app用户")
public class MUserController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(MUserController.class);

    @Inject
    private UserService userService;

    @Inject
    private TokenManager tokenManager;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private AreaService areaService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private UserMapper userMapper;

    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "登录手机号", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "手机验证码", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "登录密码", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "repeatPassword", value = "重复密码", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "province", value = "用户所属省份", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "city", value = "用户所属城市", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "area", value = "用户所属地区", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "recommendCode", value = "推荐码", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "registerSource", value = "注册来源", allowableValues = "android,ios,pc,h5", paramType = "form", dataType = "string"),
    })
    @PostMapping("register")
    @AuthIgnore
    public ResponseInfo register(HttpSession session,
                                 HttpServletRequest request,
                                 @RequestParam String mobile,
                                 @RequestParam String code,
                                 @RequestParam String password,
                                 @RequestParam String repeatPassword,
                                 @RequestParam(required = false) String province,
                                 @RequestParam(required = false) String city,
                                 @RequestParam String area,
                                 @RequestParam(required = false) String recommendCode,
                                 @RequestParam(required = false) String captcha,
                                 @RequestParam(required = false) String registerSource) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"mobile", "code", "password", "repeatPassword", "area"},
                mobile, code, password, repeatPassword, area);
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, mobile);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 8 && password.length() <= 32);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, password.equals(repeatPassword));

        AssertUtils.isContain(PARAMS_EXCEPTION, registerSource,
                REGISTER_SOURCE_H5, REGISTER_SOURCE_ANDROID,
                REGISTER_SOURCE_IOS, REGISTER_SOURCE_PC);

        String remoteIp = "", userAgent = "";
        if (REGISTER_SOURCE_H5.compare(registerSource)) {
            remoteIp = RequestUtils.getRemoteIp(request);
            userAgent = RequestUtils.getUserAgent(request);
        }
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(mobile, code, SmsLogDict.TYPE_REGISTER_H5));
        }

        // 兼容旧版本，旧版本只传一个area
        if (StringUtils.isNotEmpty(province) || StringUtils.isNotEmpty(city)) {
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"province", "city"}, province, city);
            int provinceCount = areaService.countByNameAndType(province, AreaDict.TYPE_PROVINCE.getKey());
            int cityCount = areaService.countByNameAndType(city, AreaDict.TYPE_CITY.getKey());
            int areaCount = areaService.countByNameAndType(area, AreaDict.TYPE_DISTRICT.getKey());
            AssertUtils.isTrue(PARAMS_EXCEPTION, provinceCount > 0 && cityCount > 0 && areaCount > 0);
            area = province + city + area;
        }

        userService.register(mobile, password, area, recommendCode, registerSource, remoteIp, userAgent);

        return this.login(mobile, password);
    }

    @ApiOperation(value = "app用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "用户名", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "form", dataType = "string"),
    })
    @PostMapping("login")
    @AuthIgnore
    public ResponseInfo login(@RequestParam String mobile,
                              @RequestParam String password) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"mobile", "password"}, mobile, password);
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, mobile);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 8 && password.length() <= 32);

        //用户登录
        Integer userId = userService.login(mobile, password);

        String token = tokenManager.createToken(userId, TokenModel.TYPE_APP);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_APP));
        return responseInfo;
    }

    @ApiOperation(value = "app用户登出")
    @GetMapping("logout")
    public ResponseInfo logout(@ApiIgnore @LoginUser User user) {
        tokenManager.delToken(user.getId(), TokenModel.TYPE_APP);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "app用户信息", response = UserRole.class,
            notes = "isMerchant是否是商家, userRole推广员或高级推广员, parentName上线名称/电话")
    @GetMapping("info")
    public ResponseInfo userInfo(@ApiIgnore @LoginUser User user) {

        Boolean isMerchant = userService.isIncludeRole(user, RoleDict.CODE_MERCHANT);
        String parentName = null;
        if (user.getParentId() != null) {
            User parentUser = userService.findById(user.getParentId());
            if (parentUser != null) {
                if (StringUtils.isNotEmpty(parentUser.getRealName())) {
                    parentName = parentUser.getRealName();
                } else {
                    String mobile = parentUser.getMobile();
                    parentName = mobile.substring(0, 3) + "*****" + mobile.substring(8, mobile.length());
                }
            }
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(user);
        responseInfo.putData("isMerchant", isMerchant);
        responseInfo.putData("userRole", userService.findAgent(user));
        responseInfo.putData("parentName", parentName);
        return responseInfo;
    }

    @ApiOperation("忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "登录手机号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "手机验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "新登录密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "repeatPassword", value = "重复新密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping("forgetPwd")
    @AuthIgnore
    public ResponseInfo forgetPwd(@RequestParam String mobile,
                                  @RequestParam String code,
                                  @RequestParam String password,
                                  @RequestParam String repeatPassword) {

        AssertUtils.notNull(PARAMS_IS_NULL, mobile, password, repeatPassword);
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, mobile);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 8 && password.length() <= 32);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, password.equals(repeatPassword));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(mobile, code, SmsLogDict.TYPE_FORGET_PWD));
        }

        Integer userId = userService.forgetPwd(mobile, password);

        String token = tokenManager.refreshToken(userId, TokenModel.TYPE_APP);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_APP));
        return responseInfo;
    }


    @ApiOperation(value = "当前用户的下级用户", response = UserRecommendDto.class,
            notes = "parentUser我的推荐人, childList(一级下线), firstNum一级数量", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @GetMapping("child")
    public ResponseInfo getChildList(@ApiIgnore @LoginUser User user,
                                     @RequestParam(defaultValue = "0") int pageNumber,
                                     @RequestParam(defaultValue = "10") int pageSize
    ) {
        User parentUser = null;
        if (user.getParentId() != null) {
            parentUser = userService.findById(user.getParentId());
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<User> childPage = userService.listByParentId(user.getId(), pageable);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("parentUser", userMapper.fromUser(parentUser));
        responseInfo.putData("childList", userMapper.fromUserList(childPage.getContent()));
        responseInfo.putData("firstNum", childPage.getTotalElements());
        responseInfo.putData("totalPages", childPage.getTotalPages());
        return responseInfo;
    }
}
