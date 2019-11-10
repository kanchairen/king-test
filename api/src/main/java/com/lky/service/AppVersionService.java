package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.FileUtils;
import com.lky.dao.AppVersionDao;
import com.lky.entity.AppVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.dict.AppVersionDict.APP_APK_DIR;
import static com.lky.enums.dict.AppVersionDict.DEVICE_TYPE_ANDROID;

/**
 * app版本
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/10
 */
@Service
public class AppVersionService extends BaseService<AppVersion, Integer> {

    @Inject
    private AppVersionDao appVersionDao;

    @Override
    public BaseDao<AppVersion, Integer> getBaseDao() {
        return this.appVersionDao;
    }

    public void updateVersion(AppVersion appVersion) throws FileNotFoundException {
        //根据安卓或是ios的设备系统类型查找最近的记录
        SimpleSpecificationBuilder<AppVersion> builder = new SimpleSpecificationBuilder<>();
        builder.add("deviceType", SpecificationOperator.Operator.eq, appVersion.getDeviceType());
        Pageable pageable = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "createTime"));
        Page<AppVersion> appVersionPage = super.findAll(builder.generateSpecification(), pageable);
        List<AppVersion> appVersionList = appVersionPage.getContent();

        if (!CollectionUtils.isEmpty(appVersionList)) {
            AppVersion sourceVersion = appVersionList.get(0);
            //校验版本是否比之前的更高
            AssertUtils.isTrue(PARAMS_EXCEPTION, appVersion.getUpdateVersion() >= sourceVersion.getUpdateVersion());
            //如果是安卓系统则删除原apk文件
            if (DEVICE_TYPE_ANDROID.getKey().equals(appVersion.getDeviceType())) {
                //校验新apk是否存在
                String fileNewName = "gnc_" + appVersion.getUpdateVersion() + ".apk";
                File fileNew = ResourceUtils.getFile(APP_APK_DIR.getKey() + fileNewName);
                AssertUtils.isTrue(PARAMS_EXCEPTION, fileNew.exists() && fileNew.isFile());
                //删除老apk版本时，但如果更新版本号相等则不删除
                if (appVersion.getUpdateVersion() > sourceVersion.getUpdateVersion()) {
                    String fileSource = "gnc_" + sourceVersion.getUpdateVersion() + ".apk";
                    File file = ResourceUtils.getFile(APP_APK_DIR.getKey() + fileSource);
                    FileUtils.delete(file);
                }
            }
        }
        appVersion.setCreateTime(new Date());
        super.save(appVersion);
    }
}
