package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.ABankCard;
import org.springframework.stereotype.Repository;

/**
 * 代理商绑定的银行卡Dao
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-12-22
 */
@Repository
public interface ABankCardDao extends BaseDao<ABankCard, Integer> {
}
