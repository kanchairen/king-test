package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.WithdrawRecordDto;
import com.lky.entity.AWithdrawRecord;
import com.lky.entity.WithdrawRecord;
import com.lky.enums.code.AssetResCode;
import com.lky.enums.dict.WithdrawRecordDict;
import com.lky.service.AWithdrawRecordService;
import com.lky.service.WithdrawRecordService;
import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.AssetResCode.MANUAL_BATCH_NOT_NULL;
import static com.lky.enums.dict.WithdrawRecordDict.*;

/**
 * 管理员提现管理
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-16
 */
@RestController
@RequestMapping("sys/withdraw")
@Api(value = "sys/withdraw", description = "用户提现管理")
public class SWithdrawController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SWithdrawController.class);

    @Inject
    private WithdrawRecordService withdrawRecordService;

    @Inject
    private AWithdrawRecordService aWithdrawRecordService;

    @ApiOperation(value = "用户、代理商大米提现明细", response = WithdrawRecordDto.class,
            notes = "withdrawRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "个人提现（person），代理商提现（agent）", required = true,
                    allowableValues = "person,agent", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "提现申请（apply），提现中（agree），" +
                    "用户提现失败(failure), 提现拒绝（refuse），提现完成（finish）",
                    allowableValues = "apply,agree,failure,refuse,finish", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "begin", value = "起始时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "end", value = "结束时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true, paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "")
    @RequiresPermissions("withdraw:manager:list")
    public ResponseInfo getWithdraw(@RequestParam String type,
                                    @RequestParam(required = false) String state,
                                    @RequestParam(required = false) Long begin,
                                    @RequestParam(required = false) Long end,
                                    @RequestParam(defaultValue = "0") Integer pageNumber,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        //校验参数，提现类型
        AssertUtils.isContain(PARAMS_EXCEPTION, type, WITHDRAW_TYPE_PERSON, WITHDRAW_TYPE_AGENT);
        if (state != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, state, STATE_APPLY, STATE_AGREE, STATE_REFUSE, STATE_FAILURE, STATE_FINISH);
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "applyTime"));

        //获取列表
        Page<WithdrawRecordDto> page;
        if (WITHDRAW_TYPE_PERSON.getKey().equals(type)) {
            //个人提现
            page = withdrawRecordService.findByState(null, state, begin, end, Boolean.FALSE, pageable);
        } else {
            //代理商提现
            page = aWithdrawRecordService.findByState(null, state, begin, end, pageable);
        }

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("withdrawRecordList", page);
        return responseInfo;
    }

    @ApiOperation(value = "同意用户、代理商提现申请", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "大米提现记录id",
                    required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "个人提现（person），代理商提现（agent）", required = true,
                    allowableValues = "person,agent", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "同意用户提现申请（agree），拒绝用户提现申请（refuse）",
                    allowableValues = "agree,refuse", paramType = "query", dataType = "String"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 60006, message = "提现处于申请状态才可被同意"),
            @ApiResponse(code = 60007, message = "易通金服商户余额不足"),
            @ApiResponse(code = 60008, message = "申请易通金服请求受理失败"),
    })
    @PutMapping(value = "")
    public ResponseInfo agreeWithdraw(@RequestParam Integer id,
                                      @RequestParam String type,
                                      @RequestParam String state) {
        //参数校验
        AssertUtils.isContain(PARAMS_EXCEPTION, type, WITHDRAW_TYPE_PERSON, WITHDRAW_TYPE_AGENT);
        AssertUtils.isContain(PARAMS_EXCEPTION, state, STATE_AGREE, STATE_REFUSE);

        if (WITHDRAW_TYPE_PERSON.getKey().equals(type)) {
            //个人提现
            WithdrawRecord withdrawRecord = withdrawRecordService.findById(id);
            AssertUtils.notNull(PARAMS_EXCEPTION, withdrawRecord);
            AssertUtils.isTrue(AssetResCode.WITHDRAW_STATE_NOT_APPLY,
                    STATE_APPLY.getKey().equals(withdrawRecord.getState()));

            if (STATE_AGREE.getKey().equals(state)) {
                //同意用户提现
                withdrawRecordService.agreeWithdraw(withdrawRecord);
            } else {
                //拒绝用户提现
                withdrawRecordService.refuseWithdraw(withdrawRecord);
            }
        } else {
            //代理商提现
            AWithdrawRecord aWithdrawRecord = aWithdrawRecordService.findById(id);
            AssertUtils.notNull(PARAMS_EXCEPTION, aWithdrawRecord);
            AssertUtils.isTrue(AssetResCode.WITHDRAW_STATE_NOT_APPLY,
                    STATE_APPLY.getKey().equals(aWithdrawRecord.getState()));

            if (STATE_AGREE.getKey().equals(state)) {
                //同意代理商提现
                aWithdrawRecordService.agreeWithdraw(aWithdrawRecord);
            } else {
                //拒绝代理商提现
                aWithdrawRecordService.refuseWithdraw(aWithdrawRecord);
            }
        }

        return ResponseInfo.buildSuccessResponseInfo();
    }

    /**
     * 根据条件查找会员，导出会员列表
     */
    @ApiOperation(value = "用户、代理商大米提现明细", response = WithdrawRecordDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "个人提现（person），代理商提现（agent）", required = true,
                    allowableValues = "person,agent", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "提现申请（apply），提现中（agree），提现拒绝（refuse），提现失败（failure），提现完成（finish）",
                    allowableValues = "apply,agree,failure,finish", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "begin", value = "起始时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "end", value = "结束时间", paramType = "query", dataType = "long"),
    })
    @GetMapping(value = "report")
    public ResponseEntity withdrawReport(@RequestParam String type,
                                         @RequestParam(required = false) String state,
                                         @RequestParam(required = false) Long begin,
                                         @RequestParam(required = false) Long end) {
        if (state != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, state, STATE_APPLY, STATE_AGREE, STATE_REFUSE, STATE_FAILURE, STATE_FINISH);
        }

        if (WITHDRAW_TYPE_PERSON.getKey().equals(type)) {
            //个人提现
            return createResponseEntity(withdrawRecordService.buildWithdrawReport(state, begin, end));
        } else {
            //代理商提现
            return createResponseEntity(aWithdrawRecordService.buildWithdrawReport(state, begin, end));
        }
    }

    private ResponseEntity createResponseEntity(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName.substring(fileName.lastIndexOf("/") + 1));

        byte[] bytes = new byte[0];
        File file = new File(fileName);
        try {
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
    }

    @ApiOperation(value = "批量手工处理提现", notes = "手工处理申请中/提现失败的提现记录", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "个人提现（person），代理商提现（agent）", required = true,
                    allowableValues = "person,agent", paramType = "query", dataType = "String"),
    })
    @PutMapping(value = "manual/batch")
    public ResponseInfo manualBatch(@ApiParam(value = "大米提现记录id列表", example = "[12,13]") @RequestBody List<Integer> ids,
                                    @RequestParam String type) {
        //参数校验
        AssertUtils.notNull(MANUAL_BATCH_NOT_NULL, ids);
        AssertUtils.isContain(PARAMS_EXCEPTION, type, WITHDRAW_TYPE_PERSON, WITHDRAW_TYPE_AGENT);

        WithdrawRecordDict withdrawType = WithdrawRecordDict.getEnum(type);
        switch (withdrawType) {
            case WITHDRAW_TYPE_PERSON:
                //个人提现
                List<WithdrawRecord> withdrawRecordList = withdrawRecordService.findList(ids);
                if (!CollectionUtils.isEmpty(withdrawRecordList)) {
                    withdrawRecordService.update(withdrawRecordList.stream()
                            .filter(w -> STATE_APPLY.compare(w.getState()) || STATE_FAILURE.compare(w.getState()))
                            .map(w -> {
                                w.setState(STATE_FINISH.getKey());
                                w.setUpdateTime(new Date());
                                w.setFinishTime(new Date());
                                return w;
                            }).collect(Collectors.toList()));
                }
                break;
            case WITHDRAW_TYPE_AGENT:
                //代理商提现
                List<AWithdrawRecord> aWithdrawRecordList = aWithdrawRecordService.findList(ids);
                if (!CollectionUtils.isEmpty(aWithdrawRecordList)) {
                    aWithdrawRecordService.update(aWithdrawRecordList.stream()
                            .filter(aw -> STATE_APPLY.compare(aw.getState()) || STATE_FAILURE.compare(aw.getState()))
                            .map(aw -> {
                                aw.setState(STATE_FINISH.getKey());
                                aw.setUpdateTime(new Date());
                                aw.setFinishTime(new Date());
                                return aw;
                            }).collect(Collectors.toList()));
                }
        }

        return ResponseInfo.buildSuccessResponseInfo();
    }
}
