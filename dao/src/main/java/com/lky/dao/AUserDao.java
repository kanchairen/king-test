package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AUser;
import org.springframework.stereotype.Repository;

/**
 * 代理商
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/21
 */
@Repository
public interface AUserDao extends BaseDao<AUser, Integer> {
    AUser findByMobile(String mobile);
}
