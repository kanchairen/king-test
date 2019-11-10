package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.BankCard;
import org.springframework.stereotype.Repository;

/**
 * 银行卡
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@Repository
public interface BankCardDao extends BaseDao<BankCard, Integer> {
}
