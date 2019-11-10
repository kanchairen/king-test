package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.IndustryDto;
import com.lky.entity.Industry;
import com.lky.mapper.IndustryMapper;
import com.lky.service.IndustryService;
import io.swagger.annotations.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.ShopResCode.INDUSTRY_NAME_EXIST;
import static com.lky.enums.code.ShopResCode.INDUSTRY_USED;

/**
 * 行业类型
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@RestController
@RequestMapping("sys/industry")
@Api(value = "sys/industry", description = "行业类型")
public class SIndustryController extends BaseController {

    @Inject
    private IndustryService industryService;

    @Inject
    private IndustryMapper industryMapper;

    @ApiOperation(value = "创建行业", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "industryDto", value = "行业dto")
                               @RequestBody IndustryDto industryDto) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, industryDto, industryDto.getName());
        industryService.create(industryDto);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "行业详情", response = IndustryDto.class, notes = "industryDto")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("industryDto", industryMapper.toDto(industryService.findById(id)));
        return responseInfo;
    }

    @ApiOperation(value = "行业列表", response = IndustryDto.class, notes = "arrayList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父id", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "行业名称", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@RequestParam(required = false) Integer parentId,
                             @RequestParam(required = false) String name) {

        SimpleSpecificationBuilder<Industry> builder = new SimpleSpecificationBuilder<>();
        if (StringUtils.isNotEmpty(name)) {
            builder.add("name", SpecificationOperator.Operator.likeAll, name.trim());
        }
        if (parentId != null) {
            builder.add("parentId", SpecificationOperator.Operator.eq, parentId);
        } else {
            builder.add("parentId", SpecificationOperator.Operator.isNull, null);
        }

        List<Industry> industryList = industryService.findAll(builder.generateSpecification());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putBeanDataAll(industryList);
        return responseInfo;
    }

    @ApiOperation(value = "修改行业", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int"),
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "industryDto", value = "行业dto")
                             @RequestBody IndustryDto industryDto) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, industryDto);
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, new String[]{"id", "name"},
                id, industryDto.getName());

        Industry industry = industryService.findById(id);
        if (!industry.getName().equals(industryDto.getName())) {
            AssertUtils.isTrue(INDUSTRY_NAME_EXIST, industryService.countByLevelAndName(industry.getLevel(), industryDto.getName()) == 0);
        }
        industry.setName(industryDto.getName());
        industry.setIcon(industryDto.getIcon());
        industryService.save(industry);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "删除行业", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        Industry industry = industryService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, industry);
        AssertUtils.isTrue(INDUSTRY_USED, !industryService.checkUsed(industry));

        industryService.delete(id);

        return ResponseUtils.buildResponseInfo();
    }
}
