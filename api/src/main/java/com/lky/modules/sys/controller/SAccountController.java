package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.redis.RedisHelper;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.ShortUrlUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.SubSUserDto;
import com.lky.entity.SRole;
import com.lky.entity.SUser;
import com.lky.enums.dict.SUserDict;
import com.lky.global.constant.Constant;
import com.lky.service.SRoleService;
import com.lky.service.SUserService;
import com.lky.service.SmsLogService;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.*;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.SmsLogDict.TYPE_OPEN_ACCOUNT;
import static com.lky.service.SmsLogService.ACCOUNT_ACTIVE_CODE;

/**
 * 子账号管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/12
 */
@Controller
@RequestMapping(value = "sys/account")
@Api(value = "sys/account", description = "子账号管理")
public class SAccountController extends BaseController {

    @Inject
    private SUserService sUserService;

    @Inject
    private Environment environment;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private RedisHelper redisHelper;

    @Inject
    private SRoleService sRoleService;

    @ApiOperation(value = "添加子账号", response = ResponseInfo.class, notes = "返回新增子账号id")
    @PostMapping(value = "")
    @ResponseBody
    public ResponseInfo create(@RequestBody SubSUserDto sUserDto) {
//        效验参数
        SUser sUser = ShiroUtils.getSUser();
        AssertUtils.notNull(SERVER_EXCEPTION, sUser);
        String[] checkFiled = {"mobile", "password", "username"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, sUserDto.getMobile(), sUserDto.getPassword(), sUserDto.getUsername());
        AssertUtils.isTrue(YSY_NAME_NOT_ALLOW, !Constant.ADMIN.equals(sUserDto.getUsername().trim())
                && !Constant.ADMIN.equals(sUserDto.getUsername()));
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, sUserDto.getMobile());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("id", sUserService.createSubSUser(sUser.getId(), sUserDto));
        return responseInfo;
    }

    @ApiOperation(value = "子账号激活", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code", required = true, paramType = "path", dataType = "string")
    })
    @GetMapping(value = "active/{code}")
    public void active(@PathVariable String code, HttpServletResponse resp) throws IOException {
        Boolean activeResult = Boolean.FALSE;
        String key = ACCOUNT_ACTIVE_CODE + String.valueOf(TYPE_OPEN_ACCOUNT) + ":" + code;
        if (redisHelper.exists(key)) {
            String mobile = (String) redisHelper.get(key);
            SUser subSUser = sUserService.findByMobile(mobile);
            if (subSUser != null) {
                String mobileKey = ACCOUNT_ACTIVE_CODE + String.valueOf(TYPE_OPEN_ACCOUNT) + ":" + mobile;
                if (!String.valueOf(SUserDict.STATE_ACTIVE).equals(subSUser.getState())) {
                    subSUser.setState(String.valueOf(SUserDict.STATE_ACTIVE));
                    subSUser.setUpdateTime(new Date());
                    sUserService.update(subSUser);
                }
                redisHelper.remove(key);
                redisHelper.remove(mobileKey);
                activeResult = true;
            }
        }
        String url = environment.getProperty("apk-server.url") + "/view/account.html?success=" + activeResult;
        resp.sendRedirect(url);
    }

    @ApiOperation(value = "重发子账号激活链接短信", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "子账号id", required = true, paramType = "query", dataType = "int")
    })
    @PostMapping(value = "repeat")
    @ResponseBody
    public ResponseInfo repeat(@RequestParam Integer id) {
        //校验参数
        AssertUtils.notNull(PARAMS_EXCEPTION, id);
        SUser subSUser = sUserService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, subSUser);
        //验证账户是否已经激活
        AssertUtils.isTrue(SYS_ALREADY_ACTIVE, !String.valueOf(SUserDict.STATE_ACTIVE).equals(subSUser.getState()));
        //删除之前redis中激活数据
        String mobileKey = ACCOUNT_ACTIVE_CODE + String.valueOf(TYPE_OPEN_ACCOUNT) + ":" + subSUser.getMobile();
        if (redisHelper.exists(mobileKey)) {
            String code = (String) redisHelper.get(mobileKey);
            String codeKey = ACCOUNT_ACTIVE_CODE + String.valueOf(TYPE_OPEN_ACCOUNT) + ":" + code;
            redisHelper.remove(codeKey);
            redisHelper.remove(mobileKey);
        }
        //重发激活短信操作
        String code = StringUtils.getNumberUUID(6);
        String url = environment.getProperty("apk-server.url") + "/sys/account/active/" + code;
        if (smsLogService.sendActiveCode(subSUser.getMobile(), String.valueOf(TYPE_OPEN_ACCOUNT), ShortUrlUtils.shortUrl(url), code)) {
            return ResponseUtils.buildResponseInfo();
        }
        return ResponseInfo.buildErrorResponseInfo();
    }

    @ApiOperation(value = "获取子账号列表", response = SUser.class, notes = "返回sUserList列表，不带权限信息", responseContainer = "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @ResponseBody
    @GetMapping(value = "list")
    @RequiresPermissions("sub:account:manager:list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        //效验参数
        SUser sUser = ShiroUtils.getSUser();
        AssertUtils.notNull(SERVER_EXCEPTION, sUser);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        SimpleSpecificationBuilder<SUser> builder = new SimpleSpecificationBuilder<>();
        if (!Constant.ADMIN.equals(sUser.getUsername())) {
            builder.add("parentId", SpecificationOperator.Operator.isNotNull, null);
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("sUserList", sUserService.findAll(builder.generateSpecification(), pageable));
        return responseInfo;
    }

    @ApiOperation(value = "删除子账号", response = ResponseInfo.class)
    @ApiImplicitParam(name = "id", value = "子账号id", paramType = "path", required = true, dataType = "int")
    @DeleteMapping(value = "{id}")
    @ResponseBody
    public ResponseInfo delete(@PathVariable Integer id) {
        //效验参数
        SUser sUser = ShiroUtils.getSUser();
        AssertUtils.notNull(SERVER_EXCEPTION, sUser);
        SUser delSUser = sUserService.findById(id);
        AssertUtils.notNull(SYS_NOT_EXIST, delSUser);
        sUserService.deleteSUserAndSRole(delSUser);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "获取子账号用户的权限组名称列表", response = SRole.class, notes = "sRoleList", responseContainer = "list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "子账号用户id", paramType = "path", required = true, dataType = "int"),
    })
    @GetMapping(value = "userRole/{id}")
    @ResponseBody
    public ResponseInfo userRoleList(@PathVariable Integer id) {
        List<SRole> sRoleList = sRoleService.findByUserId(id);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sRoleList", sRoleList);
        return responseInfo;
    }

    @ApiOperation(value = "获取子账号用户信息", response = SubSUserDto.class, notes = "sUserDto")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "子账号用户id", paramType = "path", required = true, dataType = "int"),
    })
    @GetMapping(value = "{id}")
    @ResponseBody
    public ResponseInfo user(@PathVariable Integer id) {
        List<SRole> sRoleList = sRoleService.findByUserId(id);
        SUser sUser = sUserService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, sUser);
        SubSUserDto subSUserDto = new SubSUserDto();
        subSUserDto.setId(sUser.getId());
        subSUserDto.setMobile(sUser.getMobile());
        subSUserDto.setParentId(sUser.getParentId());
        subSUserDto.setSroleList(sRoleList);
        subSUserDto.setUsername(sUser.getUsername());
        subSUserDto.setState(sUser.getState());

        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sUserDto", subSUserDto);
        return responseInfo;
    }

    @ApiOperation(value = "修改用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "子账号用户id", paramType = "path", required = true, dataType = "int"),
    })
    @PutMapping(value = "{id}")
    @ResponseBody
    public ResponseInfo modify(@PathVariable Integer id,
                               @RequestBody SubSUserDto sUserDto) {
        SUser sUser = ShiroUtils.getSUser();
        AssertUtils.notNull(SERVER_EXCEPTION, sUser);
        SUser sourceUser = sUserService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, sourceUser);
        sUserService.edit(sourceUser, sUserDto);
        return ResponseUtils.buildResponseInfo();
    }

}
