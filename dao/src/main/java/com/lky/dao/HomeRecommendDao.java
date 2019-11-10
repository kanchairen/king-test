package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.HomeRecommend;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 首页推荐
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/10
 */
@Repository
public interface HomeRecommendDao extends BaseDao<HomeRecommend, Integer> {
    List<HomeRecommend> findByTargetTypeOrderBySortIndexAsc(String targetType);
}
