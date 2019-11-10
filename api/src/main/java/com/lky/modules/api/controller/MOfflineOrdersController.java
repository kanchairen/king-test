package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.OfflineOrdersDto;
import com.lky.entity.OfflineOrders;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.OfflineOrdersMapper;
import com.lky.service.OfflineOrdersService;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.OrderResCode.POINT_SHOP_NOT_OFFLINE;

/**
 * 线下订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@RestController
@RequestMapping("api/offline/orders")
@Api(value = "api/offline/orders", description = "线下订单")
public class MOfflineOrdersController extends BaseController {

    @Inject
    private OfflineOrdersService offlineOrdersService;

    @Inject
    private OfflineOrdersMapper offlineOrdersMapper;

    @Inject
    private ShopService shopService;

    @ApiOperation(value = "线下订单，获得G米数", response = ResponseInfo.class, notes = "giveWPoint")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "amount", value = "消费总金额", required = true, paramType = "query", dataType = "double"),
    })
    @GetMapping(value = "give/wpoint")
    public ResponseInfo giveWPoint(@ApiIgnore @LoginUser User user,
                                   @RequestParam Integer shopId,
                                   @RequestParam Double amount) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"shopId", "amount"}, shopId, amount);
        double giveWPoint = offlineOrdersService.giveWPoint(user, shopId, amount);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("giveWPoint", giveWPoint);
        return responseInfo;
    }

    @ApiOperation(value = "线下订单", response = ResponseInfo.class, notes = "orderIds")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, paramType = "form", dataType = "int"),
            @ApiImplicitParam(name = "amount", value = "消费总金额", required = true, paramType = "form", dataType = "double"),
    })
    @PostMapping(value = "create")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @RequestParam Integer shopId,
                               @RequestParam Double amount) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"shopId", "amount"}, shopId, amount);

        String orderCode = offlineOrdersService.create(user, shopId, amount);
        AssertUtils.isTrue(POINT_SHOP_NOT_OFFLINE, !shopService.checkPointShop(shopId));

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("orderIds", orderCode);
        return responseInfo;
    }

    @ApiOperation(value = "线下订单列表", response = OfflineOrdersDto.class, notes = "offlineOrdersList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "订单状态",
                    allowableValues = "unpaid,paid", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@ApiIgnore @LoginUser User user,
                             @RequestParam(required = false) String state,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        SimpleSpecificationBuilder<OfflineOrders> builder = new SimpleSpecificationBuilder<>();
        if (StringUtils.isNotEmpty(state)) {
            builder.add("state", SpecificationOperator.Operator.eq, state);
        }
        builder.add("user", SpecificationOperator.Operator.eq, user);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));

        Page<OfflineOrders> offlineOrdersList = offlineOrdersService.findAll(builder.generateSpecification(), pageable);
        List<OfflineOrdersDto> offlineOrdersDtoList = offlineOrdersMapper.toDtoList(offlineOrdersList.getContent());
        Page<OfflineOrdersDto> offlineOrdersDtoPage = new PageImpl<>(offlineOrdersDtoList, pageable, offlineOrdersList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("offlineOrdersList", offlineOrdersDtoPage);
        return responseInfo;
    }
}
