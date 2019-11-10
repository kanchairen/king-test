package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ShopDto;
import com.lky.dto.ShopHeadDto;
import com.lky.entity.Shop;
import com.lky.mapper.ShopMapper;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

/**
 * 店铺相关
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/9
 */
@RestController
@RequestMapping(value = "sys/shop")
@Api(value = "sys/shop", description = "店铺相关")
public class SShopController extends BaseController {

    @Inject
    private ShopService shopService;

    @Inject
    private ShopMapper shopMapper;


    @ApiOperation(value = "店铺列表", response = ShopDto.class, notes = "shopList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "店铺名称", paramType = "query", dataType = "String")
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String name) {

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "recentSumOrder"));
        Specification<Shop> spec = shopService.buildSpec(null, name);
        Page<Shop> shopList = shopService.findAll(spec, pageable);

        List<ShopDto> shopDtoListList = shopMapper.toDtoList(shopList.getContent());
        Page<ShopDto> shopDtoPage = new PageImpl<>(shopDtoListList, pageable, shopList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopList", shopDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "店铺详情", response = ShopHeadDto.class, notes = "shop")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        Shop shop = shopService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shop", shopMapper.toHeadDto(shop));
        return responseInfo;
    }
}
