package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.*;
import com.lky.dto.HighConfigDto;
import com.lky.entity.HighConfig;
import com.lky.service.BaseConfigService;
import com.lky.service.ConvertService;
import com.lky.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.AssetResCode.*;
import static com.lky.enums.code.SetResCode.*;
import static com.lky.enums.dict.HighConfigDict.*;

/**
 * 高级配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/11
 */
@RestController
@RequestMapping("sys/high/config")
@Api(value = "sys/high/config", description = "高级配置")
public class SHighConfigController extends BaseController {

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private ConvertService convertService;

    @ApiOperation(value = "高级配置详情", response = HighConfig.class, notes = "highConfig, currentLhealthIndex乐康指数")
    @GetMapping(value = "")
    @RequiresPermissions("high:config:get")
    public ResponseInfo get() {

        HighConfig highConfig = baseConfigService.findH();

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("highConfig", convertService.toDto(highConfig));
        responseInfo.putData("currentLhealthIndex", ArithUtils.mul(String.valueOf(convertService.getLHealthIndex()), "100"));
        return responseInfo;
    }

    @ApiOperation(value = "保存高级配置", response = HighConfig.class, notes = "highConfig")
    @PutMapping(value = "")
    public ResponseInfo save(@ApiParam(name = "highConfig", value = "高级配置")
                             @RequestBody HighConfigDto highConfigDto) {

        AssertUtils.notNull(PARAMS_IS_NULL, highConfigDto);

        HighConfig highConfig = baseConfigService.findH();
        if (highConfig == null) {
            highConfig = new HighConfig();
        }

        //校验消费带出比
        if (highConfigDto.getUnlockWPointRate() != null) {
            Double unlockWPointRate = highConfigDto.getUnlockWPointRate();
            AssertUtils.isTrue(UNLOCK_VALUE_ERROR, unlockWPointRate >= 0 && unlockWPointRate <= 1000000);
        }

        BeanUtils.copyPropertiesIgnoreNull(highConfigDto, highConfig, "id", "lhealthIndex",
                "firstSharing", "secondSharing", "merchantSharing", "remindMobileMap");

        //不传不处理，除true时，设置手动乐康指数，传false，为计算乐康指数
        if (highConfigDto.getManualSetLHealthIndex() != null) {
            if (highConfigDto.getManualSetLHealthIndex()) {
                AssertUtils.notNull(PARAMS_IS_NULL, highConfigDto.getLhealthIndex());
                highConfig.setLhealthIndex(highConfigDto.getLhealthIndex());
            } else {
                highConfig.setLhealthIndex(null);
            }
        }

        //处理收益分成参数设置
        if (highConfig.getId() == null) {
            AssertUtils.notNull(PARAMS_EXCEPTION, highConfigDto.getFirstSharing(),
                    highConfigDto.getSecondSharing(), highConfigDto.getMerchantSharing());
            Map<String, Double> sharingMap = new HashMap<>();
            sharingMap.put(SHARING_FIRST.getKey(), highConfigDto.getFirstSharing());
            sharingMap.put(SHARING_SECOND.getKey(), highConfigDto.getSecondSharing());
            sharingMap.put(SHARING_MERCHANT.getKey(), highConfigDto.getMerchantSharing());
            highConfig.setMemberRights(JsonUtils.objectToJson(sharingMap));
        } else {
            Map<String, Double> memberRights;
            if (StringUtils.isNotEmpty(highConfig.getMemberRights())) {
                memberRights = JsonUtils.jsonToMap(highConfig.getMemberRights(), String.class, Double.class);
            } else {
                memberRights = new HashMap<>();
            }

            if (memberRights != null) {
                if (highConfigDto.getFirstSharing() != null) {
                    memberRights.put(SHARING_FIRST.getKey(), highConfigDto.getFirstSharing());
                }

                if (highConfigDto.getSecondSharing() != null) {
                    memberRights.put(SHARING_SECOND.getKey(), highConfigDto.getSecondSharing());
                }

                if (highConfigDto.getMerchantSharing() != null) {
                    memberRights.put(SHARING_MERCHANT.getKey(), highConfigDto.getMerchantSharing());
                }

                highConfig.setMemberRights(JsonUtils.objectToJson(memberRights));
            }

        }

        Map<String, Object> remindMobileMap = highConfigDto.getRemindMobileMap();
        if (!CollectionUtils.isEmpty(remindMobileMap)) {
            //校验键值对中键值为手机号
            AssertUtils.isMobile(PARAMS_EXCEPTION, remindMobileMap.keySet());
            highConfig.setRemindMobile(JsonUtils.objectToJson(remindMobileMap));
        }

        if (highConfigDto.getLockWPointRate() != null && highConfigDto.getLockWPointRate() > 0) {
            Double lockWPointRate = highConfigDto.getLockWPointRate();
            //冻结所有用户和商家的G米
            convertService.lockWPoint(lockWPointRate);
        }

        //余粮公社设置每个用户最多可存入余粮公社
        Double maxSurplusGrain = highConfigDto.getMaxSurplusGrain();
        if (maxSurplusGrain != null) {
            AssertUtils.isTrue(MAX_SURPLUS_GRAIN_ILLEGAL, maxSurplusGrain > 0 && maxSurplusGrain <= 1000000);
            highConfig.setMaxSurplusGrain(maxSurplusGrain);
        }
        //存入余粮公社几天后开始计算收益
        Integer startDay = highConfigDto.getSurplusGrainStartDay();
        if (startDay != null) {
            AssertUtils.isTrue(SURPLUS_GRAIN_START_DAY_ILLEGAL, startDay > 0 && startDay <= 1000);
            highConfig.setSurplusGrainStartDay(startDay);
        }
        //余粮公社中的1元每天可收益G米数量
        Double surplusGrainRate = highConfigDto.getSurplusGrainRate();
        if (surplusGrainRate != null) {
            AssertUtils.isTrue(SURPLUS_GRAIN_RATE_ILLEGAL, surplusGrainRate > 0 && surplusGrainRate <= 1000000);
            highConfig.setSurplusGrainRate(surplusGrainRate);
        }

        //推荐奖励参数效验
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"registerWPoint", "registerParentWPoint", "beMerchantWPointRate",
                        "beMerchantParentWPoint", "beMerchantParentBalance"}, highConfig.getRegisterWPoint(),
                highConfig.getRegisterParentWPoint(), highConfig.getBeMerchantWPointRate(),
                highConfig.getBeMerchantParentWPoint(), highConfig.getBeMerchantParentBalance());
        AssertUtils.isTrue(REGISTER_WPOINT_ERROR, highConfig.getRegisterWPoint() >= 0 && highConfig.getRegisterWPoint() <= 1000000);
        AssertUtils.isTrue(REGISTER_PARENT_WPOINT_ERROR, highConfig.getRegisterParentWPoint() >= 0 && highConfig.getRegisterParentWPoint() <= 1000000);
        AssertUtils.isTrue(BE_MERCHANT_WPOINT_RATE_ERROR, highConfig.getBeMerchantWPointRate() >= 0 && highConfig.getBeMerchantWPointRate() <= 1000000);
        AssertUtils.isTrue(BE_MERCHANT_PARENT_WPOINT_ERROR, highConfig.getBeMerchantParentWPoint() >= 0 && highConfig.getBeMerchantParentWPoint() <= 1000000);
        AssertUtils.isTrue(BE_MERCHANT_PARENT_BALANCE_ERROR, highConfig.getBeMerchantParentBalance() >= 0 && highConfig.getBeMerchantParentBalance() <= 1000000);

        highConfig = baseConfigService.saveOrUpdateH(highConfig);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("highConfig", highConfig);
        return responseInfo;
    }

    @ApiOperation(value = "立即将G米转换成大米和小米", response = ResponseInfo.class)
    @PutMapping(value = "transfer")
    public ResponseInfo immediateTransfer() {

//        //校验高级配置中G米为手动转化
//        HighConfig highConfig = baseConfigService.findH();
//        highConfig.setWpointAutoConvert(Boolean.FALSE);
//        baseConfigService.saveOrUpdateH(highConfig);
//        if (convertService.checkManualConvert(highConfig, "muser")) {
//            convertService.executeMUser(TYPE_MANUAL);
//        }
//        if (convertService.checkAutoConvert(highConfig, "merchant")) {
//            convertService.executeMerchant(TYPE_MANUAL);
//        }
//        if (convertService.checkManualConvert(highConfig, "auser")) {
//            convertService.executeAUser(TYPE_MANUAL);
//        }

        return ResponseInfo.buildSuccessResponseInfo();
    }
}


