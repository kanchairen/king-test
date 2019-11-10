package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.RechargeRecordDao;
import com.lky.entity.RechargeRecord;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 大米充值
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/7
 */
@Service
public class RechargeRecordService extends BaseService<RechargeRecord, Integer>{

    @Inject
    private RechargeRecordDao rechargeRecordDao;

    @Override
    public BaseDao<RechargeRecord, Integer> getBaseDao() {
        return this.rechargeRecordDao;
    }
}
