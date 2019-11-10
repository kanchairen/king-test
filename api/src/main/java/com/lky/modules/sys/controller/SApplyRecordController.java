package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.ApplyRecordDto;
import com.lky.entity.ApplyRecord;
import com.lky.entity.Shop;
import com.lky.enums.dict.ApplyRecordDict;
import com.lky.mapper.ApplyRecordMapper;
import com.lky.service.ApplyRecordService;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.dict.ApplyRecordDict.*;

/**
 * 店铺申请记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/17
 */
@RestController
@RequestMapping("sys/applyRecord")
@Api(value = "sys/applyRecord", description = "店铺申请记录")
public class SApplyRecordController extends BaseController {

    @Inject
    private ApplyRecordService applyRecordService;

    @Inject
    private ApplyRecordMapper applyRecordMapper;

    @Inject
    private ShopService shopService;

    @ApiOperation(value = "店铺申请记录列表", response = ApplyRecordDto.class , notes = "applyRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "店铺名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "state", value = "审核状态", allowableValues = "apply,agree,refuse,close",
                    paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "list")
    @RequiresPermissions("merchant:manager:list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String state) {

        Pageable pageable = new PageRequest(pageNumber, pageSize);

        SimpleSpecificationBuilder<ApplyRecord> builder = new SimpleSpecificationBuilder<>();
        if (StringUtils.isNotEmpty(name)) {
            builder.add("shopName", SpecificationOperator.Operator.likeAll, name.trim());
        }
        if (!StringUtils.isEmpty(state)) {
            builder.add("state", SpecificationOperator.Operator.eq, state);
        } else {
            builder.add("state", SpecificationOperator.Operator.ne, STATE_UNPAID.getKey());
        }

        Page<ApplyRecord> applyRecordList = applyRecordService.findAll(builder.generateSpecification(), pageable);
        List<ApplyRecordDto> applyRecordDtoList = applyRecordMapper.toPageDto(applyRecordList.getContent());
        Page<ApplyRecordDto> applyRecordDtoPage = new PageImpl<>(applyRecordDtoList, pageable, applyRecordList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("applyRecordList", applyRecordDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "店铺申请记录审核", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审核记录id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "auditRemark", value = "审核备注", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "state", value = "审核状态", required = true, allowableValues = "agree,refuse",
                    paramType = "form", dataType = "string"),
    })
    @PostMapping("audit/{id}")
    public ResponseInfo audit(@PathVariable Integer id,
                              @RequestParam(required = false) String auditRemark,
                              @RequestParam String state) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"id", "state"}, id, state);
        ApplyRecord applyRecord = applyRecordService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, applyRecord);
        AssertUtils.isTrue(PARAMS_EXCEPTION, STATE_APPLY.compare(applyRecord.getState()));
        AssertUtils.isContain(PARAMS_EXCEPTION, state, STATE_AGREE, STATE_REFUSE);

        applyRecord.setState(state);
        applyRecord.setAuditRemark(auditRemark);

        applyRecordService.audit(applyRecord);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "店铺解封/封停", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "店铺id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "openShop", value = "true解封/false封停", required = true, paramType = "query", dataType = "boolean"),
    })
    @PutMapping("status/{id}")
    public ResponseInfo shopOperation(@PathVariable Integer id,
                                      @RequestParam Boolean openShop) {
        Shop shop = shopService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, shop);
        ApplyRecord applyRecord = applyRecordService.findByUser(shop.getUser());
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, applyRecord);
        if (openShop) {
            applyRecord.setState(ApplyRecordDict.STATE_AGREE.getKey());
        } else {
            applyRecord.setState(ApplyRecordDict.STATE_CLOSED.getKey());
        }
        shop.getShopConfig().setOpenShop(openShop);
        applyRecordService.update(applyRecord);
        shopService.update(shop);
        return ResponseUtils.buildResponseInfo();
    }

}
