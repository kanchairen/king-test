package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.QrCode;
import org.springframework.stereotype.Repository;

/**
 * 活动二维码
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/17
 */
@Repository
public interface QrCodeDao extends BaseDao<QrCode, Integer> {
    QrCode findByCode(String code);
}
