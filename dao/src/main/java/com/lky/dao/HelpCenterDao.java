package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.HelpCenter;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帮助中心
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Repository
public interface HelpCenterDao extends BaseDao<HelpCenter, Integer> {
    List<HelpCenter> findByTypeOrderBySortIndexDesc(String type);
}
