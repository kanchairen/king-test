package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SUser;
import org.springframework.stereotype.Repository;

/**
 * 系统用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/19
 */
@Repository
public interface SUserDao extends BaseDao<SUser, Integer> {

    SUser findByUsername(String username);

    SUser findByMobile(String mobile);
}
