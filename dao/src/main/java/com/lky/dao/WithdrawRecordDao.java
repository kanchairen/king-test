package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.WithdrawRecord;
import org.springframework.stereotype.Repository;

/**
 * 提现记录
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-15
 */
@Repository
public interface WithdrawRecordDao extends BaseDao<WithdrawRecord, Integer> {

    WithdrawRecord findByBatchNo(String batchNo);
}
