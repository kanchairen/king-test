package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.*;
import com.lky.entity.AppVersion;
import com.lky.service.AppVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.lky.commons.code.PublicResCode.*;
import static com.lky.enums.dict.AppVersionDict.*;

/**
 * app版本管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/10
 */
@RestController
@RequestMapping("sys/version")
@Api(value = "sys/version", description = "app版本管理")
public class SVersionController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SVersionController.class);

    @Inject
    private Environment environment;

    @Inject
    private AppVersionService appVersionService;

    @ApiOperation(value = "发布版本", response = AppVersion.class, notes = "appVersion")
    @PostMapping("")
    public ResponseInfo createVersion(@RequestBody AppVersion appVersion) throws FileNotFoundException {
        String[] checkField = {"version", "updateVersion", "url", "deviceType"};
        AssertUtils.notNull(PARAMS_IS_NULL, appVersion);
        AssertUtils.notNull(PARAMS_IS_NULL, checkField,
                appVersion.getVersion(), appVersion.getUpdateVersion(), appVersion.getUrl(), appVersion.getDeviceType());
        AssertUtils.isInclude(PARAMS_EXCEPTION, appVersion.getDeviceType(), DEVICE_TYPE_ANDROID.getKey(), DEVICE_TYPE_IOS.getKey());
        appVersionService.updateVersion(appVersion);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("appVersion", appVersion);
        return responseInfo;
    }

    @ApiOperation(value = "上传apk安装包", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "updateVersion", value = "updateVersion", required = true, paramType = "query", dataType = "int"),
    })
    @PostMapping("upload")
    public ResponseInfo uploadApk(HttpServletRequest request,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestParam Integer updateVersion) {
        AssertUtils.isTrue(PARAMS_IS_NULL, !file.isEmpty());
        AssertUtils.notNull(PARAMS_IS_NULL, updateVersion);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        try {
            String fileName = "gnc_" + updateVersion + ".apk";
            if (log.isDebugEnabled()) {
                log.debug(" file name = {} ", fileName);
            }
            // 创建文件保存路径,如果文件夹不存在则创建
            String appApkPath = APP_APK_DIR.getKey();
            File filePath = new File(appApkPath);
            if (!FileUtils.isFolder(filePath)) {
                FileUtils.createFolder(filePath);
            }
            // 创建文件,如果文件不存在则创建
            File localFile = new File(appApkPath, fileName);
            if (!FileUtils.isFile(localFile)) {
                FileUtils.createFile(localFile);
            }
            byte[] bytes = file.getBytes();
            FileOutputStream fileOutputStream = new FileOutputStream(localFile);
            BufferedOutputStream stream =
                    new BufferedOutputStream(fileOutputStream);
            stream.write(bytes);
            stream.flush();
            stream.close();
            //文件下载路径
            responseInfo.putData("appApkPath", environment.getProperty("apk-server.url") + "/" + appApkPath + fileName);
        } catch (Exception e) {
            log.error("上传apk文件异常:" + e);
            ExceptionUtils.throwResponseException(SERVER_EXCEPTION);
        }
        return responseInfo;
    }

    @ApiOperation(value = "获取最新安卓和ios版本信息列表", response = AppVersion.class, notes = "appVersion", responseContainer = "List")
    @GetMapping(value = "list")
    @RequiresPermissions("app:version:manager:list")
    public ResponseInfo getVersion() {
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

        List<AppVersion> appVersionList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(appVersionListAndroid)) {
            appVersionList.add(appVersionListAndroid.get(0));
        }

        if (!CollectionUtils.isEmpty(appVersionListIos)) {
            appVersionList.add(appVersionListIos.get(0));
        }

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("appVersion", appVersionList);
        return responseInfo;
    }
}
