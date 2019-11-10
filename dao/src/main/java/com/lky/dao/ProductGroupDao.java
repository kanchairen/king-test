package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.ProductGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 商品组
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ProductGroupDao extends BaseDao<ProductGroup, Integer> {

    @Query(value = "SELECT pg.id, oi.number FROM `m_orders_item`  oi INNER JOIN m_orders o on oi.orders_id = o.id INNER JOIN m_product p on oi.product_id = p.id \n" +
            "INNER JOIN m_product_group pg on p.product_group_id = pg.id WHERE o.state != 'wait' \n" +
            "AND o.state != 'close' AND o.create_time > ?1", nativeQuery = true)
    List<Object[]> findGroupIdAndSaleNumber(Date recentTime);

    @Query(value = "SELECT  pg.id pgid, sum(oi.number) num FROM `m_orders_item`  oi " +
            "INNER JOIN m_orders o on oi.orders_id = o.id " +
            "INNER JOIN m_product p on oi.product_id = p.id \n" +
            "INNER JOIN m_product_group pg on p.product_group_id = pg.id " +
            "WHERE o.state != 'wait' AND o.state != 'close' AND o.create_time > ?1 GROUP BY pgid", nativeQuery = true)
    List<Object[]> findMonthGroupSale(Date recentTime);

    @Query(value = "SELECT oi.product_id pid, sum(oi.number) num FROM `m_orders_item`  oi " +
            "INNER JOIN m_orders o on oi.orders_id = o.id \n" +
            "WHERE o.state != 'wait' AND o.state != 'close' AND o.create_time > ?1 GROUP BY pid", nativeQuery = true)
    List<Object[]> findMonthProductSale(Date recentTime);
}
