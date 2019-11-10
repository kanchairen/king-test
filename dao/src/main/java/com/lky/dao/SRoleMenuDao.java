package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SRoleMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统角色菜单中间表Dao
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-12-13
 */
@Repository
public interface SRoleMenuDao extends BaseDao<SRoleMenu, Integer> {

    List<SRoleMenu> findByRoleId(Integer roleId);
}
