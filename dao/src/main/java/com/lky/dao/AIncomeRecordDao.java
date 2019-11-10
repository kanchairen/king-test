package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AIncomeRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 收益记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/26
 */
@Repository
public interface AIncomeRecordDao extends BaseDao<AIncomeRecord, Integer> {

    /**
     * 查询代理商按月的收益记录列表
     *
     * @param aUserId  代理商id
     * @param offset   分页起始页
     * @param pageSize 每页的条数
     * @return 收益记录列表
     */
    @Query(value = "select sum(ai.id) id, " +
            "ai.a_user_id a_user_id, ai.create_time create_time, " +
            "sum(ai.consumer_amount) consumer_amount, " +
            "sum(ai.income_sum_amount) income_sum_amount, " +
            "sum(ai.income_amount) income_amount, " +
            "sum(ai.income_wpoint) income_wpoint, " +
            "sum(ai.back_amount) back_amount " +
            "from a_income_record ai where ai.a_user_id = ?1 " +
            "group by extract(year_month from create_time) " +
            "order by create_time desc limit ?2, ?3", nativeQuery = true)
    List<AIncomeRecord> findByMonth(Integer aUserId, Integer offset, Integer pageSize);

    /**
     * 查询代理商按日的收益记录列表
     * @param aUserId 代理商id
     * @param date 时间
     * @return 收益记录列表
     */
    @Query(value = "select ai.* from a_income_record ai " +
            "where ai.a_user_id = ?1 and date(ai.create_time) = date(?2) " +
            "order by ai.create_time desc", nativeQuery = true)
    List<AIncomeRecord> findByDate(Integer aUserId, Date date);

    /**
     * 统计代理商按月的收益条数
     *
     * @param aUserId 用户
     * @return Long
     */
    @Query(value = "select count(mo.id) from " +
            "(select id from a_income_record ai where ai.a_user_id = ?1 " +
            "group by extract(year_month from create_time)) as mo", nativeQuery = true)
    long countByMonth(Integer aUserId);
}
