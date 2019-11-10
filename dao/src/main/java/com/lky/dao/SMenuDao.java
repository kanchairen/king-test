package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SMenu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统菜单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/13
 */
@Repository
public interface SMenuDao extends BaseDao<SMenu, Integer> {

    @Query(value = "select m.* from s_role_menu rm " +
            "left join s_menu m on rm.s_menu_id = m.id " +
            "where rm.s_role_id = ?1", nativeQuery = true)
    List<SMenu> selectBySRoleId(Integer sRoleId);

    @Query(value = "SELECT DISTINCT m.*  FROM s_role_menu rm LEFT JOIN s_role r ON rm.s_role_id = r.id  " +
            "LEFT JOIN s_user_role ur ON r.id = ur.s_role_id LEFT JOIN s_menu m ON m.id = rm.s_menu_id " +
            "WHERE ur.s_user_id = ?1  ORDER BY m.id", nativeQuery = true)
    List<SMenu> selectBySUserId(Integer sUserId);

    SMenu findByPerms(String perms);
}
