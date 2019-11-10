package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.CategoryDetailDto;
import com.lky.entity.Category;
import com.lky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 类目管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/21
 */
@RestController
@RequestMapping("api/category")
@Api(value = "api/category", description = "类目管理")
public class MCategoryController extends BaseController {

    @Inject
    private CategoryService categoryService;

    @ApiOperation(value = "一级列表", response = Category.class, notes = "categoryList", responseContainer = "List")
    @GetMapping(value = "one/list")
    public ResponseInfo list() {
        List<Category> categoryList = categoryService.listByLevel(CategoryService.LEVEL_ONE);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("categoryList", categoryList);
        return responseInfo;
    }

    @ApiOperation(value = "二、三级列表", response = CategoryDetailDto.class, notes = "categoryDetailList", responseContainer = "List")
    @ApiImplicitParam(name = "parentId", value = "父id", required = true, paramType = "query", dataType = "int")
    @GetMapping(value = "one/detail")
    public ResponseInfo detail(@RequestParam Integer parentId) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL);

        Category category = categoryService.findById(parentId);
        AssertUtils.isTrue(PublicResCode.PARAMS_EXCEPTION, CategoryService.LEVEL_ONE == category.getLevel());

        List<Category> categoryList = categoryService.listByParentId(parentId);
        List<CategoryDetailDto> categoryDetailDtoList = new ArrayList<>(categoryList.size());
        for (Category c : categoryList) {
            CategoryDetailDto categoryDetailDto = new CategoryDetailDto();
            categoryDetailDto.setId(c.getId());
            categoryDetailDto.setName(c.getName());
            categoryDetailDto.setChildCategory(categoryService.listByParentId(c.getId()));
            categoryDetailDtoList.add(categoryDetailDto);
        }

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("categoryDetailList", categoryDetailDtoList);
        return responseInfo;
    }
}
