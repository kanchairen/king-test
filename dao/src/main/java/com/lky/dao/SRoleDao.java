package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统角色Dao层
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-12-13
 */
@Repository
public interface SRoleDao extends BaseDao<SRole, Integer> {

    @Query(value = "select r.* from s_user_role ur " +
            "left join s_role r on ur.s_role_id = r.id " +
            "where ur.s_user_id = ?1", nativeQuery = true)
    List<SRole> selectByUserId(Integer userId);
}
