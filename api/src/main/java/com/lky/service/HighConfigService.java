package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.HighConfigDao;
import com.lky.entity.HighConfig;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 高级配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/11
 */
@Service
public class HighConfigService extends BaseService<HighConfig, Integer> {

    @Inject
    private HighConfigDao highConfigDao;

    @Override
    public BaseDao<HighConfig, Integer> getBaseDao() {
        return this.highConfigDao;
    }

//    @Cacheable(value = "lky:cache:config", key = "'high_config'")
//    public HighConfig find() {
//        Optional<HighConfig> configOptional = findAll().stream().findFirst();
//        return configOptional.orElse(null);
//    }
//
//    @CachePut(value = "lky:cache:config", key = "'high_config'")
//    public HighConfig saveOrUpdate(HighConfig highConfig) {
//        if (highConfig.getId() != null) {
//            super.update(highConfig);
//        } else {
//            super.save(highConfig);
//        }
//        return highConfig;
//    }
}
