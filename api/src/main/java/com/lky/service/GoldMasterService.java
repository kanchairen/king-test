package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.ClimateCardLinkDao;
import com.lky.entity.ClimateCardLink;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 金主数字资产服务平台
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/23
 */
@Service
public class GoldMasterService extends BaseService<ClimateCardLink, Integer> {

    @Inject
    private ClimateCardLinkDao climateCardLinkDao;

    @Override
    public BaseDao<ClimateCardLink, Integer> getBaseDao() {
        return this.climateCardLinkDao;
    }
}
