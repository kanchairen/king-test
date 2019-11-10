package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SurplusGrainRecord;
import org.springframework.stereotype.Repository;

/**
 * 余粮公社变动记录
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/27
 */
@Repository
public interface SurplusGrainDao extends BaseDao<SurplusGrainRecord, Integer> {
}
