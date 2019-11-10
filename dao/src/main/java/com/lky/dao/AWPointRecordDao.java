package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AWPointRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 代理商G米记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/26
 */
@Repository
public interface AWPointRecordDao extends BaseDao<AWPointRecord, Integer> {

    @Query(value = "select w.a_user_id as userId, w.current_wpoint as transWPoint from a_wpoint_record w right join " +
            " (select wr.a_user_id, MAX(wr.id) id from a_wpoint_record wr " +
            " where wr.create_time<?" +
            " group by wr.a_user_id) r" +
            " on w.id = r.id", nativeQuery = true)
    List<Object[]> allTransWPoint(Date date);

    @Transactional
    @Modifying
    @Query(value = "update a_user_asset set trans_wpoint = :transWPoint where id = :id", nativeQuery = true)
    void updateAgentTransWPoint(@Param("id") Integer id, @Param("transWPoint") double transWPoint);
}
