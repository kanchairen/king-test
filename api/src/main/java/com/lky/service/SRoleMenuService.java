package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.SRoleMenuDao;
import com.lky.entity.SRoleMenu;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * 系统角色菜单中间表Service
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@Service
public class SRoleMenuService extends BaseService<SRoleMenu, Integer> {

    @Inject
    private SRoleMenuDao sRoleMenuDao;

    @Override
    public BaseDao<SRoleMenu, Integer> getBaseDao() {
        return sRoleMenuDao;
    }

    public List<SRoleMenu> findByRoleId(Integer roleId) {
        return sRoleMenuDao.findByRoleId(roleId);
    }
}
