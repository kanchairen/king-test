package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.Banner;
import com.lky.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.dict.BannerDict.TYPE_HOME;
import static com.lky.enums.dict.BannerDict.TYPE_ONLINE;

/**
 * banner管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@RestController
@RequestMapping("api/banner")
@Api(value = "api/banner", description = "banner管理")
public class MBannerController extends BaseController {

    @Inject
    private BannerService bannerService;

    @ApiOperation(value = "banner详情", notes = "banner", response = Banner.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        Banner banner = bannerService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("banner", banner);
        return responseInfo;
    }

    @ApiOperation(value = "banner列表", notes = "bannerList", response = Banner.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "banner分类", required = true,
                    allowableValues = "home,online", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam String type) {

        AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_HOME, TYPE_ONLINE);

        SimpleSpecificationBuilder<Banner> builder = new SimpleSpecificationBuilder<>();
        builder.add("type", SpecificationOperator.Operator.eq, type);
        builder.add("shopId", SpecificationOperator.Operator.isNull, null);

        List<Banner> bannerList = bannerService.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "sortIndex"));

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("bannerList", bannerList);
        return responseInfo;
    }
}
