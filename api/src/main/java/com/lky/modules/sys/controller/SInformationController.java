package com.lky.modules.sys.controller;

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
import com.lky.mapper.InformationMapper;
import com.lky.service.InformationService;
import com.lky.utils.BeanUtils;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
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
@RequestMapping("sys/information")
@Api(value = "sys/information", description = "资讯管理")
public class SInformationController extends BaseController {

    @Inject
    private InformationService informationService;

    @Inject
    private InformationMapper informationMapper;

    @ApiOperation(value = "添加资讯", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "information", value = "资讯内容") @RequestBody Information information) {

        String[] checkFields = {"information", "type", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields, information,
                information.getType(), information.getTitle(), information.getContent());

        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, information.getType(),
                TYPE_NEWS, TYPE_NOTICE, TYPE_COLLEGE);
       informationService.checkPopUpAndSuspend(information, null);

        informationService.create(information);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改资讯", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "information", value = "资讯内容") @RequestBody Information information) {

        String[] checkFields = {"information", "id", "type", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields, information, id,
                information.getType(), information.getTitle(), information.getContent());

        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, information.getType(),
                TYPE_NEWS, TYPE_NOTICE, TYPE_COLLEGE);

        Information info = informationService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, new String[]{"id"}, info);

        informationService.checkPopUpAndSuspend(information, id);
        BeanUtils.copyPropertiesIgnoreNull(information, info, "createTime", "updateTime", "id", "type");
        info.setUpdateTime(new Date());
        informationService.save(info);

        return ResponseUtils.buildResponseInfo();
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
    @RequiresPermissions("information:manager:list")
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

        Pageable pageable = new PageRequest(pageNumber, pageSize);

        Page<Information> informationPage = informationService.findAll(builder.generateSpecification(), pageable);
        List<InformationDto> informationDtoList = informationMapper.toPageDto(informationPage.getContent());
        Page<InformationDto> informationList = new PageImpl<>(informationDtoList, pageable, informationPage.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("informationList", informationList);
        return responseInfo;
    }

    @ApiOperation(value = "删除资讯", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        informationService.delete(id);

        return ResponseUtils.buildResponseInfo();
    }
}
