package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.AppVersion;
import com.lky.global.annotation.AuthIgnore;
import com.lky.service.AppVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.dict.AppVersionDict.DEVICE_TYPE_ANDROID;
import static com.lky.enums.dict.AppVersionDict.DEVICE_TYPE_IOS;


/**
 * app版本
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/10
 */
@RestController
@RequestMapping("api/version")
@Api(value = "api/version", description = "app版本")
public class MVersionController extends BaseController {

    @Inject
    private AppVersionService appVersionService;

    @ApiOperation(value = "获取最新版本信息", response = AppVersion.class, notes = "appVersion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceType", value = "设备类型", paramType = "query", dataType = "String",
                    allowableValues = "ios, android")
    })
    @GetMapping(value = "get")
    public ResponseInfo getVersion(@RequestParam String deviceType) {
        AssertUtils.isInclude(PARAMS_EXCEPTION, deviceType, DEVICE_TYPE_ANDROID.getKey(), DEVICE_TYPE_IOS.getKey());
        SimpleSpecificationBuilder<AppVersion> builder = new SimpleSpecificationBuilder<>();
        builder.add("deviceType", SpecificationOperator.Operator.eq, deviceType);
        Pageable pageable = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "createTime"));
        Page<AppVersion> appVersionPage = appVersionService.findAll(builder.generateSpecification(), pageable);
        List<AppVersion> appVersionList = appVersionPage.getContent();
        AppVersion appVersion = null;
        if (!CollectionUtils.isEmpty(appVersionList)) {
            appVersion = appVersionList.get(0);
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("appVersion", appVersion);
        return responseInfo;
    }

    @ApiOperation(value = "获取最新版本url", response = ResponseInfo.class, notes = "androidUrl, iosUrl")
    @AuthIgnore
    @GetMapping(value = "url")
    public ResponseInfo getVersionUrls() {
        SimpleSpecificationBuilder<AppVersion> builder = new SimpleSpecificationBuilder<>();
        Pageable pageable = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "createTime"));
        builder.add("deviceType", SpecificationOperator.Operator.eq, DEVICE_TYPE_ANDROID.getKey());
        Page<AppVersion> appVersionPage = appVersionService.findAll(builder.generateSpecification(), pageable);
        List<AppVersion> appVersionListAndroid = appVersionPage.getContent();

        SimpleSpecificationBuilder<AppVersion> builderIos = new SimpleSpecificationBuilder<>();
        Pageable pageableIos = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "createTime"));
        builderIos.add("deviceType", SpecificationOperator.Operator.eq, DEVICE_TYPE_IOS.getKey());
        Page<AppVersion> appVersionPageIos = appVersionService.findAll(builderIos.generateSpecification(), pageableIos);
        List<AppVersion> appVersionListIos = appVersionPageIos.getContent();
        String androidUrl = null;
        String iosUrl = null;
        if (!CollectionUtils.isEmpty(appVersionListAndroid)) {
            androidUrl = appVersionListAndroid.get(0).getUrl();
        }
        if (!CollectionUtils.isEmpty(appVersionListIos)) {
            iosUrl = appVersionListIos.get(0).getUrl();
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("androidUrl", androidUrl);
        responseInfo.putData("iosUrl", iosUrl);
        return responseInfo;
    }
}
