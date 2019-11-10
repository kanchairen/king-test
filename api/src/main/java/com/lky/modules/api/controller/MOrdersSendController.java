package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.LogisticsDto;
import com.lky.entity.Express;
import com.lky.global.annotation.MerchantSign;
import com.lky.service.ExpressService;
import com.lky.service.OrdersSendService;
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

/**
 * 订单发货
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/28
 */
@RestController
@RequestMapping("api/orders/send")
@Api(value = "api/orders/send", description = "订单发货")
public class MOrdersSendController extends BaseController {

    @Inject
    private OrdersSendService ordersSendService;

    @Inject
    private ExpressService expressService;

    @ApiOperation(value = "查询物流跟踪信息", response = LogisticsDto.class, notes = "logistics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ordersId", value = "订单号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("query/logistics")
    public ResponseInfo queryLogistics(@RequestParam String ordersId) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, ordersId);
        LogisticsDto logisticsDto = ordersSendService.queryLogistics(ordersId);

        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("logistics", logisticsDto);
        return responseInfo;
    }

    @ApiOperation(value = "快递公司列表", response = Express.class)
    @GetMapping(value = "express/list")
    @MerchantSign
    public ResponseInfo expressList() {

        List<Express> expressList = expressService.findAll();

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("expressList", expressList);
        return responseInfo;
    }
}
