package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.AgentDto;
import com.lky.entity.AUser;
import com.lky.enums.code.UserResCode;
import com.lky.enums.dict.AUserDict;
import com.lky.enums.dict.AUserInfoDict;
import com.lky.mapper.AgentMapper;
import com.lky.service.AUserMemberService;
import com.lky.service.AUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;

/**
 * 代理商管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/21
 */
@RestController
@RequestMapping(value = "sys/agent")
@Api(value = "sys/agent", description = "代理商管理")
public class SAgentController extends BaseController {

    @Inject
    private AUserService aUserService;

    @Inject
    private AgentMapper agentMapper;

    @Inject
    private AUserMemberService aUserMemberService;

    @ApiOperation(value = "添加代理商", response = ResponseInfo.class, notes = "返回新增代理商id")
    @PostMapping(value = "")
    public ResponseInfo create(@RequestBody AgentDto agentDto) {
        //效验参数
        this.checkParameter(agentDto, null);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("id", aUserService.add(agentDto));
        return responseInfo;
    }

    @ApiOperation(value = "代理商列表", response = AgentDto.class, notes = "agentList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "代理级别", paramType = "query", dataType = "string", allowableValues = "province, city, district"),
            @ApiImplicitParam(name = "condition", value = "代理地区，姓名，手机号模糊查询", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "list")
    @RequiresPermissions("agent:manager:list")
    public ResponseInfo list(@RequestParam(required = false) String level,
                             @RequestParam(required = false) String condition,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("agentList", aUserService.findListByCondition(pageNumber, pageSize, level, condition));
        return responseInfo;
    }

    @ApiOperation(value = "根据id获取代理商详情", response = AgentDto.class, notes = "agent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "代理商id", paramType = "path", dataType = "int"),
    })
    @GetMapping(value = "{id}")
    public ResponseInfo getAgent(@PathVariable Integer id) {
        AUser aUser = aUserService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, aUser);
        AgentDto agentDto = agentMapper.toDto(aUser);
        agentDto.setMemberList(aUserMemberService.findByAUserId(id));

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("agent", agentDto);
        return responseInfo;
    }

    @ApiOperation(value = "编辑代理商信息", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "代理商id", paramType = "path", dataType = "int", required = true)
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @RequestBody AgentDto agentDto) {
        AUser sourceAUser = aUserService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, sourceAUser);

        //效验参数
        this.checkParameter(agentDto, sourceAUser);
        aUserService.modify(sourceAUser, agentDto);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    private void checkParameter(AgentDto agentDto, AUser sourceAUser) {
        //效验参数
        String[] checkFields = {"mobile", "payAll", "level", "area", "incomeRate" , "beginAgentDate", "amount",
                "memberList", "state"};
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields, agentDto.getMobile(), agentDto.getPayAll(),
                agentDto.getLevel(), agentDto.getArea(), agentDto.getIncomeRate(), agentDto.getBeginAgentDate(), agentDto.getAmount(),
                agentDto.getMemberList(), agentDto.getState());
        AssertUtils.isContain(PARAMS_EXCEPTION, agentDto.getLevel(), AUserInfoDict.LEVEL_PROVINCE, AUserInfoDict.LEVEL_CITY, AUserInfoDict.LEVEL_DISTRICT);
        AssertUtils.isContain(PARAMS_EXCEPTION, agentDto.getState(), AUserDict.AGENT_STATE_ACTIVE, AUserDict.AGENT_STATE_LOCK);

        AssertUtils.isMobile(UserResCode.MOBILE_FORMAT_ERROR, agentDto.getMobile());
        if ((sourceAUser != null && !agentDto.getMobile().equals(sourceAUser.getMobile())) || sourceAUser == null) {
            AUser aUser = aUserService.findByCellphone(agentDto.getMobile());
            AssertUtils.isNull(UserResCode.MOBILE_EXIST, aUser);
        }
        //地区校验
        aUserService.verifyAddress(agentDto.getLevel(), agentDto.getArea(), sourceAUser);
    }

}