package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.CustomTextDto;
import com.lky.entity.CustomText;
import com.lky.mapper.CustomTextMapper;
import com.lky.service.CustomTextService;
import com.lky.utils.BeanUtils;
import io.swagger.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * 自定义文本
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/8
 */
@RestController
@RequestMapping("sys/custom/text")
@Api(value = "sys/custom/text", description = "自定义文本")
public class SCustomTextController extends BaseController {

    @Inject
    private CustomTextService customTextService;

    @Inject
    private CustomTextMapper customTextMapper;

    @ApiOperation(value = "添加自定义页面", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "customText", value = "自定义文本") @RequestBody CustomText customText) {

        String[] checkFields = {"customText", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields,
                customText, customText.getTitle(), customText.getContent());

        customTextService.create(customText);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "自定义页面列表", response = CustomTextDto.class, notes = "customTextList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));

        Page<CustomText> customTextPage = customTextService.findAll(pageable);
        List<CustomTextDto> customTextDtoList = customTextMapper.toPageDto(customTextPage.getContent());
        Page<CustomTextDto> customTextList = new PageImpl<>(customTextDtoList, pageable, customTextPage.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("customTextList", customTextList);
        return responseInfo;
    }

    @ApiOperation(value = "自定义页面详情", response = CustomTextDto.class, notes = "customText")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        CustomText customText = customTextService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("customText", customText);
        return responseInfo;
    }

    @ApiOperation(value = "修改自定义页面", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "customText", value = "自定义文本") @RequestBody CustomText customText) {

        String[] checkFields = {"customText", "id", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields,
                customText, id, customText.getTitle(), customText.getContent());

        CustomText text = customTextService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, new String[]{"id"}, text);

        BeanUtils.copyPropertiesIgnoreNull(customText, text, "createTime", "updateTime", "id");

        text.setUpdateTime(new Date());
        customTextService.save(text);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "删除自定义页面", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        customTextService.delete(id);

        return ResponseUtils.buildResponseInfo();
    }
}
