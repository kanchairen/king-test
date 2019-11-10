package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ShortUrlUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.SUserDao;
import com.lky.dto.SubSUserDto;
import com.lky.entity.SRole;
import com.lky.entity.SUser;
import com.lky.entity.SUserRole;
import com.lky.enums.code.RoleResCode;
import com.lky.enums.dict.SUserDict;
import com.lky.global.constant.Constant;
import com.lky.utils.PasswordUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.commons.code.PublicResCode.SERVER_EXCEPTION;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.SmsLogDict.TYPE_OPEN_ACCOUNT;

/**
 * 系统用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/20
 */
@Service
public class SUserService extends BaseService<SUser, Integer> {

    @Inject
    private SUserDao sUserDao;

    @Inject
    private Environment environment;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private SRoleService sRoleService;

    @Inject
    private SUserRoleService sUserRoleService;

    @Override
    public BaseDao<SUser, Integer> getBaseDao() {
        return this.sUserDao;
    }

    @Override
    public SUser findById(Integer id) {
        return sUserDao.findOne(id);
    }

    @Override
    public Page<SUser> findAll(Pageable pageable) {
        return sUserDao.findAll(pageable);
    }

    public SUser findByUsername(String username) {
        return sUserDao.findByUsername(username);
    }

    public SUser findByMobile(String mobile) {
        return sUserDao.findByMobile(mobile);
    }

    @Override
    public void save(SUser sUser) {
        SUser dataSUser = this.findByMobile(sUser.getMobile());
        AssertUtils.isNull(MOBILE_EXIST, dataSUser);
        if (sUser.getState() == null) {
            sUser.setState(String.valueOf(SUserDict.STATE_ACTIVE));
        }
        sUser.setPassword(PasswordUtils.createHash(sUser.getPassword()));
        if (sUser.getId() == null) {
            super.save(sUser);
        } else {
            super.update(sUser);
        }
    }

    /**
     * 修改登录密码
     *
     * @param mobile   登录手机号
     * @param password 新密码
     * @return 登录用户id
     */
    public Integer forgetPwd(String mobile, String password) {
        SUser sUser = this.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, sUser);
        sUser.setPassword(PasswordUtils.createHash(password));
        sUser.setUpdateTime(new Date());
        super.update(sUser);
        return sUser.getId();
    }

    /**
     * 添加子账号
     *
     * @param id 超级管理员id
     * @param sUserDto 子账号dto
     * @return 子账号id
     */
    public Integer createSubSUser(Integer id, SubSUserDto sUserDto) {
        //添加子账号
        SUser addSUser = new SUser();
        addSUser.setMobile(sUserDto.getMobile());
        addSUser.setPassword(sUserDto.getPassword());
        addSUser.setUsername(sUserDto.getUsername());
        addSUser.setParentId(id);
        addSUser.setState(String.valueOf(SUserDict.STATE_LOCK));
        this.save(addSUser);
        //发送激活子账号短信
        String code = StringUtils.getNumberUUID(6);
        String url = environment.getProperty("apk-server.url") + "/sys/account/active/" + code;
        AssertUtils.isTrue(SERVER_EXCEPTION,
                smsLogService.sendActiveCode(sUserDto.getMobile(), String.valueOf(TYPE_OPEN_ACCOUNT), ShortUrlUtils.shortUrl(url), code));
        //添加子账号权限
        if (!CollectionUtils.isEmpty(sUserDto.getSroleList())) {
            for (SRole sRole : sUserDto.getSroleList()) {
                AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"sRole.id"}, sRole.getId());
                SRole dataSRole = sRoleService.findById(sRole.getId());
                AssertUtils.notNull(RoleResCode.NO_EXIST, dataSRole);
                SUserRole sUserRole = new SUserRole();
                sUserRole.setRoleId(sRole.getId());
                sUserRole.setUserId(addSUser.getId());
                sUserRoleService.save(sUserRole);
            }
        }
        return addSUser.getId();
    }

    /**
     * 删除子账号及其对应权限中间表
     *
     * @param delSUser 子账号
     */
    public void deleteSUserAndSRole(SUser delSUser) {
        List<SUserRole> sUserRoleList = sUserRoleService.findByUserId(delSUser.getId());
        if (!CollectionUtils.isEmpty(sUserRoleList)) {
            sUserRoleService.delete(sUserRoleList);
        }
        super.delete(delSUser);
    }

    /**
     * 编辑子账号
     *
     * @param sourceUser 原子账号信息
     * @param sUserDto 新子账号信息
     */
    public void edit(SUser sourceUser, SubSUserDto sUserDto) {
        if (sUserDto.getUsername() != null) {
            AssertUtils.isTrue(YSY_NAME_NOT_ALLOW, !Constant.ADMIN.equals(sUserDto.getUsername().trim())
                    && !Constant.ADMIN.equals(sUserDto.getUsername()));
            sourceUser.setUsername(sUserDto.getUsername());
        }
        if (sUserDto.getPassword() != null) {
            sourceUser.setPassword(PasswordUtils.createHash(sUserDto.getPassword()));
        }
        List<SRole> sourceRoleList = sRoleService.findByUserId(sourceUser.getId());
        List<SRole> targetRoleList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(sUserDto.getSroleList())) {
            List<SRole> addSRole = new ArrayList<>();
            for (SRole sRole : sUserDto.getSroleList()) {
                AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"sRole.id"}, sRole.getId());
                SRole dataSRole = sRoleService.findById(sRole.getId());
                AssertUtils.notNull(RoleResCode.NO_EXIST, dataSRole);
                targetRoleList.add(dataSRole);
                addSRole.add(dataSRole);
            }
            //新增权限时，用户角色中间表中的数据增加
            addSRole.removeAll(sourceRoleList);
            if (!CollectionUtils.isEmpty(addSRole)) {
                for (SRole sRole : addSRole) {
                    SUserRole sUserRole = new SUserRole();
                    sUserRole.setRoleId(sRole.getId());
                    sUserRole.setUserId(sourceUser.getId());
                    sUserRoleService.save(sUserRole);
                }
            }
        }
        if (!CollectionUtils.isEmpty(sourceRoleList)) {
            sourceRoleList.removeAll(targetRoleList);
            //减少权限时，用户角色中间表中的数据相应删除
            if (!CollectionUtils.isEmpty(sourceRoleList)) {
                for (SRole sRole : sourceRoleList) {
                    SUserRole sUserRole = sUserRoleService.findByUserIdAndRoleId(sourceUser.getId(), sRole.getId());
                    sUserRoleService.delete(sUserRole);
                }
            }
        }
        sourceUser.setUpdateTime(new Date());
        super.update(sourceUser);
    }
}
