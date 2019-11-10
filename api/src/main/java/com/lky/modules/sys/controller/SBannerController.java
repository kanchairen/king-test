package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.entity.Banner;
import com.lky.service.BannerService;
import io.swagger.annotations.*;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.dict.BannerDict.*;

/**
 * banner管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@RestController
@RequestMapping("sys/banner")
@Api(value = "sys/banner", description = "banner管理")
public class SBannerController extends BaseController {

    @Inject
    private BannerService bannerService;

    @ApiOperation(value = "添加banner", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "banner", value = "banner图片") @RequestBody Banner banner) {

        String[] checkFields = {"banner", "type", "bannerImg"};

        AssertUtils.notNull(PARAMS_IS_NULL, checkFields, banner, banner.getType(), banner.getBannerImg());
        AssertUtils.isContain(PARAMS_EXCEPTION, banner.getType(), TYPE_HOME, TYPE_ONLINE);

        if (StringUtils.isNotEmpty(banner.getLinkType())) {
            AssertUtils.isContain(PARAMS_EXCEPTION, banner.getLinkType(),
                    LINK_TYPE_PRODUCT, LINK_TYPE_SHOP, LINK_TYPE_CUSTOM_TEXT, LINK_TYPE_CUSTOM_LINK);
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"linkValue"}, banner.getLinkValue());
        }

        bannerService.create(banner);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "banner详情", response = Banner.class, notes = "banner")
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

    @ApiOperation(value = "banner列表", response = Banner.class , notes = "bannerList", responseContainer = "List")
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

    @ApiOperation(value = "修改banner", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "banner", value = "banner图片") @RequestBody Banner banner) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        String[] checkFields = {"banner", "type", "bannerImg"};

        AssertUtils.notNull(PARAMS_IS_NULL, checkFields, banner, banner.getType(), banner.getBannerImg());
        AssertUtils.isContain(PARAMS_EXCEPTION, banner.getType(), TYPE_HOME, TYPE_ONLINE);

        if (StringUtils.isNotEmpty(banner.getLinkType())) {
            AssertUtils.isContain(PARAMS_EXCEPTION, banner.getLinkType(),
                    LINK_TYPE_PRODUCT, LINK_TYPE_SHOP, LINK_TYPE_CUSTOM_TEXT, LINK_TYPE_CUSTOM_LINK);
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"linkValue"}, banner.getLinkValue());
        }

        bannerService.modify(id, banner);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "删除banner", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        bannerService.delete(id);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改排序", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceId", value = "源id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "destId", value = "目标id", required = true, paramType = "query", dataType = "int"),
    })
    @PutMapping(value = "sorted")
    public ResponseInfo sorted(@RequestParam Integer sourceId,
                               @RequestParam Integer destId) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, sourceId, destId);

        Banner source = bannerService.findById(sourceId);
        Banner dest = bannerService.findById(destId);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, source, dest);
        bannerService.swapPosition(source, dest);

        return ResponseUtils.buildResponseInfo();
    }
}
