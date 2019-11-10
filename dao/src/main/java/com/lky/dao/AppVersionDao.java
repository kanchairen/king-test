package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AppVersion;
import org.springframework.stereotype.Repository;

/**
 * app版本
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface AppVersionDao extends BaseDao<AppVersion, Integer> {
}
