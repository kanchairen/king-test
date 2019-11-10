package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.CommentCountDto;
import com.lky.dto.CommentDto;
import com.lky.dto.CreateComment;
import com.lky.entity.Comment;
import com.lky.entity.ProductGroup;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.CommentMapper;
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
import java.util.ArrayList;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;

/**
 * 商品评论
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/30
 */
@RestController
@RequestMapping(value = "api/comment")
@Api(value = "api/comment", description = "商品评论")
public class MCommentController extends BaseController {

    @Inject
    private CommentService commentService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private CommentMapper commentMapper;

    @ApiOperation(value = "初次评论", response = ResponseInfo.class)
    @PostMapping(value = "")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @RequestBody List<CreateComment> createCommentList) {
        AssertUtils.isTrue(PARAMS_IS_NULL, !CollectionUtils.isEmpty(createCommentList));
        commentService.create(user, createCommentList);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "追加评论", response = ResponseInfo.class)
    @PutMapping(value = "append")
    public ResponseInfo createAppend(@ApiIgnore @LoginUser User user,
                                     @RequestBody List<CreateComment> createCommentList) {
        AssertUtils.isTrue(PARAMS_IS_NULL, !CollectionUtils.isEmpty(createCommentList));
        commentService.createAppend(user, createCommentList);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "获取商品组的评论列表", response = CommentCountDto.class, notes = "commentList, commentCount", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "String", allowableValues = "high, middle, low, image, append",
                    paramType = "query", dataType = "String")
    })
    @GetMapping(value = "get/{productGroupId}")
    public ResponseInfo getByProductGroup(@RequestParam(defaultValue = "0") Integer pageNumber,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String type,
                                          @PathVariable Integer productGroupId) {
        AssertUtils.notNull(PARAMS_IS_NULL, productGroupId);
        ProductGroup productGroup = productGroupService.findById(productGroupId);
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup);
        Pageable pageable = new PageRequest(pageNumber, pageSize,
                new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("commentList", commentService.listByProductAndType(productGroupId, type, pageable));
        responseInfo.putData("commentCount", commentService.countByProductGroup(productGroupId));
        return responseInfo;
    }

    @ApiOperation(value = "获取订单的评论列表", response = CommentDto.class, notes = "commentList", responseContainer = "List")
    @GetMapping(value = "order/{ordersId}")
    public ResponseInfo getByOrdersId(@ApiIgnore @LoginUser User user,
                                      @PathVariable String ordersId) {
        AssertUtils.isTrue(PARAMS_IS_NULL, StringUtils.isNotEmpty(ordersId));
        SimpleSpecificationBuilder<Comment> builder = new SimpleSpecificationBuilder<>();
        builder.add("user", SpecificationOperator.Operator.eq, user.getId());
        builder.add("ordersId", SpecificationOperator.Operator.eq, ordersId);
        List<Comment> commentList = commentService.findAll(builder.generateSpecification());
        List<CommentDto> commentDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(commentList)) {
            AssertUtils.isTrue(PARAMS_EXCEPTION, user.getId() == commentList.get(0).getUser().getId());
            commentList.forEach(comment -> commentDtoList.add(commentMapper.toDto(comment)));
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("commentList", commentDtoList);
        return responseInfo;
    }

    @ApiOperation(value = "获取商品组的各维度评论数字", response = CommentCountDto.class, notes = "commentCount")
    @GetMapping(value = "count")
    public ResponseInfo count(@RequestParam Integer productGroupId) {
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroupId);
        CommentCountDto commentCountDto = commentService.countByProductGroup(productGroupId);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("commentCount", commentCountDto);
        return responseInfo;
    }

}
