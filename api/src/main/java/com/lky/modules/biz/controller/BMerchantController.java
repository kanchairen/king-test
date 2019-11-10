package com.lky.modules.biz.controller;

import com.google.code.kaptcha.Constants;
import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ShopDto;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.annotation.LoginUser;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.mapper.ShopMapper;
import com.lky.service.EnvironmentService;
import com.lky.service.ShopService;
import com.lky.service.SmsLogService;
import com.lky.service.UserService;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;

/**
 * 商家
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@RestController
@RequestMapping("biz/merchant")
@Api(value = "biz/merchant", description = "商家")
public class BMerchantController extends BaseController {

    @Inject
    private UserService userService;

    @Inject
    private TokenManager tokenManager;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopMapper shopMapper;

    @Inject
    private EnvironmentService environmentService;

    @ApiOperation(value = "商家登录", response = ResponseInfo.class, notes = "返回token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "用户名", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, paramType = "form", dataType = "string"),
    })
    @PostMapping("login")
    @AuthIgnore
    public ResponseInfo login(HttpSession session,
                              @RequestParam String mobile,
                              @RequestParam String password,
                              @RequestParam String captcha) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"mobile", "password", "captcha"}, mobile, password, captcha);
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, mobile);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 8 && password.length() <= 32);
        AssertUtils.isTrue(KAPTCHA_ERROR, captcha.equalsIgnoreCase((String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY)));

        //验证成功移除
        session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);

        //用户登录
        Integer userId = userService.merchantLogin(mobile, password);

        //生成token
        String token = tokenManager.createToken(userId, TokenModel.TYPE_BIZ);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_BIZ));
        return responseInfo;
    }

    @ApiOperation(value = "app用户登出")
    @GetMapping("logout")
    public ResponseInfo logout(@ApiIgnore @LoginUser User user) {
        tokenManager.delToken(user.getId(), TokenModel.TYPE_BIZ);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "商家忘记密码", response = ResponseInfo.class, notes = "返回token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "手机号验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "新密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "captcha", value = "验证码", required = true, paramType = "query", dataType = "string"),
    })
    @AuthIgnore
    @PutMapping("forget/password")
    public ResponseInfo forgetPwd(@RequestParam String mobile,
                                  @RequestParam String code,
                                  @RequestParam String password,
                                  @RequestParam String captcha) {

        String[] checkFiled = {"mobile", "code", "password", "captcha"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, mobile, code, password, captcha);
        String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        AssertUtils.isTrue(KAPTCHA_ERROR, captcha.equalsIgnoreCase(kaptcha));
        AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(mobile, code, SmsLogDict.TYPE_BIZ_FORGET_PWD));

        Integer userId = userService.forgetPwd(mobile, password);

        //生成token
        String token = tokenManager.refreshToken(userId, TokenModel.TYPE_BIZ);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_BIZ));
        return responseInfo;
    }

    @ApiOperation(value = "商家信息", response = ShopDto.class)
    @GetMapping("info")
    public ResponseInfo userInfo(@ApiIgnore @LoginUser User user) {
        Shop shop = shopService.findByUser(user);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        ShopDto shopDto = shopMapper.toDto(shop);
        shopDto.setUserId(user.getId());
        responseInfo.putBeanDataAll(shopDto);
        return responseInfo;
    }

    @ApiOperation(value = "修改商家信息", response = Shop.class)
    @PutMapping("")
    public ResponseInfo editInfo(@ApiIgnore @LoginUser User user,
                                 @ApiParam(name = "ShopDto", value = "店铺dto")
                                 @RequestBody ShopDto shopDto) {

        String[] checkFiled = {"shopDto", "benefitRate", "shopLogoImg", "shareText",
                "shareImg", "contactPhone", "name", "shopLicenseImgList"};

        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, shopDto, shopDto.getBenefitRate(),
                shopDto.getShopLogoImg(), shopDto.getShareText(), shopDto.getShareImg(), shopDto.getContactPhone(),
                shopDto.getName(), shopDto.getShopLicenseImgList());

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(PARAMS_EXCEPTION, shop);
        shopDto.setId(shop.getId());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(shopService.modify(shop, shopDto, Boolean.FALSE));
        return responseInfo;
    }

    @ApiOperation(value = "修改登录密码", response = ResponseInfo.class, notes = "返回token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "手机验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "新登录密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "repeatPassword", value = "重复新密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "loginPwd")
    public ResponseInfo editLoginPwd(@ApiIgnore @LoginUser User user,
                                     @RequestParam String mobile,
                                     @RequestParam String code,
                                     @RequestParam String password,
                                     @RequestParam String repeatPassword) {

        String[] checkFields = {"mobile", "code", "password", "repeatPassword"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFields, mobile, code, password, repeatPassword);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, password.length() >= 8 && password.length() <= 32);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, password.equals(repeatPassword));
        AssertUtils.isTrue(MOBILE_ERROR, mobile.equals(user.getMobile()));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(user.getMobile(), code, SmsLogDict.TYPE_BIZ_LOGIN_PWD));
        }

        userService.forgetPwd(user.getMobile(), password);
        //生成token
        String token = tokenManager.refreshToken(user.getId(), TokenModel.TYPE_BIZ);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(tokenManager.buildTokenMap(token, TokenModel.TYPE_BIZ));
        return responseInfo;
    }
}
