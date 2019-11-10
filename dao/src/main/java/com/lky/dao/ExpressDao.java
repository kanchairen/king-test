package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Express;
import org.springframework.stereotype.Repository;

/**
 * 快递公司
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ExpressDao extends BaseDao<Express, Integer> {
}
