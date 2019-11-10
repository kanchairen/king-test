package com.lky.modules.act.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.AgentDto;
import com.lky.entity.*;
import com.lky.enums.dict.ABalanceRecordDict;
import com.lky.enums.dict.ARPointRecordDict;
import com.lky.enums.dict.AWPointRecordDict;
import com.lky.enums.dict.SmsLogDict;
import com.lky.mapper.AgentMapper;
import com.lky.service.*;
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

import static com.lky.commons.code.PublicResCode.*;
import static com.lky.enums.code.AssetResCode.RED_POINT_NOT_ENOUGH;
import static com.lky.enums.code.AssetResCode.TRANSFER_MOBILE_NOT_EXIST;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.AIncomeRecordDict.TYPE_DAY;
import static com.lky.enums.dict.AIncomeRecordDict.TYPE_MONTH;

/**
 * 代理商用户信息Controller
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-22
 */
@RestController
@RequestMapping("act/auser/info")
@Api(value = "act/auser/info", description = "代理商用户信息")
public class AUserInfoController extends BaseController {

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private AUserService aUserService;

    @Inject
    private AIncomeRecordService aIncomeRecordService;

    @Inject
    private ABalanceRecordService aBalanceRecordService;

    @Inject
    private AWPointRecordService awPointRecordService;

    @Inject
    private ARPointRecordService arPointRecordService;

    @Inject
    private UserService userService;

    @Inject
    private AgentMapper agentMapper;

    @Inject
    private AUserMemberService aUserMemberService;

    @ApiOperation(value = "添加/编辑代理商支付密码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "手机验证码",
                    required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码",
                    required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "repeatPayPwd", value = "重复支付密码",
                    required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "payPwd")
    public ResponseInfo editPayPwd(@RequestParam String code,
                                   @RequestParam String payPwd,
                                   @RequestParam String repeatPayPwd) {
        AUser aUser = ShiroUtils.getAUser();
        AssertUtils.notNull(PARAMS_IS_NULL, payPwd, repeatPayPwd);
        AssertUtils.isTrue(PASSWORD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.isTrue(PASSWORD_NOT_SAME, payPwd.equals(repeatPayPwd));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(aUser.getMobile(),
                    code, SmsLogDict.TYPE_ACT_EDIT_PAY_PWD));
        }
        aUser.setPayPwd(PasswordUtils.createHash(payPwd));
        aUserService.update(aUser);

        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "代理商收益", response = AIncomeRecord.class,
            notes = "incomeRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查看收益记录的类型:day(按天查看代理商收益明细), month(按月查看代理商收益明细)",
                    allowableValues = "day, month", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "income")
    public ResponseInfo seeAgentIncome(@RequestParam String type,
                                        @RequestParam(defaultValue = "0") int pageNumber,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        AUser aUser = ShiroUtils.getAUser();
        AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_DAY, TYPE_MONTH);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<AIncomeRecord> page = aIncomeRecordService.findByAUser(type, aUser.getId(), pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sumIncomeAmount", aUser.getAUserAsset().getSumIncomeAmount());
        responseInfo.putData("yesterdayIncomeAmount", aIncomeRecordService.findYesterdayIncome(aUser.getId()));
        responseInfo.putData("incomeRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "查看代理商大米", response = ABalanceRecord.class,
            notes = "balanceRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "大米记录类型:wpoint_convert_balance(G米转化获得的大米), " +
                    "income(收益获得的大米), withdraw_back(提现失败退回获得的大米), withdraw(提现消耗的大米)",
                    allowableValues = "wpoint_convert_balance, income, withdraw_back, withdraw",
                    paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "balance")
    public ResponseInfo seeAgentBalance(@RequestParam(required = false) String type,
                                        @RequestParam(defaultValue = "0") int pageNumber,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        AUser aUser = ShiroUtils.getAUser();
        if (type != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, type,
                    ABalanceRecordDict.TYPE_WPOINT_CONVERT_BALANCE,
                    ABalanceRecordDict.TYPE_INCOME,
                    ABalanceRecordDict.TYPE_WITHDRAW_BACK,
                    ABalanceRecordDict.TYPE_WITHDRAW);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<ABalanceRecord> page = aBalanceRecordService.findByType(aUser.getId(), type, pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("balance", aUser.getAUserAsset().getBalance());
        responseInfo.putData("balanceRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "查看代理商G米", response = AWPointRecord.class,
            notes = "wpointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "G米记录类型:income(收益获得的G米), " +
                    "convert_balance(转化成大米消耗的G米), convert_rpoint(转化成小米消耗的G米)",
                    allowableValues = "income, convert_balance, convert_rpoint",
                    paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "wpoint")
    public ResponseInfo seeAgentWPoint(@RequestParam(required = false) String type,
                                       @RequestParam(defaultValue = "0") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        AUser aUser = ShiroUtils.getAUser();
        if (type != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, type,
                    AWPointRecordDict.TYPE_CONVERT_BALANCE,
                    AWPointRecordDict.TYPE_CONVERT_RPOINT,
                    AWPointRecordDict.TYPE_INCOME);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        Page<AWPointRecord> page = awPointRecordService.findByType(aUser.getId(), type, pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("wpoint", aUser.getAUserAsset().getWpoint());
        //可激励G米
        responseInfo.putData("transWPoint", awPointRecordService.findAUserTransWPoint(aUser.getId()));
        responseInfo.putData("sumConvert", aUser.getAUserAsset().getConvertAmount());
        responseInfo.putData("wpointRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "查看代理商小米", response = ARPointRecord.class,
            notes = "rpointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "小米记录类型:wpoint_convert(G米转化获得的小米), roll_out(转出消耗的小米)",
                    allowableValues = "wpoint_convert, roll_out", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "rpoint")
    public ResponseInfo seeAgentRPoint(@RequestParam(required = false) String type,
                                       @RequestParam(defaultValue = "0") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        AUser aUser = ShiroUtils.getAUser();
        if (type != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, type,
                    ARPointRecordDict.TYPE_WPOINT_CONVERT,
                    ARPointRecordDict.TYPE_ROLL_OUT);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        Page<ARPointRecord> page = arPointRecordService.findByType(aUser.getId(), type, pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("rpoint", aUser.getAUserAsset().getRpoint());
        responseInfo.putData("rpointRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "代理商小米转出到用户小米", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "收方手机号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "amount", value = "小米数量", required = true, paramType = "query", dataType = "double"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping("rpoint/transfer")
    public ResponseInfo transfer(@RequestParam String mobile,
                                 @RequestParam double amount,
                                 @RequestParam String payPwd) {
        AUser aUser = ShiroUtils.getAUser();
        //效验参数
        String[] checkFiled = {"mobile", "amount","payPwd"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, mobile, amount, payPwd);
        AssertUtils.isTrue(PARAMS_EXCEPTION, amount > 0);
        AssertUtils.isTrue(RED_POINT_NOT_ENOUGH, aUser.getAUserAsset().getRpoint() >= amount);
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, mobile);
        User targetUser = userService.findByMobile(mobile);
        AssertUtils.notNull(TRANSFER_MOBILE_NOT_EXIST, targetUser);
        AssertUtils.notNull(SERVER_EXCEPTION, aUser.getAUserAsset());
        AssertUtils.notNull(PAY_PWD_NOT_SET, aUser.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, aUser.getPayPwd()));

        arPointRecordService.transfer(aUser, targetUser, amount);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "获取代理概况，代理信息", response = AgentDto.class, notes = "agent")
    @GetMapping(value = "agent")
    public ResponseInfo getAgent() {
        AUser aUser = ShiroUtils.getAUser();
        AgentDto agentDto = agentMapper.toDto(aUser);
        agentDto.setMemberList(aUserMemberService.findByAUserId(aUser.getId()));

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("agent", agentDto);
        return responseInfo;
    }
}
