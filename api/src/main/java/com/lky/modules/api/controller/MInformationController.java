package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.InformationDto;
import com.lky.entity.Information;
import com.lky.enums.dict.InformationDict;
import com.lky.mapper.InformationMapper;
import com.lky.service.InformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.dict.InformationDict.*;

/**
 * 资讯管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/8
 */
@RestController
@RequestMapping("api/information")
@Api(value = "api/information", description = "资讯管理")
public class MInformationController extends BaseController {

    @Inject
    private InformationService informationService;

    @Inject
    private InformationMapper informationMapper;

    @ApiOperation(value = "获取弹窗公告和悬停公告", response = Information.class, notes = "informationPopUp 弹窗公告, informationSuspend 悬浮公告")
    @GetMapping(value = "notice")
    public ResponseInfo getNotice() {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("informationPopUp", informationService.findByNoticeType(String.valueOf(InformationDict.TYPE_NOTICE_POPUP)));
        responseInfo.putData("informationSuspend", informationService.findByNoticeType(String.valueOf(InformationDict.TYPE_NOTICE_SUSPEND)));
        return responseInfo;
    }

    @ApiOperation(value = "资讯详情", response = Information.class, notes = "information")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        Information information = informationService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("information", information);
        return responseInfo;
    }

    @ApiOperation(value = "资讯列表", response = InformationDto.class, notes = "informationList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "分类", required = true,
                    allowableValues = "news,notice,college", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam String type,
                             @RequestParam(required = false) String title) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, type);
        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, type, TYPE_NEWS, TYPE_NOTICE, TYPE_COLLEGE);

        SimpleSpecificationBuilder<Information> builder = new SimpleSpecificationBuilder<>();
        if (StringUtils.isNotEmpty(title)) {
            builder.add("title", SpecificationOperator.Operator.likeAll, title);
        }
        builder.add("type", SpecificationOperator.Operator.eq, type);

        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.DESC, "updateTime"),
                new Sort.Order(Sort.Direction.DESC, "createTime"),
                new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = new PageRequest(pageNumber, pageSize, sort);

        Page<Information> informationPage = informationService.findAll(builder.generateSpecification(), pageable);
        List<InformationDto> informationDtoList = informationMapper.toPageDto(informationPage.getContent());
        Page<InformationDto> informationList = new PageImpl<>(informationDtoList, pageable, informationPage.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("informationList", informationList);
        return responseInfo;
    }
}
