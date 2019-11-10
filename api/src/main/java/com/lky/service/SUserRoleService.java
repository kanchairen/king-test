package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.SUserRoleDao;
import com.lky.entity.SMenu;
import com.lky.entity.SUser;
import com.lky.entity.SUserRole;
import com.lky.global.constant.Constant;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统用户角色Service层
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@Service
public class SUserRoleService extends BaseService<SUserRole, Integer> {

    @Inject
    private SUserRoleDao sUserRoleDao;

    @Inject
    private SMenuService sMenuService;

    @Override
    public BaseDao<SUserRole, Integer> getBaseDao() {
        return sUserRoleDao;
    }

    /**
     * 根据用户查找其权限属性
     *
     * @param sUser 系统用户
     * @return 权限set
     */
    public Set<String> findPermsByUser(SUser sUser) {
        List<String> permsList;
        if (Constant.ADMIN.equals(sUser.getUsername())) {
            List<SMenu> sMenuList = sMenuService.findAll();
            permsList = sMenuList.stream().map(SMenu::getPerms).collect(Collectors.toList());
        } else {
            permsList = sUserRoleDao.selectPermsByUserId(sUser.getId());
        }

        Set<String> permSet = new HashSet<>();
        permsList.forEach(perms -> {
            if (StringUtils.isNotEmpty(perms)) {
                if (perms.contains(",")) {
                    permSet.addAll(Arrays.asList(perms.split(",")));
                } else {
                    permSet.add(perms);
                }
            }
        });
        return permSet;
    }

    public List<SUserRole> findByUserId(Integer id) {
        return sUserRoleDao.findByUserId(id);
    }

    public SUserRole findByUserIdAndRoleId(Integer userId, Integer roleId) {
        return sUserRoleDao.findByUserIdAndRoleId(userId, roleId);
    }

    public List<SUserRole> findByRoleId(Integer roleId) {
        return sUserRoleDao.findByRoleId(roleId);
    }
}
