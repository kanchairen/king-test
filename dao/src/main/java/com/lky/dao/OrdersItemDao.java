package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.OrdersItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 子订单项
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface OrdersItemDao extends BaseDao<OrdersItem, Integer> {
    List<OrdersItem> findByOrdersId(String ordersId);

    @Query(value = "SELECT pg.id, oi.number FROM `m_orders_item`  oi INNER JOIN m_orders o on oi.orders_id = o.id INNER JOIN m_product p on oi.product_id = p.id \n" +
            "INNER JOIN m_product_group pg on p.product_group_id = pg.id WHERE o.state != 'wait' \n" +
            "AND o.state != 'close' AND o.create_time > ?1", nativeQuery = true)
    List<Object[]> findGroupIdProductIdNumber(Date recentTime);


}
