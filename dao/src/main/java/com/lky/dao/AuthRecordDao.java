package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AuthRecord;
import com.lky.entity.User;
import org.springframework.stereotype.Repository;

/**
 * 实名认证申请记录Dao
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-12-8
 */
@Repository
public interface AuthRecordDao extends BaseDao<AuthRecord, Integer> {

    AuthRecord findByUser(User user);
}
