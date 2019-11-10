package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.SToken;
import com.lky.enums.dict.STokenDict;
import org.springframework.stereotype.Repository;

/**
 * 用户token管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/26
 */
@Repository
public interface STokenDao extends BaseDao<SToken, Integer> {

    SToken findByUserIdAndType(Integer userId, String type);

    SToken findByTokenAndType(String token, String type);
}
