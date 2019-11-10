package com.lky.modules.api;

import com.lky.entity.OrdersJob;
import com.lky.enums.dict.OrdersJobDict;
import com.lky.service.OrdersJobService;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Date;

/**
 * 订单任务测试
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/7
 */
public class OrdersJobTest extends BaseTest {

    @Inject
    private OrdersJobService ordersJobService;

    @Test
    public void save() {
        OrdersJob ordersJob = new OrdersJob();
        ordersJob.setOrdersId("111");
        ordersJob.setType(String.valueOf(OrdersJobDict.TYPE_CLOSE));
        ordersJob.setExecuteTime(new Date());
        ordersJobService.save(ordersJob);
    }

}
