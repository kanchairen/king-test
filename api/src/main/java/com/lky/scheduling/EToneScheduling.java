package com.lky.scheduling;

import com.lky.commons.utils.CollectionUtils;
import com.lky.dto.EToneQuery;
import com.lky.global.constant.Constant;
import com.lky.pay.etone.EToneUtils;
import com.lky.service.AWithdrawRecordService;
import com.lky.service.WithdrawRecordService;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 * 易通金服代付查询
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-20
 */
@Component
public class EToneScheduling {

    @Inject
    private RedisTemplate redisTemplate;

    @Inject
    private WithdrawRecordService withdrawRecordService;

    @Inject
    private AWithdrawRecordService aWithdrawRecordService;

    @Scheduled(cron = "0 */5 * * * ?")
    @SuppressWarnings("unchecked")
    public void etoneQuery() {
        //执行定时查询未回调的任务
        BoundSetOperations ops = redisTemplate.boundSetOps(Constant.ETONE_DAI_FU);
        Set<EToneQuery> querySet = ops.members();

        if (!CollectionUtils.isEmpty(querySet)) {
            Date date = new Date();
            ops.remove(querySet.toArray());
            Iterator<EToneQuery> iterator = querySet.iterator();

            //遍历所有任务
            while (iterator.hasNext()) {
                EToneQuery query = iterator.next();
                //到了本次查询时间，则进行查询
                if (query.getQueryTime() <= date.getTime()) {
                    Boolean queryResult = EToneUtils.daiFuQry(query.getBatchNo());
                    //查询有返回结果，则进行处理，代付成功/失败则更新记录并删除任务
                    if (queryResult != null) {
                        if (query.getBatchNo().startsWith("0")) {
                            withdrawRecordService.handleWithdraw(query.getWithdrawRecordId(), queryResult, date);
                        } else {
                            aWithdrawRecordService.handleWithdraw(query.getWithdrawRecordId(), queryResult, date);
                        }
                        iterator.remove();
                    } else {
                        //查询结果正在处理中，则查询次数减1，更新下次查询时间
                        if ((query.getQueryCount() - 1) > 0) {
                            query.setQueryCount(query.getQueryCount() - 1);
                            query.setQueryTime(date.getTime() + Constant.OTHER_CYCLE);
                        } else {
                            //查询次数用完则删除
                            iterator.remove();
                        }
                    }
                }
            }

            //未执行处理的任务再次添加到redis里
            if (!CollectionUtils.isEmpty(querySet)) {
                ops.add(querySet.toArray());
            }
        }
    }
}
