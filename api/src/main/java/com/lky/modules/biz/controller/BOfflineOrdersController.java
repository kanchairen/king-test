package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.OfflineOrdersDto;
import com.lky.entity.OfflineOrders;
import com.lky.entity.Shop;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.ShopResCode.SHOP_NOT_EXIST;
import static com.lky.enums.dict.OfflineOrdersDict.STATE_PAID;
import static com.lky.enums.dict.OfflineOrdersDict.STATE_UNPAID;

/**
 * 线下订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@RestController
@RequestMapping("biz/offline/orders")
@Api(value = "biz/offline/orders", description = "线下订单")
public class BOfflineOrdersController extends BaseController {

    @Inject
    private OfflineOrdersService offlineOrdersService;

    @Inject
    private OfflineOrdersMapper offlineOrdersMapper;

    @Inject
    private ShopService shopService;

    @ApiOperation(value = "线下订单列表", response = OfflineOrdersDto.class, notes = "offlineOrdersList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "state", value = "订单状态",
                    allowableValues = "unpaid,paid", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@ApiIgnore @LoginUser User user,
                             @RequestParam(required = false) String id,
                             @RequestParam(required = false) String state,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        SimpleSpecificationBuilder<OfflineOrders> builder = new SimpleSpecificationBuilder<>();
        if (StringUtils.isNotEmpty(state)) {
            AssertUtils.isContain(PARAMS_EXCEPTION, state, STATE_UNPAID, STATE_PAID);
            builder.add("state", SpecificationOperator.Operator.eq, state);
        }
        if (StringUtils.isNotEmpty(id)) {
            builder.add("id", SpecificationOperator.Operator.likeAll, id.trim());
        }
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);
        builder.add("shop", SpecificationOperator.Operator.eq, shop);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));

        Page<OfflineOrders> offlineOrdersList = offlineOrdersService.findAll(builder.generateSpecification(), pageable);
        List<OfflineOrdersDto> offlineOrdersDtoList = offlineOrdersMapper.toDtoList(offlineOrdersList.getContent());
        Page<OfflineOrdersDto> offlineOrdersDtoPage = new PageImpl<>(offlineOrdersDtoList, pageable, offlineOrdersList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("offlineOrdersList", offlineOrdersDtoPage);
        return responseInfo;
    }
}
