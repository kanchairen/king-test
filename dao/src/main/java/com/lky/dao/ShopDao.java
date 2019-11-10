package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Shop;
import com.lky.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 店铺
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ShopDao extends BaseDao<Shop, Integer> {

    Shop findByUser(User user);

    @Query(value = "SELECT  ao.shop_id, COUNT(*) num  FROM (\n" +
            "(SELECT o.shop_id  FROM m_orders o WHERE o.state != 'wait' " +
            "AND o.state != 'close' AND o.create_time > ?1 ) " +
            "UNION ALL\n" +
            "(SELECT ofo.shop_id  FROM m_offline_orders ofo WHERE ofo.state = 'paid' " +
            "AND ofo.create_time > ?1  " +
            ") ) AS ao GROUP BY ao.shop_id ", nativeQuery = true)
    List<Object[]> findShopRecentOrdersNumber(Date recentTime);
}
