package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ProductDao extends BaseDao<Product, Integer> {
    List<Product> findByProductGroupId(Integer productGroupId);
}
