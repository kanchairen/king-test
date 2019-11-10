package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.RoleDao;
import com.lky.entity.Role;
import com.lky.enums.dict.RoleDict;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * app角色
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@Service
public class RoleService extends BaseService<Role, Integer> {

    @Inject
    private RoleDao roleDao;

    @Override
    public BaseDao<Role, Integer> getBaseDao() {
        return this.roleDao;
    }

    public Role findByCode(RoleDict code) {
        return roleDao.findByCode(String.valueOf(code));
    }
}
