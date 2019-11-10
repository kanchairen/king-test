package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.User;
import com.lky.entity.UserCollect;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * app用户收藏
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface UserCollectDao extends BaseDao<UserCollect, Integer> {
    UserCollect findByUserAndShopId(User user, Integer shopId);

    List<UserCollect> findByUser(User user);
}
