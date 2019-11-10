package com.lky.scheduling;

import com.lky.commons.utils.CollectionUtils;
import com.lky.entity.Orders;
import com.lky.entity.OrdersJob;
import com.lky.enums.dict.OrdersJobDict;
import com.lky.service.OrdersJobService;
import com.lky.service.OrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * 订单处理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
@Component
public class OrdersScheduling {

    private static final Logger log = LoggerFactory.getLogger(OrdersScheduling.class);

    @Inject
    private OrdersJobService ordersJobService;

    @Inject
    private OrdersService ordersService;

    /**
     * 将超时未支付的订单，进行取消
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void close() {
        List<OrdersJob> ordersJobList = ordersJobService.findByType(OrdersJobDict.TYPE_CLOSE);
        if (!CollectionUtils.isEmpty(ordersJobList)) {
            for (OrdersJob ordersJob : ordersJobList) {
                if (ordersJob == null || ordersJob.getExecuteTime().getTime() > System.currentTimeMillis()) {
                    continue;
                }
                Orders orders = ordersService.findById(ordersJob.getOrdersId());
                if (orders != null) {
                    ordersService.closeOverTime(orders);
                }
                ordersJobService.del(ordersJob);
            }
        }
    }

    /**
     * 发货后，逾期未进行确认收货，将执行自动确认收货!
     */
    @Scheduled(cron = "20 */2 * * * ?")
    public void receive() {
        List<OrdersJob> ordersJobList = ordersJobService.findByType(OrdersJobDict.TYPE_RECEIVE);
        if (!CollectionUtils.isEmpty(ordersJobList)) {
            for (OrdersJob ordersJob : ordersJobList) {
                if (ordersJob == null || ordersJob.getExecuteTime().getTime() > System.currentTimeMillis()) {
                    continue;
                }
                Orders orders = ordersService.findById(ordersJob.getOrdersId());
                if (orders != null) {
                    ordersService.receive(orders);
                }
                ordersJobService.del(ordersJob);
            }
        }
    }

}
