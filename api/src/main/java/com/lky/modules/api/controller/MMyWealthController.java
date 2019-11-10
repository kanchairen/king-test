package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ExceptionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.DetailRecordDto;
import com.lky.dto.SurplusGrainDto;
import com.lky.dto.UserAssetDto;
import com.lky.dto.WithdrawRecordDto;
import com.lky.entity.*;
import com.lky.enums.code.ShopResCode;
import com.lky.enums.dict.AuthRecordDict;
import com.lky.enums.dict.RPointRecordDict;
import com.lky.enums.dict.WPointRecordDict;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.annotation.LoginUser;
import com.lky.service.*;
import com.lky.utils.PasswordUtils;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.AssetResCode.*;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.WithdrawRecordDict.*;

/**
 * 个人中心—我的财富
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/2
 */
@RestController
@RequestMapping("api/wealth")
@Api(value = "api/wealth", description = "个人中心-我的财富")
public class MMyWealthController extends BaseController {

    @Inject
    private ShopService shopService;

    @Inject
    private MyWealthService myWealthService;

    @Inject
    private BankCardService bankCardService;

    @Inject
    private WithdrawRecordService withdrawRecordService;

    @Inject
    private EToneService eToneService;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private UserService userService;

    @ApiOperation(value = "我的财富首页", response = UserAssetDto.class, notes = "userAsset")
    @GetMapping(value = "get")
    public ResponseInfo assetInfo(@ApiIgnore @LoginUser User user) throws ParseException {

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("userAsset", myWealthService.getAssetInfo(user));
        return responseInfo;
    }

    @ApiOperation(value = "大米明细", response = DetailRecordDto.class, notes = "balanceRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "0全部，1收入，2支出", required = true, allowableValues = "0, 1, 2",
                    paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "get/balance")
    public ResponseInfo getBalance(@ApiIgnore @LoginUser User user,
                                   @RequestParam Integer level,
                                   @RequestParam(defaultValue = "0") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, level <= 2 && level >= 0);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("balanceRecordList", myWealthService.findBalanceRecordList(user, level, pageable, null, null, null));
        return responseInfo;
    }

    @ApiOperation(value = "用户G米明细", response = DetailRecordDto.class, notes = "wpointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "0全部，1收入，2支出", required = true, allowableValues = "0, 1, 2",
                    paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "get/wPoint")
    public ResponseInfo getWPoint(@ApiIgnore @LoginUser User user,
                                  @RequestParam Integer level,
                                  @RequestParam(defaultValue = "0") Integer pageNumber,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, level <= 2 && level >= 0);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("wpointRecordList", myWealthService.findWPointRecordList(user, level, pageable,
                WPointRecordDict.USER_TYPE_CONSUMER.getKey(), null));
        return responseInfo;
    }


    @ApiOperation(value = "商家G米明细", response = DetailRecordDto.class, notes = "merchantWPointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "0全部，1收入，2支出", required = true, allowableValues = "0, 1, 2",
                    paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "get/merchant/wPoint")
    public ResponseInfo getMerchantWPoint(@ApiIgnore @LoginUser User user,
                                          @RequestParam Integer level,
                                          @RequestParam(defaultValue = "0") Integer pageNumber,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, level <= 2 && level >= 0);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("merchantWPointRecordList", myWealthService.findWPointRecordList(user, level, pageable,
                WPointRecordDict.USER_TYPE_MERCHANT.getKey(), null));
        return responseInfo;
    }

    @ApiOperation(value = "用户小米明细", response = DetailRecordDto.class, notes = "rpointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "0全部，1收入，2支出", required = true, allowableValues = "0, 1, 2",
                    paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "get/rPoint")
    public ResponseInfo getRPoint(@ApiIgnore @LoginUser User user,
                                  @RequestParam Integer level,
                                  @RequestParam(defaultValue = "0") Integer pageNumber,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, level <= 2 && level >= 0);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("rpointRecordList", myWealthService.findRPointRecordList(user, level, pageable,
                RPointRecordDict.USER_TYPE_CONSUMER.getKey(), null, null, null));
        return responseInfo;
    }


    @ApiOperation(value = "商家小米明细", response = DetailRecordDto.class, notes = "merchantRPointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "0全部，1收入，2支出", required = true, allowableValues = "0, 1, 2",
                    paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "get/merchant/rPoint")
    public ResponseInfo getMerchantRPoint(@ApiIgnore @LoginUser User user,
                                          @RequestParam Integer level,
                                          @RequestParam(defaultValue = "0") Integer pageNumber,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, level <= 2 && level >= 0);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("merchantRPointRecordList", myWealthService.findRPointRecordList(user, level, pageable,
                RPointRecordDict.USER_TYPE_MERCHANT.getKey(), null, null, null));
        return responseInfo;
    }

    @ApiOperation(value = "小米转换成大米", response = ResponseInfo.class)
    @ApiImplicitParams({
    })
    @PutMapping(value = "rPoint/convert/balance")
    public ResponseInfo rPointConvertBalance(@ApiIgnore @LoginUser User user) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(ShopResCode.SHOP_NOT_EXIST, shop);

        myWealthService.rPointConvertBalance(shop, user);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "用户申请大米提现", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "绑定的银行卡id",
                    required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "amount", value = "提现金额",
                    required = true, paramType = "query", dataType = "double"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码",
                    required = true, paramType = "query", dataType = "String"),
    })
    @PutMapping(value = "apply/withdraw")
    public ResponseInfo applyWithdrawBalance(@ApiIgnore @LoginUser User user,
                                             @RequestParam Integer id,
                                             @RequestParam Double amount,
                                             @RequestParam String payPwd) {

        ExceptionUtils.throwResponseException(CLOSE_WITHDRAW);

        //校验用户实名认证
        AssertUtils.isTrue(UNAUTHORIZED_CAN_NOT_WITHDRAW,
                AuthRecordDict.STATE_AGREE.getKey().equals(user.getAuthState()));

        //校验当天用户提现次数
        AssertUtils.isTrue(WITHDRAW_NUMBER_OUT, !(withdrawRecordService.count(user) > 0));

        //校验用户及银行卡
        BankCard bankCard = bankCardService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, bankCard);
        AssertUtils.isTrue(PARAMS_EXCEPTION, bankCard.getUser().getId() == user.getId());

        //校验提现金额
        AssertUtils.isTrue(BALANCE_NOT_ENOUGH, user.getUserAsset().getBalance() >= amount);
        AssertUtils.isTrue(WITHDRAW_MUST_HUNDRED_TIMES, (amount > 0) && (amount % 100d == 0));

        //校验支付密码
        AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));

        //申请大米提现
        withdrawRecordService.applyWithdraw(user, bankCard, amount);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "易通代付回调", response = String.class)
    @PostMapping(value = "notify/withdraw")
    @AuthIgnore
    public String etoneNotify(HttpServletRequest request) {
        if (eToneService.etoneNotify(request)) {
            return "success";
        }
        return "failure";
    }

    @ApiOperation(value = "用户大米提现明细", response = WithdrawRecordDto.class, notes = "withdrawRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "提现申请(apply),提现失败(failure),提现完成(finish)",
                    allowableValues = "apply,failure,finish", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "get/withdraw")
    public ResponseInfo getWithdrawBalance(@ApiIgnore @LoginUser User user,
                                           @RequestParam(required = false) String state,
                                           @RequestParam(defaultValue = "0") Integer pageNumber,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        //校验用户实名认证
        AssertUtils.isTrue(UNAUTHORIZED_CAN_NOT_WITHDRAW,
                AuthRecordDict.STATE_AGREE.getKey().equals(user.getAuthState()));

        if (state != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, state, STATE_APPLY, STATE_FAILURE, STATE_FINISH);
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "applyTime"));
        Page<WithdrawRecordDto> page = withdrawRecordService.findByState(user, state, null, null, Boolean.TRUE, pageable);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("withdrawRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "余粮公社", response = SurplusGrainDto.class, notes = "surplusGrain")
    @GetMapping(value = "surplusGrain")
    public ResponseInfo surplusGrain(@ApiIgnore @LoginUser User user) throws ParseException {

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("surplusGrain", myWealthService.getSurplusGrainByUser(user));
        return responseInfo;
    }

    @ApiOperation(value = "余粮公社明细", response = DetailRecordDto.class, notes = "surplusGrainRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "0:G米收益，1:余粮公社收入，2:余粮公社支出",
                    required = true, allowableValues = "0, 1, 2", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "surplusGrain/detail")
    public ResponseInfo surplusGrainDetail(@ApiIgnore @LoginUser User user,
                                           @RequestParam Integer level,
                                           @RequestParam(defaultValue = "0") Integer pageNumber,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, level <= 2 && level >= 0);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("surplusGrainRecordList", myWealthService.findSurplusGrainRecordList(user, level, pageable));
        return responseInfo;
    }

    @ApiOperation(value = "余粮公社转入", response = ResponseInfo.class)
    @PutMapping(value = "surplusGrain/in")
    public ResponseInfo surplusGrainFromBalance(@ApiIgnore @LoginUser User user,
                                                @ApiParam(name = "amount", value = "转入金额")
                                                @RequestParam double amount) {
        //效验参数
        UserAsset userAsset = user.getUserAsset();
        HighConfig highConfig = baseConfigService.findH();
        AssertUtils.isTrue(PARAMS_EXCEPTION, amount > 0);
        AssertUtils.isTrue(SURPLUS_GRAIN_NOT_OPEN, user.getOpenSurplusGrain());
        AssertUtils.isTrue(BALANCE_NOT_ENOUGH, userAsset.getBalance() >= amount);
        AssertUtils.isTrue(MAX_SURPLUS_GRAIN_ILLEGAL, userAsset.getSurplusGrain() + amount <= highConfig.getMaxSurplusGrain());
        myWealthService.addSurplusGrain(user, userAsset, amount, highConfig.getSurplusGrainStartDay());
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "余粮公社转出", response = ResponseInfo.class)
    @PutMapping(value = "surplusGrain/out")
    public ResponseInfo surplusGrainToBalance(@ApiIgnore @LoginUser User user,
                                              @ApiParam(name = "amount", value = "转出金额")
                                              @RequestParam double amount) {
        //效验参数
        UserAsset userAsset = user.getUserAsset();
        AssertUtils.isTrue(PARAMS_EXCEPTION, amount > 0);
        AssertUtils.isTrue(SURPLUS_GRAIN_NOT_OPEN, user.getOpenSurplusGrain());
        AssertUtils.isTrue(SURPLUS_GRAIN_NOT_ENOUGH, userAsset.getSurplusGrain() >= amount);
        myWealthService.reduceSurplusGrain(user, userAsset, amount);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "开通余粮公社", response = ResponseInfo.class)
    @PostMapping(value = "surplusGrain/open")
    public ResponseInfo surplusGrainOpen(@ApiIgnore @LoginUser User user) {
        user.setOpenSurplusGrain(Boolean.TRUE);
        userService.update(user);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "大米自动转入余粮公社", response = ResponseInfo.class)
    @PutMapping(value = "surplusGrain/automatic")
    public ResponseInfo surplusGrainAutoAdd(@ApiIgnore @LoginUser User user,
                                            @ApiParam(name = "automatic", value = "true自动/false手动")
                                            @RequestParam Boolean automatic) {
        AssertUtils.notNull(PARAMS_EXCEPTION, automatic);
        if (automatic) {
            user.setAutomaticSurplusGrain(Boolean.TRUE);
        } else {
            user.setAutomaticSurplusGrain(Boolean.FALSE);
        }
        userService.update(user);
        return ResponseUtils.buildResponseInfo();
    }

}
