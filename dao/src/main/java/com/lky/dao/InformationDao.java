package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Information;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资讯管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/8
 */
@Repository
public interface InformationDao extends BaseDao<Information, Integer> {
    List<Information> findByType(String type);
}
