package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.HomeRecommend;
import com.lky.service.HomeRecommendService;
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

import static com.lky.enums.dict.HomeRecommendDict.TARGET_TYPE_PRODUCT;
import static com.lky.enums.dict.HomeRecommendDict.TARGET_TYPE_SHOP;

/**
 * 首页推荐
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/10
 */
@RestController
@RequestMapping("api/home/recommend")
@Api(value = "api/home/recommend", description = "首页推荐")
public class MHomeRecommendController extends BaseController {

    @Inject
    private HomeRecommendService homeRecommendService;

    @ApiOperation(value = "推荐列表", response = HomeRecommend.class, notes = "homeRecommendList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型", required = true,
                    allowableValues = "product,shop", paramType = "query", dataType = "string")
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam String type) {
        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, type, TARGET_TYPE_PRODUCT, TARGET_TYPE_SHOP);

        List<HomeRecommend> homeRecommendList = homeRecommendService.findByTargetType(type);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("homeRecommendList", homeRecommendList);
        return responseInfo;
    }
}
