package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.ApplyRecord;
import com.lky.entity.User;
import org.springframework.stereotype.Repository;

/**
 * 申请店铺记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ApplyRecordDao extends BaseDao<ApplyRecord, Integer> {
    ApplyRecord findByUser(User user);
}
