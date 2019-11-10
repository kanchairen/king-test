package com.lky.modules.api;

import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.OrdersItemDao;
import com.lky.service.OrdersItemService;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/7
 */
public class MysqlTest extends BaseTest{

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Inject
    private OrdersItemDao ordersItemDao;

    @Inject
    private OrdersItemService ordersItemService;

    @Test
    public void testSql() {
        //获取3个月前的时间
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, -3);
        Date threeMothBefore = calendar.getTime();
        List<Object[]> mapList = ordersItemDao.findGroupIdProductIdNumber(threeMothBefore);
        if (CollectionUtils.isEmpty(mapList)) {
            System.out.println("-------------------------------------");
        } else {
            Object[] params = mapList.get(5);
            Integer a =(int) params[0];
            Integer b =(int) params[1];
            System.out.println(a + "-----------------------------");
            System.out.println(b + "-----------------------------");
            System.out.println(params.length + "-------00000000000000-------------");
        }
    }
}
