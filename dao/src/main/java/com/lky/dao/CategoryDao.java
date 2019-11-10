package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface CategoryDao extends BaseDao<Category, Integer> {
    List<Category> findByParentId(Integer parentId);

    List<Category> findByLevel(Integer level);
}
