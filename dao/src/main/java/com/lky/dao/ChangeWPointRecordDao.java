package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.ChangeWPointRecord;
import org.springframework.stereotype.Repository;

/**
 * 赠送/扣减G米记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/1/8
 */
@Repository
public interface ChangeWPointRecordDao extends BaseDao<ChangeWPointRecord, Integer> {
}
