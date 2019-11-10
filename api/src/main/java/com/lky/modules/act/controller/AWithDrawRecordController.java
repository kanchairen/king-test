package com.lky.modules.act.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.WithdrawRecordDto;
import com.lky.entity.ABankCard;
import com.lky.entity.AUser;
import com.lky.service.ABankCardService;
import com.lky.service.AWithdrawRecordService;
import com.lky.utils.PasswordUtils;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.AssetResCode.*;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.WithdrawRecordDict.*;

/**
 * 代理商大米提现Controller
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-22
 */
@RestController
@RequestMapping("act/withdraw")
@Api(value = "act/withdraw", description = "代理商提现")
public class AWithDrawRecordController extends BaseController {

    @Inject
    private AWithdrawRecordService aWithdrawRecordService;

    @Inject
    private ABankCardService aBankCardService;

    @ApiOperation(value = "代理商申请大米提现", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "绑定的银行卡id",
                    required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "amount", value = "提现金额",
                    required = true, paramType = "query", dataType = "double"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码",
                    required = true, paramType = "query", dataType = "String"),
    })
    @PutMapping(value = "apply")
    public ResponseInfo applyWithdrawBalance(@RequestParam Integer id,
                                             @RequestParam Double amount,
                                             @RequestParam String payPwd) {
        AUser aUser = ShiroUtils.getAUser();

        //校验当天代理商提现次数
        AssertUtils.isTrue(WITHDRAW_NUMBER_OUT, !(aWithdrawRecordService.todayCount(aUser) > 0));

        //校验用户及银行卡
        ABankCard aBankCard = aBankCardService.findById(id);
        AssertUtils.isTrue(PARAMS_EXCEPTION, aBankCard != null
                && aUser.getId().equals(aBankCard.getAUser().getId()));

        //校验提现金额
        AssertUtils.isTrue(BALANCE_NOT_ENOUGH, aUser.getAUserAsset().getBalance() >= amount);
        AssertUtils.isTrue(WITHDRAW_MUST_HUNDRED_TIMES, (amount > 0) && (amount % 100d == 0));

        //校验支付密码
        AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.notNull(PAY_PWD_NOT_SET, aUser.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, aUser.getPayPwd()));

        //校验当天代理商提现次数
        AssertUtils.isTrue(WITHDRAW_NUMBER_OUT, !(aWithdrawRecordService.todayCount(aUser) > 0));
        //申请大米提现
        aWithdrawRecordService.applyWithdraw(aUser, aBankCard, amount);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "代理商大米提现明细", response = WithdrawRecordDto.class,
            notes = "withdrawRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "提现申请(apply),提现中(agree),提现失败(failure),提现完成(finish)",
                    allowableValues = "apply,agree,failure,finish", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "list")
    public ResponseInfo getWithdrawBalance(@RequestParam(required = false) String state,
                                           @RequestParam(defaultValue = "0") Integer pageNumber,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {

        if (state != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, state, STATE_APPLY, STATE_AGREE, STATE_FAILURE, STATE_FINISH);
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "applyTime"));
        Page<WithdrawRecordDto> page = aWithdrawRecordService.findByState(ShiroUtils.getAUser(), state, null, null, pageable);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("withdrawRecordList", page);
        return responseInfo;
    }
}
