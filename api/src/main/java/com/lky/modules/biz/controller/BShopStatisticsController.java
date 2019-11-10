package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.DetailRecordDto;
import com.lky.dto.ShopCountRowDto;
import com.lky.dto.ShopSituationDto;
import com.lky.entity.*;
import com.lky.enums.code.ShopResCode;
import com.lky.enums.dict.BalanceRecordDict;
import com.lky.enums.dict.OrdersDict;
import com.lky.enums.dict.RPointRecordDict;
import com.lky.global.annotation.LoginUser;
import com.lky.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.MerchantResCode.NO_MERCHANT;
import static com.lky.enums.dict.OrdersDict.STATE_CLOSE;
import static com.lky.enums.dict.OrdersDict.STATE_OVER;


/**
 * 店铺数据统计
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/6
 */
@RestController
@RequestMapping(value = "biz/count")
@Api(value = "biz/count", description = "店铺数据统计")
public class BShopStatisticsController extends BaseController {

    @Inject
    private OrdersItemService ordersItemService;

    @Inject
    private ShopService shopService;

    @Inject
    private OrdersService ordersService;

    @Inject
    private MyWealthService myWealthService;

    @Inject
    private BaseConfigService baseConfigService;


    @ApiOperation(value = "店铺概况", response = ShopSituationDto.class, notes = "ShopSituation")
    @GetMapping(value = "shopSituation")
    public ResponseInfo shopSituation(@ApiIgnore @LoginUser User user) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(NO_MERCHANT, shop);
        ShopDatum shopDatum = shop.getShopDatum();
        UserAsset userAsset = user.getUserAsset();
        ShopSituationDto situationDto = new ShopSituationDto();

        HighConfig highConfig = baseConfigService.findH();
        situationDto.setOpenShopFee(highConfig.getOpenShopFee());

        situationDto.setBalance(userAsset.getBalance());
        situationDto.setOpenShopExpire(shopDatum.getOpenShopExpire());
        situationDto.setCashDeposit(userAsset.getCashDeposit());
        situationDto.setMerchantRPoint(userAsset.getMerchantRPoint());
        //获取店铺的订单数据
        SimpleSpecificationBuilder<Orders> builder = new SimpleSpecificationBuilder<>();
        builder.add("shopId", SpecificationOperator.Operator.eq, shop.getId());
        builder.add("state", SpecificationOperator.Operator.ne, STATE_OVER.getKey());
        builder.add("state", SpecificationOperator.Operator.ne, STATE_CLOSE.getKey());
        List<Orders> ordersList = ordersService.findAll(builder.generateSpecification());
        int waitOrders = 0;
        int sendOrders = 0;
        int receiveOrders = 0;
        int todayOrders = 0;
        if (!CollectionUtils.isEmpty(ordersList)) {
            //获取今日零点零分零秒
            Date zero = DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR);
            for (Orders orders : ordersList) {
                if (orders.getCreateTime().after(zero)) {
                    todayOrders += 1;
                }
                switch (OrdersDict.getEnum(orders.getState())) {
                    case STATE_WAIT:
                        waitOrders += 1;
                        break;
                    case STATE_SEND:
                        sendOrders += 1;
                        break;
                    case STATE_RECEIVE:
                        receiveOrders += 1;
                        break;
                    default:
                }
            }
        }
        situationDto.setWaitOrders(waitOrders);
        situationDto.setSendOrders(sendOrders);
        situationDto.setReceiveOrders(receiveOrders);
        situationDto.setTodayOrders(todayOrders);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("ShopSituation", situationDto);
        return responseInfo;
    }

    @ApiOperation(value = "大米明细", response = DetailRecordDto.class, notes = "balanceRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "账单类型", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "endTime", value = "截止时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "balance")
    public ResponseInfo getBalance(@ApiIgnore @LoginUser User user,
                                   @RequestParam Long beginTime,
                                   @RequestParam Long endTime,
                                   @RequestParam(required = false) String type,
                                   @RequestParam(defaultValue = "0") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(NO_MERCHANT, shop);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("balanceRecordList", myWealthService.findBalanceRecordList(user, null, pageable,
                type, beginTime, endTime));
        return responseInfo;
    }

    @ApiOperation(value = "获取大米类型", response = ResponseInfo.class, notes = "balanceType", responseContainer = "Map")
    @GetMapping(value = "balanceType")
    public ResponseInfo getBalanceType() {
        Map<String, String> balanceType = BalanceRecordDict.elements;
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("balanceType", balanceType);
        return responseInfo;
    }

    @ApiOperation(value = "小米明细", response = DetailRecordDto.class, notes = "rPointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "账单类型", paramType = "query", dataType = "String",
                    allowableValues = "merchant_convert, merchant_offline_orders, merchant_online_orders，merchant_convert_balance"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "endTime", value = "截止时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "rPoint")
    public ResponseInfo getRPoint(@ApiIgnore @LoginUser User user,
                                  @RequestParam Long beginTime,
                                  @RequestParam Long endTime,
                                  @RequestParam(required = false) String type,
                                  @RequestParam(defaultValue = "0") Integer pageNumber,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(NO_MERCHANT, shop);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("rPointRecordList", myWealthService.findRPointRecordList(user, null, pageable,
                RPointRecordDict.USER_TYPE_MERCHANT.getKey(), type, beginTime, endTime));
        return responseInfo;
    }

    @ApiOperation(value = "统计某段时间内的销售情况", response = ShopCountRowDto.class, notes = "sales", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "endTime", value = "截止时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "countType", value = "统计类型", required = true, allowableValues = "number, amount", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "groupType", value = "汇总类型", required = true, allowableValues = "month, day, category", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "desc", value = "是否降序", paramType = "query", dataType = "Boolean"),
    })
    @GetMapping(value = "sales")
    public ResponseInfo countSale(@ApiIgnore @LoginUser User user,
                                  @RequestParam Long beginTime,
                                  @RequestParam Long endTime,
                                  @RequestParam String countType,
                                  @RequestParam String groupType,
                                  @RequestParam(required = false) Boolean desc) {
        AssertUtils.notNull(PARAMS_EXCEPTION, beginTime, endTime);
        List<ShopCountRowDto> mapList = ordersItemService.countSalesByShopId(
                user, new Date(beginTime), new Date(endTime), countType, groupType, desc);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sales", mapList);
        return responseInfo;
    }


    @ApiOperation(value = "生成统计报表", response = ResponseInfo.class , notes = "fileName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "endTime", value = "截止时间", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "countType", value = "统计类型", required = true, allowableValues = "number, amount", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "groupType", value = "汇总类型", required = true, allowableValues = "month, day, category", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "desc", value = "是否降序", paramType = "query", dataType = "Boolean"),
    })
    @GetMapping(value = "sales/report")
    public ResponseInfo saleReport(@ApiIgnore @LoginUser User user,
                                   @RequestParam Long beginTime,
                                   @RequestParam Long endTime,
                                   @RequestParam String countType,
                                   @RequestParam String groupType,
                                   @RequestParam(required = false) Boolean desc) {
        AssertUtils.notNull(PARAMS_EXCEPTION, beginTime, endTime);
        String fileName = ordersItemService.buildSalesReport(
                user, new Date(beginTime), new Date(endTime), countType, groupType, desc);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("fileName", fileName);
        return responseInfo;
    }

    @ApiOperation(value = "小米转换成大米", response = ResponseInfo.class)
    @ApiImplicitParams({
    })
    @PutMapping(value = "rPoint/convert/balance")
    public ResponseInfo rPointConvertBalance(@ApiIgnore @LoginUser User user) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(ShopResCode.SHOP_NOT_EXIST, shop);

        myWealthService.rPointConvertBalance(shop, user);

        return ResponseUtils.buildResponseInfo();
    }
}
