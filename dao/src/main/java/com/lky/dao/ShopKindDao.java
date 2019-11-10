package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.ShopKind;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺内分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ShopKindDao extends BaseDao<ShopKind, Integer> {
    List<ShopKind> findByShopIdOrderBySortIndexDesc(Integer shopId);
}
