package com.lky.scheduling;

import com.lky.commons.redis.RedisHelper;
import com.lky.entity.HighConfig;
import com.lky.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import static com.lky.global.constant.Constant.L_HEATH_INDEX;

/**
 * G米定时转换发放
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/15
 */
@Component
public class WpointConvertScheduling {

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private RedisHelper redisHelper;

    @Inject
    private ComputeService computeService;

    @Inject
    private OrdersService ordersService;

    @Inject
    private OfflineOrdersService offlineOrdersService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private AWPointRecordService awPointRecordService;

    @Inject
    private ConvertService convertService;

    @Inject
    private ExecutorService executorService;

    private static final Logger log = LoggerFactory.getLogger(WpointConvertScheduling.class);

    /**
     * 每天凌晨12：30计算可激励G米
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void computeTransWPoint() {
        wPointRecordService.init();
        awPointRecordService.init();
    }

    /**
     * 每天凌晨12：15计算乐康指数
     */
    @Scheduled(cron = "0 15 0 * * ?")
    public void computeLHeathIndex() {
        //获取高级配置中的乐康指数
        HighConfig highConfig = baseConfigService.findH();
        Double lHealthIndex = highConfig.getLhealthIndex();
        //手动设置的乐康指数
        if (lHealthIndex == null) {
            //前一天所有商家的现金成交额
            double sumMerchantOrdersPrice = ordersService.sumMerchantOrdersPrice()
                    + offlineOrdersService.sumMerchantOfflineOrdersPrice();

            //累计到前一天所有G米
            double sumTransWPoint = wPointRecordService.sumTransWPoint();

            lHealthIndex = computeService.lHealthIndex(sumMerchantOrdersPrice, sumTransWPoint);
        } else {
            lHealthIndex = lHealthIndex / 100;
        }

        log.info("Get new lHealthIndex : {}", lHealthIndex);
        redisHelper.set(L_HEATH_INDEX, lHealthIndex);
    }

    @Scheduled(cron = "0 15 1 * * ?")
    public void convertAUser() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("dadf");
            }
        }, new Date());

        convertService.executeAUser();
    }

    @Scheduled(fixedRate = 10)
    public void convertMerchant() {
        executorService.execute(() -> convertService.executeMerchant());
    }

    /**
     * 每隔30秒检查是否转换，
     * 如果满足条件，即刻转换
     */
    @Scheduled(fixedRate = 10)
    public void convertMUser() {
        executorService.execute(() -> convertService.executeMUser());
    }
}
