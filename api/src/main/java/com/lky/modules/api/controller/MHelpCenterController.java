package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.HelpCenterDto;
import com.lky.entity.HelpCenter;
import com.lky.mapper.HelpCenterMapper;
import com.lky.service.HelpCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.dict.HelpCenterDict.*;

/**
 * 帮助中心
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@RestController
@RequestMapping("api/help/center")
@Api(value = "api/help/center", description = "帮助中心")
public class MHelpCenterController extends BaseController {

    @Inject
    private HelpCenterService helpCenterService;

    @Inject
    private HelpCenterMapper helpCenterMapper;

    @ApiOperation(value = "帮助详情", response = HelpCenter.class, notes = "helpCenter")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        HelpCenter helpCenter = helpCenterService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("helpCenter", helpCenter);
        return responseInfo;
    }

    @ApiOperation(value = "帮助列表", response = HelpCenterDto.class, notes = "helpCenterList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "分类", required = true,
                    allowableValues = "user,merchant,operation", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam String type,
                             @RequestParam(required = false) String title) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, type);
        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, type, TYPE_USER, TYPE_MERCHANT, TYPE_OPERATION);

        SimpleSpecificationBuilder<HelpCenter> builder = new SimpleSpecificationBuilder<>();
        if (StringUtils.isNotEmpty(title)) {
            builder.add("title", SpecificationOperator.Operator.likeAll, title);
        }
        builder.add("type", SpecificationOperator.Operator.eq, type);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "sortIndex"));

        Page<HelpCenter> helpCenterPage = helpCenterService.findAll(builder.generateSpecification(), pageable);
        List<HelpCenterDto> helpCenterDtoList = helpCenterMapper.toPageDto(helpCenterPage.getContent());
        Page<HelpCenterDto> helpCenterList = new PageImpl<>(helpCenterDtoList, pageable, helpCenterPage.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("helpCenterList", helpCenterList);
        return responseInfo;
    }
}
