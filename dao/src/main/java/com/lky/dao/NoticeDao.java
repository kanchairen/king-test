package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Notice;
import org.springframework.stereotype.Repository;

/**
 * 平台公告
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface NoticeDao extends BaseDao<Notice, Integer> {
}
