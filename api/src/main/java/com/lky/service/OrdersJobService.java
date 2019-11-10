package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.redis.RedisHelper;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.OrdersJobDao;
import com.lky.entity.OrdersJob;
import com.lky.enums.dict.OrdersJobDict;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * 订单定时任务
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/7
 */
@Service
public class OrdersJobService extends BaseService<OrdersJob, Integer> {

    @Inject
    private Environment environment;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private OrdersJobDao ordersJobDao;

    @Inject
    private RedisHelper redisHelper;

    @Inject
    private RedisTemplate redisTemplate;

    @Override
    public BaseDao<OrdersJob, Integer> getBaseDao() {
        return this.ordersJobDao;
    }

    public List<OrdersJob> findByType(OrdersJobDict jobType) {
        String jobKeys = "lky:cache:orders:job~keys";
        if (redisHelper.exists(jobKeys)) {
            Set<String> jobKeySet = (Set<String>) redisHelper.get(jobKeys);
            if (!CollectionUtils.isEmpty(jobKeySet)) {
                List<OrdersJob> ordersJobList = new ArrayList<>(jobKeySet.size());
                for (String jobKey : jobKeySet) {
                    OrdersJob ordersJob = (OrdersJob) redisHelper.get(jobKey);
                    if (ordersJob != null && jobType.compare(ordersJob.getType())) {
                        ordersJobList.add(ordersJob);
                    }
                }
                return ordersJobList;
            }
        }
        return ordersJobDao.findByType(String.valueOf(jobType));
    }

    public OrdersJob findByTypeAndOrdersId(OrdersJobDict jobType, String ordersId) {
        return ordersJobDao.findByTypeAndOrdersId(String.valueOf(jobType), ordersId);
    }

    public OrdersJob add(OrdersJob ordersJob) {
        super.save(ordersJob);
        String jobKeys = "lky:cache:orders:job~keys";
        String jobKey = "orders_job_" + ordersJob.getType() + "_" + ordersJob.getOrdersId();
        ZSetOperations<String, String> vo = redisTemplate.opsForZSet();
        vo.add(jobKeys, jobKey, 1d);
        redisHelper.set(jobKey, ordersJob);
        return ordersJob;
    }

    public void del(OrdersJobDict jobType, String ordersId) {
        OrdersJob ordersJob = this.findByTypeAndOrdersId(jobType, ordersId);
        if (ordersJob != null) {
            this.del(ordersJob);
        }
    }

    public void del(OrdersJob ordersJob) {
        super.delete(ordersJob);
        String jobKeys = "lky:cache:orders:job~keys";
        String jobKey = "orders_job_" + ordersJob.getType() + "_" + ordersJob.getOrdersId();
        ZSetOperations<String, String> vo = redisTemplate.opsForZSet();
        vo.remove(jobKeys, jobKey);
        if (redisHelper.exists(jobKey)) {
            redisHelper.remove(jobKey);
        }
    }

    public void batchDel(OrdersJobDict jobType, Set<String> ordersIdSet) {
        List<OrdersJob> ordersJobs = ordersJobDao.listByJobTypeAndIds(String.valueOf(jobType), ordersIdSet);
        if (!CollectionUtils.isEmpty(ordersJobs)) {
            String jobKeys = "lky:cache:orders:job~keys";
            ordersIdSet.forEach(ordersId -> {
                String jobKey = "orders_job_" + String.valueOf(jobType) + "_" + ordersId;
                ZSetOperations<String, String> vo = redisTemplate.opsForZSet();
                vo.remove(jobKeys, jobKey);
                if (redisHelper.exists(jobKey)) {
                    redisHelper.remove(jobKey);
                }
            });
            super.delete(ordersJobs);
        }
    }

    /**
     * 构建定时任务
     *
     * @param jobType  类型
     * @param ordersId 订单号
     * @return 定时任务
     */
    public OrdersJob buildJob(OrdersJobDict jobType, String ordersId) {
        OrdersJob ordersJob = new OrdersJob();
        Date date = new Date();
        Date executeTime = null;
        int dayOfYear = Calendar.MINUTE;
        if (environmentService.executeEnv()) {
            dayOfYear = Calendar.DAY_OF_YEAR;
        }

        switch (jobType) {
            case TYPE_CLOSE:
                executeTime = DateUtils.add(date, dayOfYear,
                        Integer.parseInt(environment.getProperty("orders.auto.close-time")));
                break;
            case TYPE_RECEIVE:
                executeTime = DateUtils.add(date, dayOfYear,
                        Integer.parseInt(environment.getProperty("orders.auto.receive-time")));
                break;
            default:
        }
        ordersJob.setExecuteTime(executeTime);
        ordersJob.setOrdersId(ordersId);
        ordersJob.setType(jobType.getKey());
        ordersJob.setExecuteTime(executeTime);
        ordersJob.setCreateTime(date);
        return ordersJob;
    }
}
