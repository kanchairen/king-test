package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SUserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统用户角色Dao层
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@Repository
public interface SUserRoleDao extends BaseDao<SUserRole, Integer> {

    @Query(value = "select m.perms from s_user_role ur " +
            "left join s_role_menu rm on ur.s_role_id = rm.s_role_id " +
            "left join s_menu m on rm.s_menu_id = m.id " +
            "where ur.s_user_id = ?1", nativeQuery = true)
    List<String> selectPermsByUserId(Integer userId);

    List<SUserRole> findByUserId(Integer id);

    SUserRole findByUserIdAndRoleId(Integer userId, Integer roleId);

    List<SUserRole> findByRoleId(Integer roleId);
}
