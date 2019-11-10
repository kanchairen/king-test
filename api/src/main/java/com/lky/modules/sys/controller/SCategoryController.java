package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.entity.Category;
import com.lky.service.CategoryService;
import io.swagger.annotations.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.ShopResCode.CATEGORY_NAME_EXIST;
import static com.lky.enums.code.ShopResCode.CATEGORY_USED;

/**
 * 类目管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/21
 */
@RestController
@RequestMapping("sys/category")
@Api(value = "sys/category", description = "类目管理")
public class SCategoryController extends BaseController {

    @Inject
    private CategoryService categoryService;

    @ApiOperation(value = "创建类目", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "category", value = "类目")
                               @RequestBody Category category) {

        AssertUtils.notNull(PARAMS_IS_NULL, category);
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"name", "level"},
                category.getName(), category.getLevel());
        AssertUtils.isTrue(CATEGORY_NAME_EXIST, categoryService.countByLevelAndName(category.getLevel(), category.getName()) == 0);
        categoryService.create(category);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "类目详情", response = Category.class, notes = "category")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("category", categoryService.findById(id));
        return responseInfo;
    }

    @ApiOperation(value = "类目列表", response = Category.class, notes = "categoryList", responseContainer = "List")
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

    @ApiOperation(value = "修改类目", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int"),
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "category", value = "类目")
                             @RequestBody Category category) {

        AssertUtils.notNull(PARAMS_IS_NULL, category);
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"id", "name", "level"},
                id, category.getName(), category.getLevel());

        Category sourceCategory = categoryService.findById(id);
        if (!sourceCategory.getName().equals(category.getName())) {
            AssertUtils.isTrue(CATEGORY_NAME_EXIST, categoryService.countByLevelAndName(sourceCategory.getLevel(), category.getName()) == 0);
        }
        sourceCategory.setName(category.getName());
        sourceCategory.setLogoImg(category.getLogoImg());
        sourceCategory.setLevel(category.getLevel());
        categoryService.save(sourceCategory);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "删除类目", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        List<Category> categoryList = categoryService.findAllListById(id);
        AssertUtils.isTrue(CATEGORY_USED, categoryService.sumUsed(categoryList) == 0);
        categoryService.delete(categoryList);
        return ResponseUtils.buildResponseInfo();
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
