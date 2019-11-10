package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.logistics.LogisticsUtils;
import com.lky.commons.logistics.QueryResponse;
import com.lky.dao.OrdersSendDao;
import com.lky.dto.LogisticsDto;
import com.lky.entity.Express;
import com.lky.entity.OrdersSend;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 订单发货
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@Service
public class OrdersSendService extends BaseService<OrdersSend, Integer> {

    @Inject
    private OrdersSendDao ordersSendDao;

    @Override
    public BaseDao<OrdersSend, Integer> getBaseDao() {
        return this.ordersSendDao;
    }

    public OrdersSend findByOrdersId(String ordersId) {
        return ordersSendDao.findByOrdersId(ordersId);
    }

    public LogisticsDto queryLogistics(String ordersId) {
        OrdersSend ordersSend = this.findByOrdersId(ordersId);
        if (ordersSend != null) {
            Express express = ordersSend.getExpress();
            QueryResponse queryResponse = LogisticsUtils.queryOrder(express.getCode(), ordersSend.getExpressOdd());
            LogisticsDto logisticsDto = new LogisticsDto();
            logisticsDto.setExpressName(express.getName());
            logisticsDto.setExpressOdd(ordersSend.getExpressOdd());
            logisticsDto.setState("0");
            if (queryResponse.isSuccess()) {
                logisticsDto.setState(queryResponse.getState());
                logisticsDto.setTraces(queryResponse.getTraces());
            }
            return logisticsDto;
        }
        return null;
    }
}
