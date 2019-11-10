package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.OrdersReceiveDao;
import com.lky.entity.OrdersReceive;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 订单确认收货
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@Service
public class OrdersReceiveService extends BaseService<OrdersReceive, Integer> {

    @Inject
    private OrdersReceiveDao ordersReceiveDao;

    @Override
    public BaseDao<OrdersReceive, Integer> getBaseDao() {
        return this.ordersReceiveDao;
    }
}
