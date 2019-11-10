package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.HomeRecommendDto;
import com.lky.entity.HomeRecommend;
import com.lky.service.HomeRecommendService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("sys/home/recommend")
@Api(value = "sys/home/recommend", description = "首页推荐")
public class SHomeRecommendController extends BaseController {

    @Inject
    private HomeRecommendService homeRecommendService;

    @ApiOperation(value = "添加推荐", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "homeRecommendDto", value = "推荐")
                               @RequestBody HomeRecommendDto homeRecommendDto) {
        String[] checkFields = {"homeRecommendDto", "targetType", "targetId", "showImg"};
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields,
                homeRecommendDto, homeRecommendDto.getTargetType(), homeRecommendDto.getTargetId(), homeRecommendDto.getShowImg());
        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, homeRecommendDto.getTargetType(), TARGET_TYPE_PRODUCT, TARGET_TYPE_SHOP);

        homeRecommendService.create(homeRecommendDto);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改推荐", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "homeRecommendDto", value = "推荐")
                             @RequestBody HomeRecommendDto homeRecommendDto) {
        String[] checkFields = {"id", "homeRecommendDto", "targetType", "targetId", "showImg"};
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields, id,
                homeRecommendDto, homeRecommendDto.getTargetType(), homeRecommendDto.getTargetId(), homeRecommendDto.getShowImg());
        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, homeRecommendDto.getTargetType(), TARGET_TYPE_PRODUCT, TARGET_TYPE_SHOP);

        HomeRecommend homeRecommend = homeRecommendService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, homeRecommend);

        homeRecommendService.modify(homeRecommend, homeRecommendDto);

        return ResponseUtils.buildResponseInfo();
    }

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

    @ApiOperation(value = "推荐详情", response = HomeRecommend.class, notes = "homeRecommend")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        HomeRecommend homeRecommend = homeRecommendService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("homeRecommend", homeRecommend);
        return responseInfo;
    }

    @ApiOperation(value = "删除详情", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        homeRecommendService.delete(id);

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

        HomeRecommend source = homeRecommendService.findById(sourceId);
        HomeRecommend dest = homeRecommendService.findById(destId);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, source, dest);
        homeRecommendService.swapPosition(source, dest);

        return ResponseUtils.buildResponseInfo();
    }
}
