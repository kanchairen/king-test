package com.lky.scheduling;

import com.lky.entity.HighConfig;
import com.lky.service.BaseConfigService;
import com.lky.service.ConvertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * 余粮公社自动转入及收益G米发放任务
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/27
 */
@Component
public class SurplusGrainScheduling {

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private ConvertService convertService;

    private static final Logger log = LoggerFactory.getLogger(WpointConvertScheduling.class);

    /**
     * 每天晚上23:00，系统自动将大米转入余粮公社
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void automaticSurplusGrain() {
        HighConfig highConfig = baseConfigService.findH();
        double maxSurplusGrain = highConfig.getMaxSurplusGrain();
        if (maxSurplusGrain <= 0) {
            return;
        }
        convertService.automaticAddSurplusGrain(highConfig);
    }
    /**
     * 每天凌晨2:00，余粮公社计算发放G米收益
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void surplusGrainIncomeWPoint() {
        HighConfig highConfig = baseConfigService.findH();
        double surplusGrainRate = highConfig.getSurplusGrainRate();
        if (surplusGrainRate <= 0) {
            return;
        }
        convertService.incomeWPointBySurplusGrain(surplusGrainRate);
    }
}
