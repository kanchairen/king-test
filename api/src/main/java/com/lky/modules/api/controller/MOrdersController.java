package com.lky.modules.api.controller;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.ArithUtils;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.*;
import com.lky.entity.Orders;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.enums.dict.OrdersDict;
import com.lky.global.annotation.LoginUser;
import com.lky.global.annotation.MerchantSign;
import com.lky.mapper.OrdersMapper;
import com.lky.service.ComputeService;
import com.lky.service.OrdersService;
import com.lky.service.ShopService;
import com.lky.utils.PasswordUtils;
import io.swagger.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.OrderResCode.STATE_ERROR;
import static com.lky.enums.code.ShopResCode.SHOP_NOT_EXIST;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.OrdersDict.*;
import static com.lky.enums.dict.OrdersReturnDict.*;

/**
 * 订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@RestController
@RequestMapping("api/orders")
@Api(value = "api/orders", description = "订单")
public class MOrdersController extends BaseController{

    @Inject
    private OrdersService ordersService;

    @Inject
    private OrdersMapper ordersMapper;

    @Inject
    private ShopService shopService;

    @ApiOperation(value = "确认订单详情", response = OrdersConfirmListVo.class, notes = "ordersConfirmListVo, rpointRate")
    @PostMapping("confirm/detail")
    public ResponseInfo confirmDetail(@ApiIgnore @LoginUser User user,
                                      @ApiParam @RequestBody OrdersConfirmListDto ordersConfirmListDto) {

        AssertUtils.notNull(PARAMS_IS_NULL, ordersConfirmListDto);
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"ordersConfirmDtos"}, ordersConfirmListDto.getOrdersConfirmDtos());

        OrdersConfirmListVo ordersConfirmListVo = ordersService.confirmDetail(user, ordersConfirmListDto);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersConfirmListVo", ordersConfirmListVo);
        responseInfo.putData("rpointRate", ComputeService.R_POINT_RATE);
        return responseInfo;
    }

    @ApiOperation(value = "创建订单", response = String.class, notes = "ordersIds")
    @PostMapping("create")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @ApiParam @RequestBody OrdersConfirmListDto ordersConfirmListDto) {

        AssertUtils.notNull(PARAMS_IS_NULL, ordersConfirmListDto);
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"ordersConfirmDtos"}, ordersConfirmListDto.getOrdersConfirmDtos());

        String ordersIds = ordersService.create(user, ordersConfirmListDto);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersIds", ordersIds);
        return responseInfo;
    }

    @ApiOperation(value = "订单列表", response = OrdersListDto.class, notes = "ordersList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "订单状态",
                    allowableValues = "wait,send,receive,over,close,comment,return", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user,
                             @RequestParam(required = false) String state,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        Specification<Orders> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(state)) {
                if ("comment".equals(state)) {
                    predicates.add(cb.and(
                            cb.isFalse(root.get("comment")),
                            cb.equal(root.get("state"), String.valueOf(STATE_OVER)
                            )));
                } else if ("return".equals(state)) {
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
            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.isFalse(root.get("deleted")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));

        Page<Orders> ordersList = ordersService.findAll(spec, pageable);
        List<OrdersListDto> ordersDtoList = ordersMapper.toListDtoList(ordersList.getContent());
        //app端订单列表中显示的获得G米数只显示一件商品应得数，不乘以数量
        for (OrdersListDto ordersDto : ordersDtoList) {
            for (OrdersItemDto ordersItemDto : ordersDto.getOrdersItemDtoList()) {
                ordersItemDto.setGiveWPoint(
                        ArithUtils.round(ArithUtils.div(ordersItemDto.getGiveWPoint(), ordersItemDto.getNumber()), 2));
            }
        }
        Page<OrdersListDto> ordersDtoPage = new PageImpl<>(ordersDtoList, pageable, ordersList.getTotalElements());

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

        //app端订单详情中显示的获得G米数只显示一件商品应得数，不乘以数量
        for (OrdersItemDto ordersItemDto : ordersDetailDto.getOrdersItemDtoList()) {
            ordersItemDto.setGiveWPoint(
                    ArithUtils.round(ArithUtils.div(ordersItemDto.getGiveWPoint(), ordersItemDto.getNumber()), 2));
        }

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersDetail", ordersDetailDto);
        return responseInfo;
    }

    @ApiOperation(value = "订单取消，必须是待付款的订单", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "string")
    })
    @PutMapping(value = "{id}/close")
    public ResponseInfo close(@ApiIgnore @LoginUser User user,
                              @PathVariable String id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        Orders orders = ordersService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, orders);
        AssertUtils.isTrue(STATE_ERROR, STATE_WAIT.compare(orders.getState()));
        ordersService.close(orders);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "订单删除，必须是已完成或者已关闭的订单", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "string")
    })
    @PutMapping(value = "{id}/del")
    public ResponseInfo del(@ApiIgnore @LoginUser User user,
                            @PathVariable String id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        Orders orders = ordersService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, orders);
        AssertUtils.isContain(STATE_ERROR, orders.getState(), STATE_OVER, STATE_CLOSE);
        ordersService.del(user, orders);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "订单确认收货,必须是待收货的订单", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "{id}/receive")
    public ResponseInfo receive(@ApiIgnore @LoginUser User user,
                                @PathVariable String id,
                                @RequestParam String payPwd) {

        AssertUtils.notNull(PARAMS_IS_NULL, id, payPwd);
        AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));

        Orders orders = ordersService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, orders);
        AssertUtils.isTrue(STATE_ERROR, STATE_RECEIVE.compare(orders.getState()));
        ordersService.receive(orders);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "订单申请售后", response = ResponseInfo.class)
    @PutMapping(value = "return")
    public ResponseInfo ordersReturn(@ApiIgnore @LoginUser User user,
                                     @ApiParam @RequestBody OrdersReturnDto ordersReturnDto) {
        String[] checkField = {"orderId", "returnType", "returnReason", "returnPrice"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkField,
                ordersReturnDto.getOrdersId(), ordersReturnDto.getReturnType(), ordersReturnDto.getReturnReason());
        AssertUtils.isContain(PARAMS_EXCEPTION, ordersReturnDto.getReturnType(),
                RETURN_TYPE_REFUND, RETURN_TYPE_EXCHANGE, RETURN_TYPE_ALL);

        Orders orders = ordersService.findById(ordersReturnDto.getOrdersId());
        AssertUtils.notNull(PARAMS_EXCEPTION, orders);
        AssertUtils.isContain(STATE_ERROR, orders.getState(), STATE_RECEIVE, STATE_SEND);
        ordersService.ordersReturn(user, orders, ordersReturnDto);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "订单个数", response = Map.class, notes = "waitCount, sendCount, receiveCount")
    @GetMapping(value = "count")
    public ResponseInfo count(@ApiIgnore @LoginUser User user) {

        Specification<Orders> waitSpec = this.buildSpec(user, String.valueOf(OrdersDict.STATE_WAIT));
        Specification<Orders> sendSpec = this.buildSpec(user, String.valueOf(OrdersDict.STATE_SEND));
        Specification<Orders> receiveSpec = this.buildSpec(user, String.valueOf(OrdersDict.STATE_RECEIVE));

        long waitCount = ordersService.count(waitSpec);
        long sendCount = ordersService.count(sendSpec);
        long receiveCount = ordersService.count(receiveSpec);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("waitCount", waitCount);
        responseInfo.putData("sendCount", sendCount);
        responseInfo.putData("receiveCount", receiveCount);
        return responseInfo;
    }

    private Specification<Orders> buildSpec(User user, String state) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(state)) {
                predicates.add(cb.and(cb.equal(root.get("state"), state)));
            }
            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.isFalse(root.get("deleted")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    @ApiOperation(value = "店铺订单列表", response = OrdersListDto.class, notes = "ordersList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "订单状态",
                    allowableValues = "wait,send,receive,over,close", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list/shop")
    @MerchantSign
    public ResponseInfo listByShop(@ApiIgnore @LoginUser User user,
                                   @RequestParam(required = false) String state,
                                   @RequestParam(defaultValue = "0") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);

        Specification<Orders> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(state)) {
                predicates.add(cb.and(cb.equal(root.get("state"), state)));
            }
            predicates.add(cb.equal(root.get("shopId"), shop.getId()));
            predicates.add(cb.isFalse(root.get("deleted")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));

        Page<Orders> ordersList = ordersService.findAll(spec, pageable);
        List<OrdersListDto> ordersDtoList = ordersMapper.toListDtoList(ordersList.getContent());
        Page<OrdersListDto> ordersDtoPage = new PageImpl<>(ordersDtoList, pageable, ordersList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersList", ordersDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "店铺订单发货,必须是待发货的订单", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id", required = true, paramType = "path", dataType = "string"),
            @ApiImplicitParam(name = "expressId", value = "物流公司名称", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "expressOdd", value = "快递单号", paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "{id}/send/shop")
    @MerchantSign
    public ResponseInfo sendByShop(@ApiIgnore @LoginUser User user,
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
