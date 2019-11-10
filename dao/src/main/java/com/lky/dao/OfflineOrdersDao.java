package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.OfflineOrders;
import org.springframework.stereotype.Repository;

/**
 * 线下订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/30
 */
@Repository
public interface OfflineOrdersDao extends BaseDao<OfflineOrders, String> {
}
