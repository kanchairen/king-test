package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Image;
import org.springframework.stereotype.Repository;

/**
 * 图片
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface ImageDao extends BaseDao<Image, Integer> {
}
