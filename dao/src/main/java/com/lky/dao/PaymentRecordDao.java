package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.PaymentRecord;
import org.springframework.stereotype.Repository;

/**
 * 订单支付记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface PaymentRecordDao extends BaseDao<PaymentRecord, Integer> {
    PaymentRecord findByTransactionCode(String transactionCode);
}
