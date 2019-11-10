package com.lky.service;

import com.lky.commons.utils.CollectionUtils;
import com.lky.dto.EToneQuery;
import com.lky.entity.AWithdrawRecord;
import com.lky.entity.WithdrawRecord;
import com.lky.global.constant.Constant;
import com.lky.pay.etone.EToneUtils;
import com.lky.pay.etone.sdk.NotifyResponse;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

import static com.lky.enums.dict.WithdrawRecordDict.STATE_AGREE;
import static com.lky.enums.dict.WithdrawRecordDict.STATE_FINISH;

/**
 * 易通代付处理Service
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-23
 */
@Service
@Transactional
public class EToneService {

    @Inject
    private RedisTemplate redisTemplate;

    @Inject
    private WithdrawRecordService withdrawRecordService;

    @Inject
    private AWithdrawRecordService aWithdrawRecordService;

    /**
     * 向redis中添加代付请求记录，为后续处理易通金服未回调做准备
     *
     * @param id      提现记录id
     * @param batchNo 代付请求批次号
     */
    @SuppressWarnings("unchecked")
    public void addRedis(Integer id, String batchNo) {
        EToneQuery eToneQuery = new EToneQuery();
        eToneQuery.setWithdrawRecordId(id);
        eToneQuery.setBatchNo(batchNo);
        eToneQuery.setQueryTime(System.currentTimeMillis() + Constant.FIRST_CYCLE);
        //加入缓存，以便后续处理，调用查询接口更新状态
        redisTemplate.boundSetOps(Constant.ETONE_DAI_FU).add(eToneQuery);
    }

    /**
     * 易通提现回调处理
     *
     * @param request 回调请求
     * @return Boolean
     */
    @SuppressWarnings("unchecked")
    public Boolean etoneNotify(HttpServletRequest request) {
        NotifyResponse response = EToneUtils.acceptNotify(request);
        if (response != null) {
            String batchNo = response.getBatchNo();

            //回调成功，则删除定时任务
            BoundSetOperations ops = redisTemplate.boundSetOps(Constant.ETONE_DAI_FU);
            Set<EToneQuery> set = ops.members();
            if (!CollectionUtils.isEmpty(set)) {
                ops.remove(set.toArray());
                set.removeIf(e -> e.getBatchNo().equals(batchNo));
                if (!CollectionUtils.isEmpty(set)) {
                    ops.add(set.toArray());
                }
            }


            if (response.getBatchNo().startsWith("0")) {
                //更新个人提现记录
                WithdrawRecord withdrawRecord = withdrawRecordService.findByBatchNo(batchNo);
                if (withdrawRecord != null) {
                    if (STATE_AGREE.getKey().equals(withdrawRecord.getState())) {
                        withdrawRecord.setState(STATE_FINISH.getKey());
                        withdrawRecord.setFinishTime(new Date());
                        withdrawRecordService.update(withdrawRecord);
                        return Boolean.TRUE;
                    } else if (STATE_FINISH.getKey().equals(withdrawRecord.getState())) {
                        return Boolean.TRUE;
                    }
                }
            } else {
                //更新代理商提现记录
                AWithdrawRecord aWithdrawRecord = aWithdrawRecordService.findByBatchNo(batchNo);
                if (aWithdrawRecord != null) {
                    if (STATE_AGREE.getKey().equals(aWithdrawRecord.getState())) {
                        aWithdrawRecord.setState(STATE_FINISH.getKey());
                        aWithdrawRecord.setFinishTime(new Date());
                        aWithdrawRecordService.update(aWithdrawRecord);
                        return Boolean.TRUE;
                    } else if (STATE_FINISH.getKey().equals(aWithdrawRecord.getState())) {
                        return Boolean.TRUE;
                    }
                }
            }
        }

        return Boolean.FALSE;
    }
}
