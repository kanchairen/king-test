package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.BOrdersReturnDto;
import com.lky.entity.OrdersReturn;
import com.lky.enums.dict.OrdersReturnDict;
import com.lky.mapper.OrdersReturnMapper;
import com.lky.service.OrdersReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.OrderResCode.STATE_ERROR;
import static com.lky.enums.dict.OrdersReturnDict.*;

/**
 * 订单退换货
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/4
 */
@RestController
@RequestMapping("biz/orders/return")
@Api(value = "biz/orders/return", description = "订单退换货")
public class BOrdersReturnController extends BaseController {

    @Inject
    private OrdersReturnService ordersReturnService;

    @Inject
    private OrdersReturnMapper ordersReturnMapper;

    @ApiOperation(value = "退换货详情", response = BOrdersReturnDto.class, notes = "ordersReturn")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单项号/订单号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "退款目标类型", allowableValues = "orders,ordersItem", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "detail")
    public ResponseInfo detail(@RequestParam String id,
                               @RequestParam String type) {

        AssertUtils.isInclude(PARAMS_EXCEPTION, type, "orders", "ordersItem");

        OrdersReturn ordersReturn;
        if ("ordersItem".equals(type)) {
            ordersReturn = ordersReturnService.findByOrdersItemId(Integer.parseInt(id));
            AssertUtils.notNull(PARAMS_EXCEPTION, ordersReturn);
            AssertUtils.isTrue(PARAMS_EXCEPTION, "ordersItemId", STATE_APPLY.compare(ordersReturn.getState()));
        } else {
            ordersReturn = ordersReturnService.findByOrdersId(id);
            AssertUtils.notNull(PARAMS_EXCEPTION, ordersReturn);
            AssertUtils.isTrue(PARAMS_EXCEPTION, "ordersId", STATE_APPLY.compare(ordersReturn.getState()));
        }

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("ordersReturn", ordersReturnMapper.toBDTO(ordersReturn));
        return responseInfo;
    }

    @ApiOperation(value = "退款处理", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "退款详情id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "state", value = "退款状态（申请、同意、拒绝）", required = true,
                    allowableValues = "agree,refuse", paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "{id}")
    public ResponseInfo process(@PathVariable Integer id,
                                @RequestParam String state) {

        AssertUtils.notNull(PARAMS_IS_NULL, id, state);
        AssertUtils.isContain(STATE_ERROR, state, STATE_AGREE, STATE_REFUSE);
        OrdersReturn ordersReturn = ordersReturnService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, ordersReturn);
        AssertUtils.isTrue(PARAMS_EXCEPTION, OrdersReturnDict.STATE_APPLY.compare(ordersReturn.getState()));
        ordersReturnService.process(ordersReturn, state);

        return ResponseUtils.buildResponseInfo();
    }

}
