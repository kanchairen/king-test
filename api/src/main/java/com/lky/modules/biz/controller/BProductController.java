package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ProductDto;
import com.lky.dto.ProductGroupDto;
import com.lky.entity.*;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.ProductMapper;
import com.lky.service.ProductGroupService;
import com.lky.service.ProductService;
import com.lky.service.ShopKindService;
import com.lky.service.ShopService;
import io.swagger.annotations.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static com.lky.commons.code.PublicResCode.NOT_AUTHORIZED;
import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;

/**
 * 商品管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/24
 */
@RestController
@RequestMapping("biz/product")
@Api(value = "biz/product", description = "商品管理")
public class BProductController extends BaseController {

    @Inject
    private ProductService productService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ProductMapper productMapper;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopKindService shopKindService;

    @ApiOperation(value = "添加新商品", response = ResponseInfo.class, notes = "productGroupId")
    @PostMapping("")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @ApiParam(name = "productGroupDto", value = "商品组dto")
                               @RequestBody ProductGroupDto productGroupDto) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, shop);
        productGroupService.checkParams(productGroupDto);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupId", productGroupService.add(shop, productGroupDto));
        return responseInfo;
    }

    @ApiOperation(value = "添加同款商品", response = ResponseInfo.class)
    @PostMapping("same")
    public ResponseInfo createSame(@ApiIgnore @LoginUser User user, @RequestBody List<ProductDto> productDtoList,
                                   @RequestParam Integer productGroupId
    ) {
        ProductGroup productGroup = productGroupService.findById(productGroupId);
        Shop shop = shopService.findByUser(user);
        AssertUtils.isTrue(PARAMS_EXCEPTION, Objects.equals(shop, productGroup.getShop()));
        productGroupService.addSame(shop, productGroup, productDtoList);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "根据条件获取商品列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "商品组名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "sellState", value = "商品售卖状态", allowableValues = "onSell,sellOut,offline", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "auditState", value = "商品审核状态", allowableValues = "wait,no", paramType = "query", dataType = "string"),
    })
    @GetMapping("list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user,
                             @RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String auditState,
                             @RequestParam(required = false) String sellState) {

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
    @PutMapping("offline")
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


    @ApiOperation(value = "修改商品组信息", response = ResponseInfo.class)
    @PutMapping("edit")
    public ResponseInfo edit(@ApiIgnore @LoginUser User user,
                             @RequestBody ProductGroupDto productGroupDto) {

        ProductGroup sourceProductGroup = productGroupService.findById(productGroupDto.getId());
        //效验参数
        AssertUtils.notNull(PARAMS_EXCEPTION, sourceProductGroup);
        Shop shop = shopService.findByUser(user);
        //效验权限
        AssertUtils.isTrue(PARAMS_EXCEPTION, sourceProductGroup.getShop() == shop);

        //设置商品是否支持红积分支付
        ShopConfig shopConfig = shop.getShopConfig();
        sourceProductGroup.setSupportRPoint(shopConfig.getOpenRPoint());
        productGroupService.checkParams(productGroupDto);

        productGroupService.modify(productGroupDto, sourceProductGroup);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "根据id获取商品组", response = ProductGroupDto.class, notes = "productGroup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品组id", required = true, paramType = "path", dataType = "int"),
    })
    @GetMapping("{id}")
    public ResponseInfo findById(@ApiIgnore @LoginUser User user,
                                 @PathVariable Integer id) {
        ProductGroup productGroup = productGroupService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, productGroup);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        ProductGroupDto productGroupDto = productMapper.toGroupDto(productGroup);
        productGroupDto.setDetail(productGroup.getDetail());
        //用户默认地址/和运费模板计算运费
        productGroupDto.setFreightPrice(productGroupService.freightCalculation(productGroup, user.getId(), null, null));
        //商品组评分计算
        productGroupDto.setEvaluate(productGroupService.evaluateCalculation(productGroup.getId()));
        responseInfo.putData("productGroup", productGroupDto);
        return responseInfo;
    }

    @ApiOperation(value = "根据店铺内分类id获取商品组列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "分类id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "商品名称", paramType = "query", dataType = "String"),
    })
    @GetMapping("listByKind/{id}")
    public ResponseInfo findByShopKindId(@ApiIgnore @LoginUser User user,
                                         @RequestParam(defaultValue = "0") int pageNumber,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(required = false) String name,
                                         @PathVariable Integer id) {
        ShopKind shopKind = shopKindService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, shopKind);
        Shop shop = shopService.findByUser(user);
        AssertUtils.isTrue(PARAMS_EXCEPTION, shop.getId() == shopKind.getShopId());
        org.springframework.data.domain.Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productGroupService.findListByKind(shop.getId(), shopKind.getId(), name, pageable));
        return responseInfo;
    }

    @ApiOperation(value = "修改商品库存", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "商品id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "num", value = "数量", required = true, paramType = "query", dataType = "int"),
    })
    @PutMapping("edit/stock")
    public ResponseInfo editStock(@ApiIgnore @LoginUser User user,
                                  @RequestParam Integer productId, @RequestParam Integer num) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, num >= 0);
        Product product = productService.findById(productId);
        //效验参数
        AssertUtils.notNull(PARAMS_EXCEPTION, product);
        Shop shop = shopService.findByUser(user);
        //效验权限
        AssertUtils.isTrue(NOT_AUTHORIZED, product.getShopId() == shop.getId());
        int stock = product.getStock();
        if (num != stock) {
            product.setStock(num);
            productService.update(product);
            if (stock == 0 || num == 0) {
                ProductGroup productGroup = productGroupService.findById(product.getProductGroupId());
                productGroupService.updateStateNumberAndPrice(productGroup, Boolean.TRUE);
                productGroupService.update(productGroup);
            }
        }
        return ResponseInfo.buildSuccessResponseInfo();
    }
}
