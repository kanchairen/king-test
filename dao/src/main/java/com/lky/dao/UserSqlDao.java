package com.lky.dao;

import com.lky.commons.utils.StringUtils;
import com.lky.dto.PlatformCountDto;
import com.lky.dto.ShopSimpleDto;
import com.lky.dto.UserEpitomeDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户数据库操作sql语句
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/7
 */
@Repository
public class UserSqlDao {

    @Inject
    private NamedParameterJdbcTemplate njt;

    public List<UserEpitomeDto> findUserEpitomeList(String condition, String role, String orderStr, int pageSize, int pageNum) {

        String sql = "select u.id, u.real_name, u.mobile, u.role_type, ua.balance, ua.rpoint, ua.wpoint, ua.lock_wpoint, " +
                " ua.surplus_grain, ua.merchant_rpoint, ua.merchant_wpoint, ua.merchant_lock_wpoint " +
                " from m_user u " +
                " left join m_user_asset ua ON u.user_asset_id = ua.id " +
                " where 1 = 1 " +
                (condition == null ? "" : " AND (u.mobile LIKE '%" + condition + "%' OR u.real_name LIKE '%" + condition + "%' ) ") +
                (role == null ? "" : " AND  u.role_type = ? ") +
                " ORDER BY " + orderStr +
                " LIMIT ?,?	 ";

        List list = new ArrayList() {{
            if (StringUtils.isNotEmpty(role)) {
                add(role);
            }
            add(pageSize * pageNum);
            add(pageSize);
        }};
        return njt.getJdbcOperations().query(sql, new BeanPropertyRowMapper(UserEpitomeDto.class), list.toArray());
    }

    public Integer findUserEpitomeNumber(String condition, String role) {
        String sql = "select  count(*) " +
                " from m_user u " +
                " left join m_user_asset ua ON u.user_asset_id = ua.id " +
                " where 1 = 1 " +
                (condition == null ? "" : " AND (u.mobile LIKE '%" + condition + "%' OR u.real_name LIKE '%" + condition + "%' ) ") +
                (role == null ? "" : " AND  u.role_type = ? ");

        List list = new ArrayList() {{
            if (StringUtils.isNotEmpty(role)) {
                add(role);
            }
        }};
        return njt.getJdbcOperations().queryForObject(sql, Integer.class, list.toArray());
    }

    public PlatformCountDto getPlatformCount() {
        RowMapper<PlatformCountDto> rm = new BeanPropertyRowMapper(PlatformCountDto.class);
        String sql = " SELECT " +
                "SUM(ua.balance) as balance, SUM(ua.rpoint) as rpoint, " +
                "SUM(ua.wpoint) as wpoint, SUM(ua.lock_wpoint) as lockWPoint,  SUM(ua.surplus_grain) as surplusGrain, " +
                "SUM(ua.merchant_rpoint) as merchantRPoint,SUM(ua.merchant_wpoint) as merchantWPoint, " +
                "SUM(ua.merchant_lock_wpoint) as merchantLockWPoint" +
                " FROM m_user_asset ua ";
        return njt.getJdbcOperations().queryForObject(sql, rm);
    }

    public List<Map<String, Object>> findAutomaticSurplusGrain(double maxSurplusGrain) {
        String sql = "SELECT u.id AS id, ua.balance AS balance, ua.surplus_grain AS surplusGrain FROM m_user u " +
                " LEFT JOIN m_user_asset ua ON u.user_asset_id = ua.id " +
                " WHERE u.open_surplus_grain = 1 AND ua.balance > 0 " +
                "AND u.automatic_surplus_grain = 1 " +
                "AND ua.surplus_grain < " + maxSurplusGrain;
        return njt.getJdbcOperations().queryForList(sql);
    }

    public void updateSurplusGrain(int id, double balance, double surplusGrain) {
        String sql = "UPDATE m_user_asset SET balance =" + balance +
                " , surplus_grain = " + surplusGrain +
                " WHERE id = " + id;
        njt.getJdbcOperations().update(sql);
    }

    public List<Map<String, Object>> incomeSurplusGrain(Date beginTime) {
        String sql = " SELECT account.id AS id, account.sg - ifnull(reduce.sg,0) AS surplusGrain, account.wpoint AS wpoint FROM " +
                "(SELECT u.id AS id, ua.surplus_grain AS sg, ua.wpoint AS wpoint FROM m_user_asset ua" +
                " LEFT JOIN m_user u ON ua.id = u.user_asset_id WHERE ua.surplus_grain > 0 ) AS account " +
                " LEFT JOIN\n" +
                " (SELECT sgr.m_user_id AS id, SUM(sgr.change_surplus_grain) AS sg FROM m_surplus_grain_record sgr\n" +
                " WHERE  sgr.type = 'from_balance'\n" +
                " AND sgr.income_time >  '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beginTime) + "' " +
                " GROUP BY sgr.m_user_id) AS reduce  \n" +
                " ON account.id = reduce.id ORDER BY account.id";
        return njt.getJdbcOperations().queryForList(sql);
    }

    public void updateSGPoint(int id, double currentWPoint) {
        String sql = "UPDATE m_user_asset SET wpoint =" + currentWPoint +
                " WHERE id = " + id;
        njt.getJdbcOperations().update(sql);
    }

    public List<ShopSimpleDto> findShopListByCondition(Integer industryId, Integer id) {
        String sql = "select s.id, sc.benefit_rate, s.name, s.address, s.logo_img_id, s.lat, s.lng, s.recent_sum_order " +
                "from m_shop s \n" +
                "inner join m_shop_datum sd on s.shop_datum_id=sd.id \n" +
                "inner join m_shop_config sc on s.shop_config_id=sc.id \n" +
                "where sc.open_rpoint<>1 and sc.open_wpoint<>1 and " +
                "sd.open_shop_expire >= '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "' " +
                " and sc.open_shop=1 " +
                (industryId == null ? "" : " AND s.industry_id = ? ") +
                (id == null ? "" : " AND s.id <> ? ");

        List list = new ArrayList() {{
            if (industryId != null) {
                add(industryId);
            }
            if (id != null) {
                add(id);
            }
        }};
        return njt.getJdbcOperations().query(sql, new BeanPropertyRowMapper(ShopSimpleDto.class), list.toArray());
    }

}
