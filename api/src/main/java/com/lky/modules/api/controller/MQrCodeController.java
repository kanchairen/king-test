package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.QrCode;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.service.QrCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.OrderResCode.PAY_AMOUNT_OUT_LIMIT;
import static com.lky.enums.code.OrderResCode.QR_CODE_INVALID;

/**
 * 活动二维码，设置现金对应G米比例
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/17
 */
@RestController
@RequestMapping(value = "api/qrCode")
@Api(value = "api/qrCode", description = "活动二维码")
public class MQrCodeController extends BaseController {

    @Inject
    private QrCodeService qrCodeService;

    @ApiOperation(value = "根据扫码code获取二维码活动比例", response = ResponseInfo.class, notes = "rate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "二维码code", required = true, paramType = "path", dataType = "string"),
    })
    @GetMapping("{code}")
    public ResponseInfo findById(@PathVariable String code) {

        AssertUtils.notNull(PARAMS_IS_NULL, code);
        QrCode qrCode = qrCodeService.findByCode(code);
        AssertUtils.notNull(QR_CODE_INVALID, qrCode);
        long now = System.currentTimeMillis();
        AssertUtils.isTrue(QR_CODE_INVALID, qrCode.getBeginTime().getTime() <= now
                && qrCode.getEndTime().getTime() >= now && qrCode.getState());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("rate", qrCode.getRate());
        return responseInfo;
    }

    @ApiOperation(value = "去支付", response = Integer.class, notes = "qrCodePayId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "二维码code", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "amount", value = "支付金额", required = true, paramType = "query", dataType = "double"),
    })
    @PostMapping("{code}")
    public ResponseInfo payQrCode(@ApiIgnore @LoginUser User user,
                                  @PathVariable String code,
                                  @RequestParam Double amount) {
        AssertUtils.notNull(PARAMS_IS_NULL, code, amount);
        AssertUtils.isTrue(PARAMS_EXCEPTION, amount > 0);
        AssertUtils.isTrue(PAY_AMOUNT_OUT_LIMIT, amount <= 10000);
        QrCode qrCode = qrCodeService.findByCode(code);
        AssertUtils.notNull(QR_CODE_INVALID, qrCode);
        long now = System.currentTimeMillis();
        AssertUtils.isTrue(QR_CODE_INVALID, qrCode.getBeginTime().getTime() <= now
                && qrCode.getEndTime().getTime() >= now && qrCode.getState());
        Integer qrCodePayId = qrCodeService.pay(user, qrCode, amount);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("qrCodePayId", qrCodePayId);
        return responseInfo;
    }
}
