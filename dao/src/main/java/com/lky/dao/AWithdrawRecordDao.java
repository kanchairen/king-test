package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AWithdrawRecord;
import org.springframework.stereotype.Repository;

/**
 * 代理商提现记录Dao
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-22
 */
@Repository
public interface AWithdrawRecordDao extends BaseDao<AWithdrawRecord, Integer> {

    AWithdrawRecord findByBatchNo(String batchNo);
}
