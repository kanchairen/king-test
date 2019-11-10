package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.HomeIndustry;
import org.springframework.stereotype.Repository;

/**
 * 线下店铺首页推荐的店铺行业类型
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Repository
public interface HomeIndustryDao extends BaseDao<HomeIndustry, Integer> {
}
