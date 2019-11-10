package com.lky.modules.api;

import com.lky.LkyApplication;
import com.lky.dto.EToneQuery;
import com.lky.global.constant.Constant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Set;

/**
 * 易通支付
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LkyApplication.class)
public class EToneTest {

    @Inject
    private RedisTemplate redisTemplate;

    @Test
    @SuppressWarnings("unchecked")
    public void redisTest() {

        EToneQuery eToneQuery = new EToneQuery();
        eToneQuery.setWithdrawRecordId(1);
        eToneQuery.setQueryTime(System.currentTimeMillis() + 1000);
        eToneQuery.setBatchNo("789456");
        eToneQuery.setQueryCount(6);

        EToneQuery eToneQuery2 = new EToneQuery();
        eToneQuery2.setWithdrawRecordId(2);
        eToneQuery2.setQueryTime(System.currentTimeMillis() + 2000);
        eToneQuery2.setBatchNo("789321");
        eToneQuery2.setQueryCount(6);
        BoundSetOperations<String, EToneQuery> ops = redisTemplate.boundSetOps(Constant.ETONE_DAI_FU);
//        ops.add(eToneQuery, eToneQuery2);
        Set<EToneQuery> members = ops.members();
        for (EToneQuery member : members) {
            System.out.println(member);
            ops.remove(member);
            member.setQueryCount(1);
            member.setQueryTime(member.getQueryTime() + 5000);
            ops.add(member);
        }

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        Set<EToneQuery> set = ops.members();
        for (EToneQuery s : set) {
            System.out.println(s.toString());
        }
    }
}
