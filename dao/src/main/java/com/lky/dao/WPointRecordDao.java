package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.WPointRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * G米变动记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/9
 */
@Repository
public interface WPointRecordDao extends BaseDao<WPointRecord, Integer> {

    @Query(value = "select w.m_user_id as userId, w.current_wpoint as transWPoint from m_wpoint_record w right join " +
            " (select wr.m_user_id, MAX(wr.id) id from m_wpoint_record wr " +
            " where wr.user_type=?" +
            " and wr.create_time<?" +
            " group by wr.m_user_id) r" +
            " on w.id = r.id", nativeQuery = true)
    List<Object[]> allTransWPoint(String userType, Date date);

    @Query(value = "select sum(w.current_wpoint as transWPoint) from m_wpoint_record w right join " +
            " (select wr.m_user_id, MAX(wr.id) id from m_wpoint_record wr " +
            " where wr.user_type=?" +
            " and wr.create_time<?" +
            " group by wr.m_user_id) r" +
            " on w.id = r.id", nativeQuery = true)
    double sumTransWPoint(String userType, Date date);

    @Transactional
    @Modifying
    @Query(value = "update m_user_asset set trans_wpoint = :transWPoint " +
            "where id = (select user_asset_id from m_user where id = :userId)", nativeQuery = true)
    void updateTransWPoint(@Param("userId") Integer userId, @Param("transWPoint") double transWPoint);

    @Transactional
    @Modifying
    @Query(value = "update m_user_asset set merchant_trans_wpoint = :merchantTransWPoint " +
            "where id = (select user_asset_id from m_user where id = :userId)", nativeQuery = true)
    void updateMerchantTransWPoint(@Param("userId") Integer userId, @Param("merchantTransWPoint") double merchantTransWPoint);
}
