package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * app角色
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface RoleDao extends BaseDao<Role, Integer> {

    Role findByCode(String code);
}
