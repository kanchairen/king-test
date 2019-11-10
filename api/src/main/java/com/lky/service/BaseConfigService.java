package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.redis.RedisHelper;
import com.lky.dao.BaseConfigDao;
import com.lky.dao.HighConfigDao;
import com.lky.entity.BaseConfig;
import com.lky.entity.HighConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

/**
 * 基础配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/20
 */
@Service
public class BaseConfigService extends BaseService<BaseConfig, Integer> {

    @Inject
    private BaseConfigDao baseConfigDao;

    @Inject
    private HighConfigDao highConfigDao;

    @Inject
    private RedisHelper redisHelper;

    @Override
    public BaseDao<BaseConfig, Integer> getBaseDao() {
        return this.baseConfigDao;
    }

    @Cacheable(value = "lky:cache:config", key = "'base_config'")
    public BaseConfig find() {
        Optional<BaseConfig> configOptional = findAll().stream().findFirst();
        return configOptional.orElse(null);
    }

    @CachePut(value = "lky:cache:config", key = "'base_config'")
    public BaseConfig saveOrUpdate(BaseConfig baseConfig) {
        if (baseConfig.getId() != null) {
            super.update(baseConfig);
        } else {
            super.save(baseConfig);
        }
        return baseConfig;
    }

    @Cacheable(value = "lky:cache:config", key = "'high_config'")
    public HighConfig findH() {
        HighConfig high_config = (HighConfig) redisHelper.get("high_config");
        return high_config == null ? highConfigDao.findOne(1) : high_config;
    }

    @CachePut(value = "lky:cache:config", key = "'high_config'")
    public HighConfig saveOrUpdateH(HighConfig highConfig) {
        if (highConfig.getId() != null) {
            highConfigDao.saveAndFlush(highConfig);
        } else {
            highConfigDao.save(highConfig);
        }
        return highConfig;
    }
}
