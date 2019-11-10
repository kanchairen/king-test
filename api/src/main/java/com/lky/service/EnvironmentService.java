package com.lky.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.lky.global.constant.Constant.PROFILE_ACTIVE_DEV;
import static com.lky.global.constant.Constant.PROFILE_ACTIVE_PROD;
import static com.lky.global.constant.Constant.PROFILE_ACTIVE_TEST;

/**
 * 环境变量
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/8
 */
@Component
public class EnvironmentService {

    private static final String SUPPORT_ENV = "prod";

    private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

    @Inject
    private Environment environment;

    public boolean isProd() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            log.debug("activeProfile --- " + activeProfile);
            if (PROFILE_ACTIVE_PROD.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTest() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            log.debug("activeProfile --- " + activeProfile);
            if (PROFILE_ACTIVE_TEST.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDev() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            log.debug("activeProfile --- " + activeProfile);
            if (PROFILE_ACTIVE_DEV.equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行环境
     *
     * @return 什么环境是否执行
     */
    public boolean executeEnv() {
        switch (SUPPORT_ENV) {
            case "prod":
                return isProd();
            case "test":
                return isTest();
            case "dev":
                return isDev();
            case "dev,test":
                return isDev() || isTest();
            case "test,prod":
                return isTest() || isProd();
        }
        return false;
    }

    public String serverUrl() {
        return environment.getProperty("apk-server.url");
    }
}
