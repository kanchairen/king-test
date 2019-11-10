package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SmsLog;
import org.springframework.stereotype.Repository;

/**
 * 系统日志
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface SmsLogDao extends BaseDao<SmsLog, Integer> {
}
