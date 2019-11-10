package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.ArithUtils;
import com.lky.commons.utils.IdWorker;
import com.lky.dao.QrCodeDao;
import com.lky.dao.QrCodeRecordDao;
import com.lky.entity.QrCode;
import com.lky.entity.QrCodeRecord;
import com.lky.entity.User;
import com.lky.utils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static com.lky.enums.dict.AnnualFeeRecordDict.STATE_UNPAID;

/**
 * 活动二维码
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/17
 */
@Service
public class QrCodeService extends BaseService<QrCode, Integer> {

    @Inject
    private QrCodeDao qrCodeDao;

    @Inject
    private QrCodeRecordDao qrCodeRecordDao;

    @Override
    public BaseDao<QrCode, Integer> getBaseDao() {
        return this.qrCodeDao;
    }

    /**
     * 添加活动二维码
     *
     * @param qrCodeDto 二维码相关信息
     * @return 二维码对象信息
     */
    public QrCode createQrCode(QrCode qrCodeDto) {
        QrCode qrCode = new QrCode();
        BeanUtils.copyPropertiesIgnoreNull(qrCodeDto, qrCode);
        qrCode.setCode(IdWorker.getOrderCode());
        if (qrCodeDto.getState() == null) {
            qrCode.setState(Boolean.TRUE);
        }
        super.save(qrCode);
        return qrCode;
    }

    public QrCode findByCode(String code) {
        return qrCodeDao.findByCode(code);
    }

    public Integer pay(User user, QrCode qrCode, Double amount) {
        QrCodeRecord qrCodeRecord = new QrCodeRecord();
        qrCodeRecord.setAmount(amount);
        qrCodeRecord.setNumber(ArithUtils.round(amount * qrCode.getRate(), 2));
        qrCodeRecord.setState(String.valueOf(STATE_UNPAID));
        qrCodeRecord.setRate(qrCode.getRate());
        qrCodeRecord.setUser(user);
        qrCodeRecord.setCalculated(qrCode.getCalculated());
        qrCodeRecord.setRedirect(qrCode.getRedirect());
        qrCodeRecord.setThreshold(qrCode.getThreshold());
        qrCodeRecord.setUrl(qrCode.getUrl());
        qrCodeRecordDao.save(qrCodeRecord);
        return qrCodeRecord.getId();
    }
}
