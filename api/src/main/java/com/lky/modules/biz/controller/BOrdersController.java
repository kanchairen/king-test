package com.lky.modules.biz.controller;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.BOrdersListDto;
import com.lky.dto.OrdersDetailDto;
import com.lky.entity.Orders;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.OrdersMapper;
import com.lky.service.OrdersService;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.OrderResCode.STATE_ERROR;
import static com.lky.enums.code.ShopResCode.SHOP_NOT_EXIST;
import static com.lky.enums.dict.OrdersDict.STATE_RECEIVE;
import static com.lky.enums.dict.OrdersDict.STATE_SEND;

/**
 * 订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@RestController
@RequestMapping("biz/orders")
@Api(value = "biz/orders", description = "订单")
public class BOrdersController extends BaseController {

    @Inject
    private OrdersService ordersService;

    @Inject
    private OrdersMapper ordersMapper;

    @Inject
    private ShopService shopService;

    @ApiOperation(value = "订单列表", response = BOrdersListDto.class, notes = "ordersList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "state", value = "订单状态",
                    allowableValues = "wait,send,receive,over,close,return", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user,
                             @RequestParam(required = false) String id,
                             @RequestParam(required = false) String state,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);

        Specification<Orders> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(state)) {
                if ("return".equals(state)) {
                    predicates.add(cb.and(
                            cb.isTrue(root.get("returned")),
                            cb.or(
                                    cb.equal(root.get("state"), String.valueOf(STATE_SEND)),
                                    cb.equal(root.get("state"), String.valueOf(STATE_RECEIVE))
                            )));
                } else {
                    predicates.add(cb.and(cb.equal(root.get("state"), state)));
                }
            }
            if (StringUtils.isNotEmpty(id)) {
                predicates.add(cb.like(root.get("id"), "%" + id.trim() + "%"));
            }
            predicates.add(cb.equal(root.get("shopId"), shop.getId()));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));

        Page<Orders> ordersList = ordersService.findAll(spec, pageable);
        List<BOrdersListDto> ordersDtoList = ordersMapper.toBListDtoList(ordersList.getContent());
        Page<BOrdersListDto> ordersDtoPage = new PageImpl<>(ordersDtoList, pageable, ordersList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersList", ordersDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "订单详情", response = OrdersDetailDto.class, notes = "ordersDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "string")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo detail(@PathVariable String id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        Orders orders = ordersService.findById(id);
        OrdersDetailDto ordersDetailDto = ordersMapper.toDetailDto(orders);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersDetail", ordersDetailDto);
        return responseInfo;
    }

    @ApiOperation(value = "订单发货,必须是待发货的订单", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "expressId", value = "物流公司名称", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "expressOdd", value = "快递单号", paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "{id}/send")
    public ResponseInfo send(@ApiIgnore @LoginUser User user,
                             @PathVariable String id,
                             @RequestParam(required = false) Integer expressId,
                             @RequestParam(required = false) String expressOdd) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);

        Orders orders = ordersService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, orders);
        AssertUtils.isTrue(STATE_ERROR, STATE_SEND.compare(orders.getState()));
        ordersService.send(user, orders, expressId, expressOdd);

        return ResponseUtils.buildResponseInfo();
    }
}
