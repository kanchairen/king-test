package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.entity.Category;
import com.lky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

/**
 * 类目管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/21
 */
@RestController
@RequestMapping("biz/category")
@Api(value = "biz/category", description = "类目管理")
public class BCategoryController extends BaseController {

    @Inject
    private CategoryService categoryService;

    @ApiOperation(value = "目录列表", response = Category.class, notes = "categoryList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "类目名称", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@RequestParam(required = false) Integer parentId,
                             @RequestParam(required = false) String name) {

        SimpleSpecificationBuilder<Category> builder = new SimpleSpecificationBuilder<>();
        if (StringUtils.isNotEmpty(name)) {
            builder.add("name", SpecificationOperator.Operator.likeAll, name.trim());
        }
        if (parentId != null) {
            builder.add("parentId", SpecificationOperator.Operator.eq, parentId);
        } else {
            builder.add("parentId", SpecificationOperator.Operator.isNull, null);
        }

        List<Category> categoryList = categoryService.findAll(builder.generateSpecification());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("categoryList", categoryList);
        return responseInfo;
    }

    @ApiOperation(value = "根据目录id查找自己及所有直系父目录", response = Category.class, notes = "categoryList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "类目id", paramType = "path", dataType = "int"),
    })
    @GetMapping(value = "parent/{id}")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@PathVariable int id) {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("categoryList", categoryService.findListByChildId(id));
        return responseInfo;
    }
}
