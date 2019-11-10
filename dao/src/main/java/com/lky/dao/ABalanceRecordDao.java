package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.ABalanceRecord;
import org.springframework.stereotype.Repository;

/**
 * 代理商大米变动记录表Dao
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-12-22
 */
@Repository
public interface ABalanceRecordDao extends BaseDao<ABalanceRecord, Integer> {
}
