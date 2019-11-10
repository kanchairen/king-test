package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Industry;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺行业
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface IndustryDao extends BaseDao<Industry, Integer> {
    List<Industry> findByParentId(Integer parentId);
}
