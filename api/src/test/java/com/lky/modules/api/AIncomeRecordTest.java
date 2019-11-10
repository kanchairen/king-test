package com.lky.modules.api;

import com.lky.entity.AIncomeRecord;
import com.lky.scheduling.AgentScheduling;
import com.lky.service.AIncomeRecordService;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.inject.Inject;

/**
 * 代理商收益记录测试类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-27
 */
public class AIncomeRecordTest extends BaseTest{

    @Inject
    private AIncomeRecordService aIncomeRecordService;

    @Inject
    private AgentScheduling agentScheduling;

    @Test
    public void test() {
        Pageable pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "createTime"));
        Page<AIncomeRecord> month = aIncomeRecordService.findByAUser("month", 1, pageable);
        System.out.println(month.getTotalElements());
        System.out.println(month.getTotalPages());
    }

    @Test
    public void incomeTest() {
        agentScheduling.income();
    }
}
