package com.lky.modules.sys.controller;

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
import com.lky.utils.BeanUtils;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
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
@RequestMapping("sys/help/center")
@Api(value = "sys/help/center", description = "帮助中心")
public class SHelpCenterController extends BaseController {

    @Inject
    private HelpCenterService helpCenterService;

    @Inject
    private HelpCenterMapper helpCenterMapper;

    @ApiOperation(value = "添加帮助", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "helpCenter", value = "帮助中心") @RequestBody HelpCenter helpCenter) {

        String[] checkFields = {"helpCenter", "type", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields, helpCenter,
                helpCenter.getType(), helpCenter.getTitle(), helpCenter.getContent());

        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, helpCenter.getType(),
                TYPE_USER, TYPE_MERCHANT, TYPE_OPERATION);

        helpCenterService.create(helpCenter);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改帮助", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "helpCenter", value = "帮助中心") @RequestBody HelpCenter helpCenter) {

        String[] checkFields = {"helpCenter", "type", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields, helpCenter,
                helpCenter.getType(), helpCenter.getTitle(), helpCenter.getContent());

        AssertUtils.isContain(PublicResCode.PARAMS_EXCEPTION, helpCenter.getType(),
                TYPE_USER, TYPE_MERCHANT, TYPE_OPERATION);

        HelpCenter help = helpCenterService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, new String[]{"id"}, help);

        BeanUtils.copyPropertiesIgnoreNull(helpCenter, help, "createTime", "updateTime", "id", "type");

        help.setUpdateTime(new Date());
        helpCenterService.save(help);

        return ResponseUtils.buildResponseInfo();
    }

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
    @RequiresPermissions("help:center:list")
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

    @ApiOperation(value = "删除帮助", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        helpCenterService.delete(id);

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

        HelpCenter sourceHelpCenter = helpCenterService.findById(sourceId);
        HelpCenter destHelpCenter = helpCenterService.findById(destId);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, sourceHelpCenter, destHelpCenter);
        helpCenterService.swapPosition(sourceHelpCenter, destHelpCenter);

        return ResponseUtils.buildResponseInfo();
    }
}
