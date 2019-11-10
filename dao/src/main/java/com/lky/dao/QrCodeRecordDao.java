package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.QrCodeRecord;
import org.springframework.stereotype.Repository;

/**
 * 二维码
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/1/18
 */
@Repository
public interface QrCodeRecordDao extends BaseDao<QrCodeRecord, Integer> {
}
