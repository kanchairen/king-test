package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.QrCodeRecordDao;
import com.lky.entity.QrCodeRecord;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 二维码去支付记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/1/18
 */
@Service
public class QrCodeRecordService extends BaseService<QrCodeRecord, Integer> {

    @Inject
    private QrCodeRecordDao qrCodeRecordDao;

    @Override
    public BaseDao<QrCodeRecord, Integer> getBaseDao() {
        return this.qrCodeRecordDao;
    }
}
