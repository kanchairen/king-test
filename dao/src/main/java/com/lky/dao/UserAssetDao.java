package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.UserAsset;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;

/**
 * app用户资产
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface UserAssetDao extends BaseDao<UserAsset, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select ua from UserAsset ua where ua.id = :id")
    UserAsset findByIdForUpdate(@Param("id") Integer id);

    @Modifying
    @Query(value = "update m_user_asset set balance = balance + ?2 where id = ?1", nativeQuery = true)
    @Transactional
    int updateBalance(Integer id, double balance);

    @Modifying
    @Query(value = "update m_user_asset set balance = balance + ?2, wpoint = wpoint + ?3, rpoint = rpoint + ?4 where id = ?1", nativeQuery = true)
    @Transactional
    void covertUserUpdate(Integer id, double balance, double wpoint, double rpoint);

    @Modifying
    @Query(value = "update m_user_asset set balance = balance + ?2, merchant_wpoint = merchant_wpoint + ?3, rpoint = rpoint + ?4 where id = ?1", nativeQuery = true)
    @Transactional
    void covertMerchantUpdate(Integer id, double balance, double merchantWPoint, double merchantRPoint);
}
