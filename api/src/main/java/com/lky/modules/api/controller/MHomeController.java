package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.ArithUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.ClimateCardLink;
import com.lky.service.ConvertService;
import com.lky.service.GoldMasterService;
import com.lky.service.ShopService;
import com.lky.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.lky.enums.dict.ClimateCardDict.LINK_TYPE_BUY;
import static com.lky.enums.dict.ClimateCardDict.LINK_TYPE_QUOTATIONS;

/**
 * 首页统计数据
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-13
 */
@RestController
@RequestMapping("api/home")
@Api(value = "api/home", description = "首页数据")
public class MHomeController extends BaseController {

    @Inject
    private ConvertService convertService;

    @Inject
    private ShopService shopService;

    @Inject
    private UserService userService;

    @Inject
    private GoldMasterService goldMasterService;

    @ApiOperation(value = "首页统计数据(乐康指数、商家数量、乐心使者)", response = Map.class,
            notes = "healthIndex, merchantNumber, userNumber")
    @GetMapping(value = "count")
    public ResponseInfo get() {
        //获取乐康指数、商家数量、乐心使者
        double healthIndex = ArithUtils.mul(String.valueOf(convertService.getLHealthIndex()), "100");
        long merchantNumber = shopService.count();
        long userNumber = userService.count();

        List<ClimateCardLink> linkList = goldMasterService.findAll();
        String buyLink = null;
        String quotationsLink = null;
        if (!CollectionUtils.isEmpty(linkList)) {
            for (ClimateCardLink climateCardLink : linkList) {
                if (String.valueOf(LINK_TYPE_BUY).equals(climateCardLink.getType())) {
                    buyLink = climateCardLink.getLink();
                }
                else if (String.valueOf(LINK_TYPE_QUOTATIONS).equals(climateCardLink.getType())) {
                    quotationsLink = climateCardLink.getLink();
                }
            }
        }

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("healthIndex", healthIndex);
        responseInfo.putData("merchantNumber", merchantNumber);
        responseInfo.putData("userNumber", userNumber);
        responseInfo.putData("rPointShopId", shopService.findRPointId());
        responseInfo.putData("wPointShopId", shopService.findWPointId());
        responseInfo.putData("buyLink", buyLink);
        responseInfo.putData("quotationsLink", quotationsLink);

        return responseInfo;
    }
}
