package com.lky.modules.api;

import com.lky.entity.User;
import com.lky.entity.WPointRecord;
import com.lky.enums.dict.WPointRecordDict;
import com.lky.service.UserService;
import com.lky.service.WPointRecordService;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.dict.WPointRecordDict.TYPE_MERCHANT_ONLINE_ORDERS;

/**
 * 大数据操作
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/24
 */
public class BigDataTest extends BaseTest {

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private UserService userService;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Test
    public void add() {
        List<User> userList = userService.findAll();
        long beginTime = System.currentTimeMillis();
        List<WPointRecord> list = Lists.newArrayList();
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 10000; i++) {
                WPointRecord record = new WPointRecord();
                record.setUser(userList.get((int) (Math.random() * userList.size())));
                record.setChangeWPoint(i * 100);
                record.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
                record.setType(String.valueOf(TYPE_MERCHANT_ONLINE_ORDERS));
                record.setCurrentWPoint(0);
                record.setRemark("测试");
                list.add(record);
            }
            wPointRecordService.save(list);
        }
        System.out.println(System.currentTimeMillis() - beginTime);
    }

    @Test
    public void addSql() {
        List<User> userList = userService.findAll();
        long beginTime = System.currentTimeMillis();
        List<WPointRecord> list = Lists.newArrayList();
        for (int i = 0; i < 10000; i++) {
            WPointRecord record = new WPointRecord();
            record.setUser(userList.get((int) (Math.random() * userList.size())));
            record.setChangeWPoint(i * 100);
            record.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
            record.setType(String.valueOf(TYPE_MERCHANT_ONLINE_ORDERS));
            record.setCurrentWPoint(0);
            record.setRemark("测试");
            list.add(record);
        }

        wPointRecordService.save(list);
        System.out.println(System.currentTimeMillis() - beginTime);
    }

}
