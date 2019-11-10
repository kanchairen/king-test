package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.OrdersCloseDao;
import com.lky.entity.OrdersClose;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 订单关闭
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/3
 */
@Service
public class OrdersCloseService extends BaseService<OrdersClose, Integer> {

    @Inject
    private OrdersCloseDao ordersCloseDao;

    @Override
    public BaseDao<OrdersClose, Integer> getBaseDao() {
        return this.ordersCloseDao;
    }
}
