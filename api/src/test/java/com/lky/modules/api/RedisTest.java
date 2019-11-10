package com.lky.modules.api;

import com.lky.commons.redis.RedisHelper;
import com.lky.commons.utils.DateUtils;
import com.lky.entity.BaseConfig;
import com.lky.entity.HighConfig;
import com.lky.entity.OrdersJob;
import com.lky.service.BaseConfigService;
import com.lky.service.HighConfigService;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * redis测试
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/10
 */
public class RedisTest extends BaseTest {

    @Inject
    private RedisHelper redisHelper;

    @Inject
    private HighConfigService highConfigService;

    @Inject
    private BaseConfigService baseConfigService;

    @Test
    public void list() {
        redisHelper.leftPush("list", 1);
        redisHelper.rightPop("list");
        redisHelper.rightPop("list");
        Integer userAssetId = (Integer) redisHelper.rightPop("list");
        System.out.println("------------" + userAssetId);
    }

    @Test
    public void add() {
        redisHelper.set("kkkk", "ddd");
        System.out.println("------------------------------------" + redisHelper.get("kkkk"));
    }

    @Test
    public void tes() {
        OrdersJob ordersJob = (OrdersJob) redisHelper.get("orders_job_close_111");
        System.out.println("ldllllllllllllll::::::::" + ordersJob);

        Set<Object> jobKeySet = (Set<Object>) redisHelper.get("lky:cache:orders:job~keys");
//        for (String s : jobKeySet) {
//            System.out.println(s);
//        }
    }

    @Test
    public void expire() {
        redisHelper.setExpire("kk", "bb", 10l);
        Date add = DateUtils.add(new Date(), Calendar.MINUTE, 1);
        redisHelper.setExpireAt("tt", "test", add);
    }

    @Test
    public void testCache() throws InterruptedException {
//        HighConfig highConfig = highConfigService.find();
//        highConfigService.saveOrUpdate(highConfig);
        for (int i = 0; i < 4; i++) {
//            HighConfig highConfig = highConfigService.find();
            BaseConfig baseConfig = baseConfigService.find();
            HighConfig h = baseConfigService.findH();
            Thread.sleep(3000);
        }
    }
}
