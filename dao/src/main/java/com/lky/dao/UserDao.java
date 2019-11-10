package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * app用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface UserDao extends BaseDao <User, Integer> {

    User findByUsername(String username);

    User findByRecommendCode(String recommendCode);

    User findByMobile(String mobile);

    @Query(value = "select ua.id from m_user u LEFT JOIN  m_user_asset ua ON u.user_asset_id = ua.id " +
            " where u.locked = 0 AND ua.trans_wpoint > 0", nativeQuery = true)
    List<Integer> listUser();

    @Query(value = "select ur.user_id from m_user_role ur " +
            " left join m_role r " +
            " on ur.role_id = r.id  " +
            " where r.name = '商家' " +
            " order by ur.user_id", nativeQuery = true)
    List<Integer> listMerchant();

    User findByUserAssetId(Integer userAssetId);
}
