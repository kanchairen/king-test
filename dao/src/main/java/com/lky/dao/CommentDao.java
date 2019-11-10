package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.Comment;
import org.springframework.stereotype.Repository;

/**
 * 商品评论
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface CommentDao extends BaseDao<Comment, Integer> {
    Comment findByOrdersItemId(Integer ordersItemId);
}
