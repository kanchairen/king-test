package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ShopKindDto;
import com.lky.entity.Shop;
import com.lky.entity.ShopKind;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.ShopKindMapper;
import com.lky.service.ShopKindService;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.ShopResCode.SHOP_NOT_EXIST;

/**
 * 店铺内分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/23
 */
@RestController
@RequestMapping("biz/kind")
@Api(value = "biz/kind", description = "商家店铺内分类管理")
public class BShopKindController extends BaseController {

    @Inject
    private ShopKindService shopKindService;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopKindMapper shopKindMapper;

    @ApiOperation(value = "创建店铺内分类", response = ShopKindDto.class, notes = "shopKind")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "int"),
    })
    @PostMapping("")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @RequestParam String name) {

        AssertUtils.notNull(PARAMS_IS_NULL, name);

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopKind", shopKindMapper.toDto(shopKindService.create(shop, name)));
        return responseInfo;
    }

    @ApiOperation(value = "店铺内分类详情", response = ShopKindDto.class, notes = "shopKind")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        ShopKind shopKind = shopKindService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopKind", shopKindMapper.toDto(shopKind));
        return responseInfo;
    }

    @ApiOperation(value = "店铺内分类列表", response = ShopKindDto.class, notes = "shopKindList", responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);

        List<ShopKind> shopKindList = shopKindService.findByShopId(shop.getId());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopKindList", shopKindMapper.toDtoList(shopKindList));
        return responseInfo;
    }

    @ApiOperation(value = "店铺内分类修改", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "int"),
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @RequestParam String name) {

        AssertUtils.notNull(PARAMS_IS_NULL, id, name);

        ShopKind shopKind = shopKindService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, shopKind);
        shopKindService.modify(shopKind, name);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "店铺内分类设置排序", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceId", value = "源id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "destId", value = "目标id", required = true, paramType = "query", dataType = "int"),
    })
    @PutMapping(value = "sorted")
    public ResponseInfo sorted(@RequestParam Integer sourceId,
                               @RequestParam Integer destId) {

        AssertUtils.notNull(PARAMS_IS_NULL, sourceId, destId);

        ShopKind sourceShopKind = shopKindService.findById(sourceId);
        ShopKind destShopKind = shopKindService.findById(destId);
        AssertUtils.notNull(PARAMS_EXCEPTION, sourceShopKind, destShopKind);

        shopKindService.swapPosition(sourceShopKind, destShopKind);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "删除店铺内分类", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        shopKindService.delete(id);
        return ResponseUtils.buildResponseInfo();
    }
}
