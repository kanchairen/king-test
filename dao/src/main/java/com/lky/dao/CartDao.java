package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Cart;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 购物车
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface CartDao extends BaseDao<Cart, Integer> {
    Cart findByUserIdAndProductId(Integer userId, Integer productId);

    List<Cart> findByUserIdOrderByIdDesc(Integer userId);
}
