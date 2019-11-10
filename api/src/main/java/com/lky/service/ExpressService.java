package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.ExpressDao;
import com.lky.entity.Express;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 快递公司
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@Service
public class ExpressService extends BaseService<Express, Integer> {

    @Inject
    private ExpressDao expressDao;

    @Override
    public BaseDao<Express, Integer> getBaseDao() {
        return this.expressDao;
    }
}
