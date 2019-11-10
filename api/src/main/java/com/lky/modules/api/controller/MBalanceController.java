package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ExceptionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.RechargeRecord;
import com.lky.entity.User;
import com.lky.entity.UserAsset;
import com.lky.enums.dict.RechargeRecordDict;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.annotation.LoginUser;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.service.BalanceRecordService;
import com.lky.service.RechargeRecordService;
import com.lky.service.SmsLogService;
import com.lky.service.UserService;
import com.lky.utils.PasswordUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.commons.code.PublicResCode.USER_NO_LOGIN;
import static com.lky.enums.code.AssetResCode.*;
import static com.lky.enums.code.UserResCode.*;

/**
 * 大米操作
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/7
 */
@RestController
@RequestMapping("api/balance")
@Api(value = "api/balance", description = "大米操作")
public class MBalanceController extends BaseController {

    @Inject
    private RechargeRecordService rechargeRecordService;

    @Inject
    private UserService userService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private TokenManager tokenManager;

    @ApiOperation(value = "大米充值", notes = "rechargeRecordId", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "充值金额", paramType = "query", dataType = "double"),
    })
    @PostMapping("recharge")
    public ResponseInfo recharge(@ApiIgnore @LoginUser User user,
                                 @RequestParam double amount) {
        AssertUtils.isTrue(AMOUNT_WRONG, amount >= 0.01 && amount <= 10000);
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setAmount(amount);
        rechargeRecord.setState(String.valueOf(RechargeRecordDict.STATE_UNPAID));
        rechargeRecord.setUser(user);
        rechargeRecordService.save(rechargeRecord);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("rechargeRecordId", rechargeRecord.getId());
        return responseInfo;
    }

    @ApiOperation(value = "大米转账", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "转账手机号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "手机验证码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "amount", value = "转账金额", required = true, paramType = "query", dataType = "double"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping("transfer")
    public ResponseInfo transfer(HttpServletRequest request,
                                 @RequestParam String mobile,
                                 @RequestParam double amount,
                                 @RequestParam String code,
                                 @RequestParam String payPwd) throws UnsupportedEncodingException {
        //效验参数
        String[] checkFiled = {"user", "mobile", "amount", "code", "payPwd"};
        synchronized (UserAsset.class) {
            String authToken = super.getAppUserToken(request);
            if (authToken == null) {
                ExceptionUtils.throwResponseException(USER_NO_LOGIN);
            }
            Integer userId = tokenManager.getUserIdByToken(TokenModel.TYPE_APP, authToken);
            User user = userService.findById(userId);
            AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, user, mobile, amount, code, payPwd);
            AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
            AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));
            AssertUtils.isTrue(AMOUNT_WRONG, amount >= 0.01 && amount <= 10000);
            AssertUtils.isMobile(MOBILE_FORMAT_ERROR, mobile);
            AssertUtils.isTrue(TRANSFER_CAN_NOT_SELF, !user.getMobile().equals(mobile));
            User targetUser = userService.findByMobile(mobile);
            AssertUtils.notNull(TRANSFER_MOBILE_NOT_EXIST, targetUser);
            AssertUtils.isTrue(BALANCE_NOT_ENOUGH, user.getUserAsset().getBalance() >= amount);
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(user.getMobile(), code, SmsLogDict.TYPE_BALANCE_TRANSFER));
            balanceRecordService.transfer(user, targetUser, amount);
        }
        return ResponseUtils.buildResponseInfo();
    }
}
