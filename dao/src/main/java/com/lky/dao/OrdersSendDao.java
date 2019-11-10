package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.OrdersSend;
import org.springframework.stereotype.Repository;

/**
 * 订单发货记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface OrdersSendDao extends BaseDao<OrdersSend, Integer> {
    OrdersSend findByOrdersId(String ordersId);
}