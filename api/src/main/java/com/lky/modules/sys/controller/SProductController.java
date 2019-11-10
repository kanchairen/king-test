package com.lky.modules.sys.controller;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.ProductDao;
import com.lky.dto.ProductDto;
import com.lky.dto.ProductGroupDto;
import com.lky.entity.Category;
import com.lky.entity.Product;
import com.lky.entity.ProductGroup;
import com.lky.entity.Shop;
import com.lky.mapper.ProductMapper;
import com.lky.service.CategoryService;
import com.lky.service.ProductGroupService;
import com.lky.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.ShopResCode.STATE_NOT_WAIT;
import static com.lky.enums.dict.ProductGroupDict.*;

/**
 * 商品
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/28
 */
@RestController
@RequestMapping(value = "sys/product")
@Api(value = "sys/product", description = "商品")
public class SProductController extends BaseController {

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ProductMapper productMapper;

    @Inject
    private ProductService productService;

    @Inject
    private ProductDao productDao;

    @Inject
    private CategoryService categoryService;

    @ApiOperation(value = "商品审核记录列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "店铺名称/商品名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "state", value = "审核状态", allowableValues = "wait,yes,no",
                    paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    @RequiresPermissions("product:manager:list")
    public ResponseInfo listByState(@RequestParam(defaultValue = "0") int pageNumber,
                                    @RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String state) {

        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Specification<ProductGroup> spec = (root, query, cb) -> {
            Join<ProductGroup, Shop> shopJoin = root.join("shop", JoinType.LEFT);
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(name)) {
                predicates.add(cb.or(cb.like(shopJoin.get("name"), "%" + name.trim() + "%"),
                        cb.like(root.get("name"), "%" + name.trim() + "%")));
            }
            if (StringUtils.isNotEmpty(state)) {
                AssertUtils.isContain(PARAMS_EXCEPTION, state, AUDIT_STATE_WAIT, AUDIT_STATE_YES, AUDIT_STATE_NO);
                predicates.add(cb.equal(root.get("auditState"), state));
            }
            predicates.add(cb.equal(root.get("offline"), Boolean.FALSE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Page<ProductGroup> productGroupList = productGroupService.findAll(spec, pageable);
        List<ProductGroupDto> productGroupDtoList = productMapper.toPageDto(productGroupList.getContent());
        Page<ProductGroupDto> productGroupDtoPage = new PageImpl<>(productGroupDtoList, pageable, productGroupList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productGroupDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "修改商品类目", response = ProductGroupDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productGroupId", value = "商品组id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "categoryId", value = "类目id", paramType = "query", dataType = "string"),
    })
    @PutMapping("category/{productGroupId}")
    public ResponseInfo modify(@PathVariable Integer productGroupId,
                               @RequestParam Integer categoryId) {
        ProductGroup productGroup = productGroupService.findById(productGroupId);
        Category category = categoryService.findById(categoryId);
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup, category);
        AssertUtils.isTrue(PARAMS_EXCEPTION, CategoryService.LEVEL_THREE == category.getLevel());
        productGroup.setCategory(category);
        productGroupService.update(productGroup);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "根据id获取商品组", response = ProductGroupDto.class, notes = "productGroup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品组id", required = true, paramType = "path", dataType = "int"),
    })
    @GetMapping("{id}")
    public ResponseInfo findById(@PathVariable Integer id) {
        ProductGroup productGroup = productGroupService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        ProductGroupDto productGroupDto = productMapper.toGroupDto(productGroup);
        productGroupDto.setDetail(productGroup.getDetail());
        productGroupDto.getProductDtoList().removeIf(ProductDto::getOffline);
        //商品组评分计算
        productGroupDto.setEvaluate(productGroupService.evaluateCalculation(productGroup.getId()));
        responseInfo.putData("productGroup", productGroupDto);
        return responseInfo;
    }

    @ApiOperation(value = "商品审核", response = ProductGroupDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品组id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "auditRemark", value = "审核备注", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "state", value = "审核状态", allowableValues = "yes,no",
                    paramType = "query", dataType = "string"),
    })
    @PutMapping("audit/{id}")
    public ResponseInfo modify(@PathVariable Integer id,
                               @RequestParam String state,
                               @RequestParam(required = false) String auditRemark) {
        ProductGroup productGroup = productGroupService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup);
        AssertUtils.isTrue(STATE_NOT_WAIT, AUDIT_STATE_WAIT.getKey().equals(productGroup.getAuditState()));
        productGroup.setAuditState(state);
        productGroup.setAuditRemark(auditRemark);
        productGroup.setUpdateTime(new Date());
        productGroupService.update(productGroup);
        return ResponseInfo.buildSuccessResponseInfo();
    }


    @ApiOperation(value = "商品列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "商品名称", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "get/list")
    public ResponseInfo listByState(@RequestParam(defaultValue = "0") int pageNumber,
                                    @RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(required = false) String name) {

        Pageable pageable = new PageRequest(pageNumber, pageSize);
        Page<ProductGroup> productGroupList = productGroupService.findAll(
                productService.buildAllProductByName(name, Boolean.FALSE), pageable);
        List<ProductGroupDto> productGroupDtoList = productMapper.toPageDto(productGroupList.getContent());
        Page<ProductGroupDto> productGroupDtoPage = new PageImpl<>(productGroupDtoList, pageable, productGroupList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productGroupDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "商品组下架", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品组id", required = true, paramType = "path", dataType = "int"),
    })
    @PutMapping("offline/{id}")
    public ResponseInfo offline(@PathVariable Integer id) {
        ProductGroup productGroup = productGroupService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, new String[]{"id"}, productGroup);
        List<Product> productList = productDao.findByProductGroupId(id);
        if (productGroupService.changeOffline(productGroup.getShop().getId(), Boolean.TRUE, productList)) {
            return ResponseInfo.buildSuccessResponseInfo();
        }
        return ResponseInfo.buildErrorResponseInfo();
    }

}
