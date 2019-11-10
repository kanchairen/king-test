package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.CommentCountDto;
import com.lky.dto.CommentDto;
import com.lky.dto.CreateComment;
import com.lky.entity.ProductGroup;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.service.CommentService;
import com.lky.service.ProductGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

import static com.lky.commons.code.PublicResCode.*;

/**
 * 商品评论
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/31
 */
@RestController
@RequestMapping(value = "biz/comment")
@Api(value = "biz/comment", description = "商品评论")
public class BCommentController extends BaseController {

    @Inject
    private CommentService commentService;

    @Inject
    private ProductGroupService productGroupService;

    @ApiOperation(value = "商家回复评论", response = ResponseInfo.class)
    @PutMapping(value = "reply")
    public ResponseInfo reply(@ApiIgnore @LoginUser User user,
                              @RequestBody CreateComment createComment) {
        AssertUtils.notNull(PARAMS_IS_NULL, createComment.getId());
        AssertUtils.isTrue(PARAMS_IS_NULL, createComment.getAppendReply() != null || createComment.getReply() != null);
        commentService.reply(user, createComment);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "获取订单项的评论", response = CommentDto.class, notes = "comment")
    @GetMapping(value = "{orderItemId}")
    public ResponseInfo getByOrdersId(@PathVariable Integer orderItemId) {

        CommentDto commentDto = commentService.findByOrdersItemId(orderItemId);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("comment", commentDto);
        return responseInfo;
    }

    @ApiOperation(value = "获取商品组的评论列表", response = CommentCountDto.class, notes = "commentCount 评论数, commentList 评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "String", allowableValues = "high, middle, low, image, append",
                    paramType = "query", dataType = "String")
    })
    @GetMapping(value = "get/{productGroupId}")
    public ResponseInfo getByProductGroup(@ApiIgnore @LoginUser User user,
                                          @RequestParam(defaultValue = "0") Integer pageNumber,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String type,
                                          @PathVariable Integer productGroupId) {
        AssertUtils.notNull(PARAMS_IS_NULL, productGroupId);
        ProductGroup productGroup = productGroupService.findById(productGroupId);
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup);
        AssertUtils.isTrue(NOT_AUTHORIZED, user.getId() == productGroup.getShop().getUser().getId());
        Pageable pageable = new PageRequest(pageNumber, pageSize,
                new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("commentList", commentService.listByProductAndType(productGroupId, type, pageable));
        responseInfo.putData("commentCount", commentService.countByProductGroup(productGroupId));
        return responseInfo;
    }

}
