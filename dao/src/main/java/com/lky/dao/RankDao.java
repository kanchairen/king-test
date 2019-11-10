package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Rank;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 排行榜
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface RankDao extends BaseDao<Rank, Integer> {

    @Query(value = "SELECT oandf.x, SUM(oandf.y) y FROM ((SELECT s.id x, SUM(o.amount) y From m_orders o  " +
            "INNER JOIN m_shop s ON o.shop_id = s.id  \n " +
            "INNER JOIN m_shop_config c ON c.id = s.shop_config_id  " +
            "WHERE o.amount > 0  AND o.state != 'close' AND o.state != 'wait' " +
            "AND c.open_rpoint = FALSE AND c.open_wpoint = FALSE   \n" +
            "GROUP BY s.id )   UNION ALL\n" +
            "(SELECT s.id x, SUM(o.amount) y From m_offline_orders o INNER JOIN m_shop s ON o.shop_id = s.id \n" +
            "INNER JOIN m_shop_config c ON c.id = s.shop_config_id  " +
            "WHERE o.amount > 0  AND o.state = 'paid' \n" +
            "AND c.open_rpoint = FALSE AND c.open_wpoint = FALSE  " +
            "GROUP BY s.id )) AS oandf GROUP BY x ORDER BY y DESC LIMIT 300", nativeQuery = true)
    List<Object[]> merchantRank();

}
