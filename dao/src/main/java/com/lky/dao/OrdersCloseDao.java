package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.OrdersClose;
import org.springframework.stereotype.Repository;

/**
 * 订单关闭
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@Repository
public interface OrdersCloseDao extends BaseDao<OrdersClose, Integer> {
}
