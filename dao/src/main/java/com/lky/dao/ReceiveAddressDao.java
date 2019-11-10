package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.ReceiveAddress;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收货地址
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ReceiveAddressDao extends BaseDao<ReceiveAddress, Integer> {
    List<ReceiveAddress> findByUserIdOrderByFirstDesc(Integer userId);

    ReceiveAddress findByFirstAndUserId(Boolean first, Integer userId);
}
