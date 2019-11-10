package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.CustomText;
import org.springframework.stereotype.Repository;

/**
 * 自定义文本
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface CustomTextDao extends BaseDao<CustomText, Integer> {
}
