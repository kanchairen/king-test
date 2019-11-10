package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.OrdersReturn;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单退款记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface OrdersReturnDao extends BaseDao<OrdersReturn, Integer> {
    List<OrdersReturn> findByOrdersId(String ordersId);
}
