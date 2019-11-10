package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.UserMessage;
import org.springframework.stereotype.Repository;

/**
 * app用户消息通知
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface UserMessageDao extends BaseDao<UserMessage, Integer> {
}
