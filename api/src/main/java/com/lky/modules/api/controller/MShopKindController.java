package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ShopKindDto;
import com.lky.entity.Shop;
import com.lky.entity.ShopKind;
import com.lky.mapper.ShopKindMapper;
import com.lky.service.ShopKindService;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.code.ShopResCode.SHOP_NOT_EXIST;

/**
 * 店铺内分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/23
 */
@RestController
@RequestMapping("api/kind")
@Api(value = "api/kind", description = "商家店铺内分类管理")
public class MShopKindController extends BaseController {

    @Inject
    private ShopKindService shopKindService;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopKindMapper shopKindMapper;

    @ApiOperation(value = "店铺内分类列表", response = ShopKindDto.class, notes = "shopKindList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "shopId", required = true, paramType = "query", dataType = "int")
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam Integer shopId) {

        Shop shop = shopService.findById(shopId);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);

        List<ShopKind> shopKindList = shopKindService.findByShopId(shop.getId());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopKindList", shopKindMapper.toDtoList(shopKindList));
        return responseInfo;
    }
}
