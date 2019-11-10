package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.ConvertRecordDao;
import com.lky.entity.ConvertRecord;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * G米转换记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/16
 */
@Service
public class ConvertRecordService extends BaseService<ConvertRecord, Integer> {

    @Inject
    private ConvertRecordDao convertRecordDao;

    @Override
    public BaseDao<ConvertRecord, Integer> getBaseDao() {
        return this.convertRecordDao;
    }
}
