package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.entity.*;
import com.lky.enums.dict.ABalanceRecordDict;
import com.lky.enums.dict.ARPointRecordDict;
import com.lky.enums.dict.AWPointRecordDict;
import com.lky.service.*;
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
import static com.lky.enums.dict.AIncomeRecordDict.TYPE_DAY;
import static com.lky.enums.dict.AIncomeRecordDict.TYPE_MONTH;

/**
 * 代理商信息Controller
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-25
 */
@RestController
@RequestMapping("sys/auser/info")
@Api(value = "sys/auser/info", description = "系统管理员查看代理商用户信息")
public class SAgentInfoController extends BaseController {

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

    @ApiOperation(value = "查看代理商收益", response = AIncomeRecord.class,
            notes = "incomeRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "代理商id", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "type", value = "查看收益记录的类型:day(按天查看代理商收益明细), month(按月查看代理商收益明细)",
                    allowableValues = "day, month", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "income/{id}")
    public ResponseInfo seeAgentIncome(@PathVariable Integer id,
                                        @RequestParam String type,
                                        @RequestParam(defaultValue = "0") int pageNumber,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        //校验参数
        AUser aUser = aUserService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, aUser);
        AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_DAY, TYPE_MONTH);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        Page<AIncomeRecord> page = aIncomeRecordService.findByAUser(type, id, pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sumIncomeAmount", aUser.getAUserAsset().getSumIncomeAmount());
        responseInfo.putData("yesterdayIncomeAmount", aIncomeRecordService.findYesterdayIncome(id));
        responseInfo.putData("incomeRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "查看代理商大米", response = ABalanceRecord.class,
            notes = "balanceRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "代理商id", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "type", value = "大米记录类型:wpoint_convert_balance(G米转化获得的大米), " +
                    "income(收益获得的大米), withdraw_back(提现失败退回获得的大米), withdraw(提现消耗的大米)",
                    allowableValues = "wpoint_convert_balance, income, withdraw_back, withdraw",
                    paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "balance/{id}")
    public ResponseInfo seeAgentBalance(@PathVariable Integer id,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(defaultValue = "0") int pageNumber,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        //校验参数
        AUser aUser = aUserService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, aUser);
        if (type != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, type,
                    ABalanceRecordDict.TYPE_WPOINT_CONVERT_BALANCE,
                    ABalanceRecordDict.TYPE_INCOME,
                    ABalanceRecordDict.TYPE_WITHDRAW_BACK,
                    ABalanceRecordDict.TYPE_WITHDRAW);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        Page<ABalanceRecord> page = aBalanceRecordService.findByType(id, type, pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("balance", aUser.getAUserAsset().getBalance());
        responseInfo.putData("balanceRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "查看代理商G米", response = AWPointRecord.class,
            notes = "wpointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "代理商id", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "type", value = "G米记录类型:income(收益获得的G米), " +
                    "convert_balance(转化成大米消耗的G米), convert_rpoint(转化成小米消耗的G米)",
                    allowableValues = "income, convert_balance, convert_rpoint",
                    paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "wpoint/{id}")
    public ResponseInfo seeAgentWPoint(@PathVariable Integer id,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(defaultValue = "0") int pageNumber,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        //校验参数
        AUser aUser = aUserService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, aUser);
        if (type != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, type,
                    AWPointRecordDict.TYPE_CONVERT_BALANCE,
                    AWPointRecordDict.TYPE_CONVERT_RPOINT,
                    AWPointRecordDict.TYPE_INCOME);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        Page<AWPointRecord> page = awPointRecordService.findByType(id, type, pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("wpoint", aUser.getAUserAsset().getWpoint());
        //可激励G米
        responseInfo.putData("transWPoint", awPointRecordService.findAUserTransWPoint(id));
        responseInfo.putData("sumConvert", aUser.getAUserAsset().getConvertAmount());
        responseInfo.putData("wpointRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "查看代理商小米", response = ARPointRecord.class,
            notes = "rpointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "代理商id", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "type", value = "小米记录类型:wpoint_convert(G米转化获得的小米), roll_out(转出消耗的小米)",
                    allowableValues = "wpoint_convert, roll_out", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "rpoint/{id}")
    public ResponseInfo seeAgentRPoint(@PathVariable Integer id,
                                       @RequestParam(required = false) String type,
                                       @RequestParam(defaultValue = "0") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        //校验参数
        AUser aUser = aUserService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, aUser);
        if (type != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, type,
                    ARPointRecordDict.TYPE_WPOINT_CONVERT,
                    ARPointRecordDict.TYPE_ROLL_OUT);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        Page<ARPointRecord> page = arPointRecordService.findByType(id, type, pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("rpoint", aUser.getAUserAsset().getRpoint());
        responseInfo.putData("rpointRecordList", page);
        return responseInfo;
    }
}
