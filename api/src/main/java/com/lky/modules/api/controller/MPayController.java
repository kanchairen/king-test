package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.*;
import com.lky.dto.OrdersCashierDto;
import com.lky.entity.User;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.annotation.LoginUser;
import com.lky.service.PaymentRecordService;
import com.lky.utils.PasswordUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;

/**
 * 支付相关
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/17
 */
@RestController
@RequestMapping("api/pay")
@Api(value = "api/pay", description = "支付相关")
public class MPayController extends BaseController {

    @Inject
    private PaymentRecordService ordersPaymentService;

    @ApiOperation(value = "收银详情", response = OrdersCashierDto.class, notes = "ordersCashierDto")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id或者申请记录id", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "类型", required = true,
                    allowableValues = "offline-orders,orders,apply,annual,recharge,qr-code", paramType = "form", dataType = "string"),
    })
    @GetMapping("cashier/detail")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo cashierDetail(@ApiIgnore @LoginUser User user,
                                      @RequestParam String id,
                                      @RequestParam String type) {

        String[] checkFiled = {"id", "type"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id, type);

        OrdersCashierDto ordersCashierDto = ordersPaymentService.cashierDetail(user, id, type);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersCashierDto", ordersCashierDto);
        return responseInfo;
    }

    @ApiOperation(value = "米支付", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", required = true, paramType = "form", dataType = "string"),
    })
    @PostMapping("point")
    public ResponseInfo pointPay(@ApiIgnore @LoginUser User user,
                                 @RequestParam String id,
                                 @RequestParam String payPwd) {
        String[] checkFiled = {"id", "payPwd"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id, payPwd);
        AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));

        ordersPaymentService.pointPay(user, id, payPwd);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "大米支付", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id或者申请记录id", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "类型", required = true,
                    allowableValues = "offline-orders,orders,apply,annual,qr-code", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", paramType = "form", dataType = "string"),
    })
    @PostMapping("balance")
    public ResponseInfo balance(@ApiIgnore @LoginUser User user,
                                @RequestParam String id,
                                @RequestParam String type,
                                @RequestParam String payPwd) {

        String[] checkFiled = {"id", "type", "payPwd"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id, type, payPwd);
        AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));

        ordersPaymentService.balancePay(user, id, type, payPwd);

        return ResponseUtils.buildResponseInfo();
    }


    @ApiOperation(value = "微信支付", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id或者申请记录id", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "类型", required = true,
                    allowableValues = "offline-orders,orders,apply,annual,recharge,qr-code", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", paramType = "form", dataType = "string"),
    })
    @PostMapping("wxpay")
    public ResponseInfo wxpay(HttpServletRequest request,
                              @ApiIgnore @LoginUser User user,
                              @RequestParam String id,
                              @RequestParam String type,
                              @RequestParam(required = false) String payPwd) {

        String[] checkFiled = {"id", "type"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id, type);
        if (StringUtils.isNotEmpty(payPwd)) {
            AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
            AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
            AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));
        }

        return ordersPaymentService.wxpay(user, id, type, RequestUtils.getRemoteIp(request), payPwd);
    }

    @ApiOperation(value = "支付宝支付", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id或者申请记录id", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "类型", required = true,
                    allowableValues = "offline-orders,orders,apply,annual,recharge,qr-code", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", paramType = "form", dataType = "string"),
    })
    @PostMapping("alipay")
    public ResponseInfo alipay(@ApiIgnore @LoginUser User user,
                               @RequestParam String id,
                               @RequestParam String type,
                               @RequestParam(required = false) String payPwd) {

        String[] checkFiled = {"id", "type"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id, type);
        if (StringUtils.isNotEmpty(payPwd)) {
            AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
            AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
            AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));
        }

        return ordersPaymentService.alipay(user, id, type, payPwd);
    }

    @ApiOperation(value = "银联支付", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id或者申请记录id", required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "类型", required = true,
                    allowableValues = "offline-orders,orders,apply,annual,qr-code", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", paramType = "form", dataType = "string"),
    })
    @GetMapping("unipay")
    public ResponseInfo unipay(@ApiIgnore @LoginUser User user,
                               @RequestParam String id,
                               @RequestParam String type,
                               @RequestParam(required = false) String payPwd) throws Exception {

        String[] checkFiled = {"id", "type"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id, type);
        if (StringUtils.isNotEmpty(payPwd)) {
            AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
            AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
            AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));
        }

        return ordersPaymentService.unipay(user, id, type, payPwd);
    }

    @ApiOperation(value = "微信回调", response = ResponseInfo.class)
    @PostMapping(value = "wxpay/notify")
    @AuthIgnore
    public String wxpayNotify(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("return_code", "FAIL");
        if (ordersPaymentService.wxpayNotify(request)) {
            map.put("return_code", "SUCCESS");
        }
        return XMLUtils.toXML(map, Boolean.TRUE);
    }

    @ApiOperation(value = "支付宝回调", response = ResponseInfo.class)
    @PostMapping(value = "alipay/notify")
    @AuthIgnore
    public String alipayNotify(HttpServletRequest request,
                               HttpServletResponse response) throws IOException {

        if (ordersPaymentService.alipayNotify(request)) {
            return "success";
        }
        return "failure";
    }

    @ApiOperation(value = "银联回调", response = ResponseInfo.class)
    @PostMapping(value = "unipay/notify")
    @AuthIgnore
    public String unipayNotify(HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        if (ordersPaymentService.unipayNotify(request)) {
            //返回给银联服务器http 200状态码
            response.getWriter().print("ok");
            return "success";
        }
        return "failure";
    }
}
