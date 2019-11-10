package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.entity.User;
import com.lky.entity.UserMessage;
import com.lky.global.annotation.LoginUser;
import com.lky.service.UserMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * 用户消息控制层
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-10
 */
@RestController
@RequestMapping("api/userMessage")
@Api(value = "api/userMessage", description = "用户消息")
public class MUserMessageController extends BaseController {

    @Inject
    private UserMessageService userMessageService;

    @ApiOperation(value = "用户消息详情", response = UserMessage.class, notes = "userMessage")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        UserMessage userMessage = userMessageService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("userMessage", userMessage);
        return responseInfo;
    }

    @ApiOperation(value = "用户消息列表", response = UserMessage.class, notes = "userMessageList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "消息类型",
                    allowableValues = "conclude,send,applySuccess,applyFail,withdraw,toAccount",
                    paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String type,
                             @RequestParam(required = false) String title) {
        SimpleSpecificationBuilder<UserMessage> builder = new SimpleSpecificationBuilder<>();
        builder.add("userId", SpecificationOperator.Operator.eq, user.getId());
        if (StringUtils.isNotEmpty(type)) {
            builder.add("targetType", SpecificationOperator.Operator.eq, type);
        }

        if (StringUtils.isNotEmpty(title)) {
            builder.add("title", SpecificationOperator.Operator.likeAll, title);
        }

        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.DESC, "unread"),
                new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = new PageRequest(pageNumber, pageSize, sort);

        Page<UserMessage> userMessageList = userMessageService.findAll(builder.generateSpecification(), pageable);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("userMessageList", userMessageList);
        return responseInfo;
    }

    @ApiOperation(value = "用户消息统计数量", response = ResponseInfo.class, notes = "count")
    @GetMapping(value = "count")
    public ResponseInfo count(@ApiIgnore @LoginUser User user) {
        SimpleSpecificationBuilder<UserMessage> builder = new SimpleSpecificationBuilder<>();
        builder.add("userId", SpecificationOperator.Operator.eq, user.getId());
        builder.add("unread", SpecificationOperator.Operator.eq, true);
        long count = userMessageService.count(builder.generateSpecification());

        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("count", count);
        return responseInfo;
    }

    @ApiOperation(value = "标记已读", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query", dataType = "int"),
    })
    @PutMapping(value = "read")
    public ResponseInfo read(@ApiIgnore @LoginUser User user,
                             @RequestParam(required = false) Integer id) {
        if (id != null) {
            UserMessage userMessage = userMessageService.findById(id);
            AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, userMessage);
            userMessage.setUnread(Boolean.FALSE);
            userMessage.setUpdateTime(new Date());
            userMessageService.update(userMessage);
        } else {
            SimpleSpecificationBuilder<UserMessage> builder = new SimpleSpecificationBuilder<>();
            builder.add("userId", SpecificationOperator.Operator.eq, user.getId());
            builder.add("unread", SpecificationOperator.Operator.eq, true);
            List<UserMessage> userMessageList = userMessageService.findAll(builder.generateSpecification());
            if (!CollectionUtils.isEmpty(userMessageList)) {
                userMessageList.forEach(userMessage -> {
                    userMessage.setUnread(Boolean.FALSE);
                    userMessage.setUpdateTime(new Date());
                    userMessageService.update(userMessage);
                });
            }
        }

        return ResponseUtils.buildResponseInfo();
    }
}
