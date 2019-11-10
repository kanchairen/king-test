package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.OrdersJob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 订单任务
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/7
 */
@Repository
public interface OrdersJobDao extends BaseDao<OrdersJob, Integer> {
    List<OrdersJob> findByType(String type);

    OrdersJob findByTypeAndOrdersId(String type, String ordersId);

    @Query(value = "select oj.* FROM m_orders_job oj where type = ?1 and orders_id IN (?2)", nativeQuery = true)
    List<OrdersJob> listByJobTypeAndIds(String type, Set<String> ordersIdSet);
}
