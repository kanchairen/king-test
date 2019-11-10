package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Area;
import org.springframework.stereotype.Repository;

/**
 * 地区
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface AreaDao extends BaseDao<Area, Integer> {

    int countByNameAndType(String name, String type);
}
