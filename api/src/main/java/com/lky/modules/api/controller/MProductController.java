package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ProductDto;
import com.lky.dto.ProductGroupDto;
import com.lky.entity.Product;
import com.lky.entity.ProductGroup;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.enums.code.ShopResCode;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.annotation.LoginUser;
import com.lky.global.annotation.MerchantSign;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.mapper.ProductMapper;
import com.lky.service.ProductGroupService;
import com.lky.service.ProductService;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.OrderResCode.PRODUCT_IS_OFFLINE;
import static com.lky.enums.dict.ProductGroupDict.AUDIT_STATE_YES;

/**
 * 商品相关
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/26
 */
@RestController
@RequestMapping("api/product")
@Api(value = "api/product", description = "商品相关")
public class MProductController extends BaseController {

    @Inject
    private ProductService productService;

    @Inject
    private ShopService shopService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ProductMapper productMapper;

    @Inject
    private TokenManager tokenManager;

    @ApiOperation(value = "根据id获取商品组", response = ProductGroupDto.class, notes = "productGroup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品组id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "lng", value = "用户经度", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lat", value = "用户纬度", paramType = "query", dataType = "String"),
    })
    @GetMapping("{id}")
    @AuthIgnore
    public ResponseInfo findById(HttpServletRequest request,
                                 @RequestParam(required = false) String lng,
                                 @RequestParam(required = false) String lat,
                                 @PathVariable Integer id) throws UnsupportedEncodingException {
        ProductGroup productGroup = productGroupService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup);
        //效验商品状态：待审核或是下架商品
        AssertUtils.isTrue(PRODUCT_IS_OFFLINE, AUDIT_STATE_YES.compare(productGroup.getAuditState()) &&
                !productGroup.getOffline());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        ProductGroupDto productGroupDto = productMapper.toGroupDto(productGroup);
        productGroupDto.setDetail(productGroup.getDetail());
        productGroupDto.getProductDtoList().removeIf(ProductDto::getOffline);
        //根据经纬度/用户默认地址/和运费模板计算运费
        String authToken = super.getAppUserToken(request);
        if (authToken != null) {
            Integer userId = tokenManager.getUserIdByToken(TokenModel.TYPE_APP, authToken);
            productGroupDto.setFreightPrice(productGroupService.freightCalculation(productGroup, userId, lng, lat));
        }
        //商品组评分计算
        productGroupDto.setEvaluate(productGroupService.evaluateCalculation(productGroup.getId()));
        responseInfo.putData("productGroup", productGroupDto);
        return responseInfo;
    }

    @ApiOperation(value = "获取店铺内的商品列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "店铺id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "shopKindId", value = "店铺分类id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "state", value = "商品状态", allowableValues = "WPoint,saleNum,price", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "desc", value = "是否降序", paramType = "query", dataType = "boolean"),
    })
    @GetMapping("list/{id}")
    @AuthIgnore
    public ResponseInfo findByShpId(@PathVariable Integer id,
                                    @RequestParam(defaultValue = "0") Integer pageNumber,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) Integer shopKindId,
                                    @RequestParam(required = false) String state,
                                    @RequestParam(required = false) Boolean desc
    ) {
        Shop shop = shopService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, shop);
        //判断店铺是否已经过期/关闭。
        AssertUtils.isTrue(ShopResCode.SHOP_CLOSE, shopService.judgeShopExpire(shop));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productService.findByShopId
                (shop.getId(), pageNumber, pageSize, shopKindId, state, desc));
        return responseInfo;
    }

    @ApiOperation(value = "根据商品类目获取列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "categoryId", value = "类目id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "state", value = "商品状态", allowableValues = "newly,saleNum,price", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "desc", value = "是否降序", paramType = "query", dataType = "boolean"),
    })
    @GetMapping("category/list")
    public ResponseInfo findByCategory(@RequestParam Integer categoryId,
                                       @RequestParam(defaultValue = "0") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) String state,
                                       @RequestParam(required = false) Boolean desc
    ) {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productService.findByCategoryId
                (pageNumber, pageSize, categoryId, state, desc));
        return responseInfo;
    }

    @ApiOperation(value = "商品名称获取列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "商品名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "state", value = "商品状态", allowableValues = "newly,saleNum,price", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "desc", value = "是否降序", paramType = "query", dataType = "boolean"),
    })
    @GetMapping("name/list")
    public ResponseInfo findByCategory(@RequestParam(required = false) String name,
                                       @RequestParam(defaultValue = "0") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) String state,
                                       @RequestParam(required = false) Boolean desc
    ) {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productService.findByName
                (pageNumber, pageSize, name, state, desc));
        return responseInfo;
    }

    @ApiOperation(value = "获取商品管理列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "商品组名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "sellState", value = "商品售卖状态", allowableValues = "onSell,sellOut,offline", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "auditState", value = "商品审核状态", allowableValues = "wait,yes", paramType = "query", dataType = "string"),
    })
    @MerchantSign
    @GetMapping("owner/list")
    public ResponseInfo ownerList(@ApiIgnore @LoginUser User user,
                                  @RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "10") int pageSize,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) String sellState,
                                  @RequestParam(required = false) String auditState
    ) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(PARAMS_EXCEPTION, shop);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productService.findByShopIdAndState
                (shop.getId(), pageNumber, pageSize, name, auditState, sellState));
        return responseInfo;
    }


    @ApiOperation(value = "批量上架/下架商品", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "offline", value = "上架/下架", required = true, paramType = "query", dataType = "boolean"),
    })
    @MerchantSign
    @PutMapping("owner/offline")
    public ResponseInfo offline(@ApiIgnore @LoginUser User user,
                                @RequestBody Integer[] idList,
                                @RequestParam Boolean offline) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, !CollectionUtils.isEmpty(idList));
        Shop shop = shopService.findByUser(user);
        List<Product> productList = productService.findList(idList);
        if (productGroupService.changeOffline(shop.getId(), offline, productList)) {
            return ResponseInfo.buildSuccessResponseInfo();
        }
        return ResponseInfo.buildErrorResponseInfo();
    }
}
