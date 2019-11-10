package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.BalanceRecord;
import org.springframework.stereotype.Repository;

/**
 * 大米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface BalanceRecordDao extends BaseDao<BalanceRecord, Integer> {
}
