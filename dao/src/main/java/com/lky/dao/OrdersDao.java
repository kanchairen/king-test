package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Orders;
import com.lky.entity.User;
import org.springframework.stereotype.Repository;

/**
 * 订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface OrdersDao extends BaseDao<Orders, String> {
    Orders findByIdAndUser(String id, User user);
}
