package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Banner;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 广告banner
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface BannerDao extends BaseDao<Banner, Integer> {
    List<Banner> findByTypeOrderBySortIndexDesc(String type);
}
