package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.ApplyRecordDto;
import com.lky.entity.ApplyRecord;
import com.lky.entity.HighConfig;
import com.lky.entity.User;
import com.lky.enums.dict.AnnualFeeRecordDict;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.ApplyRecordMapper;
import com.lky.service.AnnualFeeRecordService;
import com.lky.service.ApplyRecordService;
import com.lky.service.BaseConfigService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.MerchantResCode.EXIST_APPLY_RECORD;
import static com.lky.enums.code.MerchantResCode.INDUSTRY_SECOND_NOT_EXIST;
import static com.lky.enums.dict.ApplyRecordDict.STATE_REFUSE;

/**
 * 商家
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@RestController
@RequestMapping("api/merchant")
@Api(value = "api/merchant", description = "商家")
public class MMerchantController extends BaseController {

    @Inject
    private ApplyRecordService applyRecordService;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private ApplyRecordMapper applyRecordMapper;

    @Inject
    private AnnualFeeRecordService annualFeeRecordService;

    @ApiOperation(value = "申请", notes = "第一次申请插入一条记录，未支付状态修改记录", response = ApplyRecordDto.class)
    @PostMapping("apply")
    public synchronized ResponseInfo apply(@ApiIgnore @LoginUser User user,
                                           @ApiParam(name = "ApplyRecordDto", value = "申请记录dto")
                                           @RequestBody ApplyRecordDto applyRecordDto) {

        String[] applyRecordFiled = {"shopName", "shopAddress", "shopContactPhone", "shopLogoImg", "shopBannerImgList", "shopLicenseImgList"};

        AssertUtils.notNull(PARAMS_IS_NULL, applyRecordDto, applyRecordDto.getLat(), applyRecordDto.getLng());
        AssertUtils.notNull(PARAMS_IS_NULL, applyRecordFiled,
                applyRecordDto.getShopName(), applyRecordDto.getShopAddress(), applyRecordDto.getShopContactPhone(),
                applyRecordDto.getShopLogoImg(), applyRecordDto.getShopBannerImgList(), applyRecordDto.getShopLicenseImgList()
        );

        AssertUtils.notNull(INDUSTRY_SECOND_NOT_EXIST, applyRecordDto.getIndustryParentDto(), applyRecordDto.getIndustryParentDto().getId());
        if (applyRecordDto.getId() == null) {
            ApplyRecord record = applyRecordService.findByUser(user);
            AssertUtils.isNull(EXIST_APPLY_RECORD, record);
        }

        //需要支付的金额
        double amount = 0;
        HighConfig highConfig = baseConfigService.findH();
        //开通普通店铺的费用
        if (highConfig != null) {
            amount = highConfig.getOpenShopFee();
        }

        applyRecordDto.setUserId(user.getId());

        ApplyRecord applyRecord = applyRecordService.apply(applyRecordDto, amount);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("applyRecord", applyRecordMapper.toDto(applyRecord));
        return responseInfo;
    }

    @ApiOperation(value = "重新申请", notes = "支付状态重新申请，如果需补差价，状态更新为未支付，否则状态为申请中", response = ApplyRecordDto.class)
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    @PutMapping("again/apply/{id}")
    public ResponseInfo againApply(@PathVariable Integer id,
                                   @ApiParam(name = "ApplyRecordDto", value = "申请记录dto")
                                   @RequestBody ApplyRecordDto applyRecordDto) {

        String[] applyRecordFiled = {"industry", "shopName", "shopAddress", "shopContactPhone", "shopLogoImg", "shopBannerImgList", "shopLicenseImgList"};

        AssertUtils.notNull(PARAMS_IS_NULL, id, applyRecordDto, applyRecordDto.getLat(), applyRecordDto.getLng());
        AssertUtils.notNull(PARAMS_IS_NULL, applyRecordFiled,
                applyRecordDto.getIndustryParentDto(), applyRecordDto.getShopName(), applyRecordDto.getShopAddress(), applyRecordDto.getShopContactPhone(),
                applyRecordDto.getShopLogoImg(), applyRecordDto.getShopBannerImgList(), applyRecordDto.getShopLicenseImgList()
        );
        ApplyRecord applyRecord = applyRecordService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, applyRecord);
        AssertUtils.isTrue(PARAMS_EXCEPTION, STATE_REFUSE.compare(applyRecord.getState()));

        double sumAmount = 0; //需要支付的金额
        HighConfig highConfig = baseConfigService.findH();
        if (highConfig != null) { //开通普通店铺的费用
            sumAmount = highConfig.getOpenShopFee();
        }

        //总共需要支付的金额减去已支付的总价等于差价
        double amount = sumAmount - applyRecord.getSumPaidAmount();

        applyRecordDto.setId(id);
        applyRecord = applyRecordService.againApply(applyRecord, applyRecordDto, amount);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("applyRecord", applyRecordMapper.toDto(applyRecord));
        return responseInfo;
    }

    @ApiOperation(value = "申请详情", response = ApplyRecordDto.class)
    @GetMapping(value = "")
    public ResponseInfo get(@ApiIgnore @LoginUser User user) {

        ApplyRecord applyRecord = applyRecordService.findByUser(user);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("applyRecord", applyRecordMapper.toDto(applyRecord));
        return responseInfo;
    }

    @ApiOperation(value = "续交年费", response = ResponseInfo.class, notes = "annualFeeId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "续交类型，普通店铺", allowableValues = "shop",
                    required = true, paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "number", value = "续交年数", defaultValue = "1", paramType = "form", dataType = "int"),
    })
    @PostMapping(value = "annual/fee")
    public ResponseInfo annualFee(@ApiIgnore @LoginUser User user,
                                  @RequestParam String type,
                                  @RequestParam(required = false, defaultValue = "1") Integer number) {
        AssertUtils.isContain(PARAMS_EXCEPTION, type, AnnualFeeRecordDict.TYPE_SHOP);

        Integer annualFeeId = annualFeeRecordService.create(user, type, number);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("annualFeeId", annualFeeId);
        return responseInfo;
    }
}
