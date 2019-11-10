package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.redis.RedisHelper;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.AWPointRecordDao;
import com.lky.entity.AWPointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 代理商G米记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/26
 */
@Service
public class AWPointRecordService extends BaseService<AWPointRecord, Integer> {

    @Inject
    private AWPointRecordDao awPointRecordDao;

    @Inject
    private RedisHelper redisHelper;

    @Override
    public BaseDao<AWPointRecord, Integer> getBaseDao() {
        return this.awPointRecordDao;
    }

    public void init() {
        List<Object[]> allTransWPoint = awPointRecordDao.allTransWPoint(DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR));
        if (!CollectionUtils.isEmpty(allTransWPoint)) {
            for (Object[] objArray : allTransWPoint) {
                Integer id = (Integer) objArray[0];
                double transWPoint = ((BigDecimal) objArray[1]).doubleValue();
                if (transWPoint > 0) {
                    awPointRecordDao.updateAgentTransWPoint(id, transWPoint);
//                    redisHelper.leftPush(AGENT_CONVERT_LIST, id);
                }
            }
        }
    }

    /**
     * 查找代理商G米变化记录
     *
     * @param aUserId  代理商id
     * @param type     类型
     * @param pageable 分页信息
     * @return G米列表
     */
    public Page<AWPointRecord> findByType(Integer aUserId, String type, Pageable pageable) {
        Specification<AWPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicateList = Lists.newArrayList();
            if (aUserId != null) {
                predicateList.add(cb.equal(root.get("aUserId"), aUserId));
            }

            if (type != null) {
                predicateList.add(cb.equal(root.get("type"), type));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };

        return super.findAll(spec, pageable);
    }

    public double findTransWPoint(Integer aUserId, Date date) {
        Specification<AWPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("aUserId"), aUserId));
            predicates.add(cb.lessThan(root.get("createTime"), date));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Pageable pageable = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "id"));
        Page<AWPointRecord> awPointRecordPage = awPointRecordDao.findAll(spec, pageable);
        return !CollectionUtils.isEmpty(awPointRecordPage.getContent()) ? awPointRecordPage.getContent().get(0).getCurrentWPoint() : 0;
    }

    /**
     * 代理商可激励G米
     *
     * @param aUserId 代理商id
     * @return 代理商可激励G米
     */
    public double findAUserTransWPoint(Integer aUserId) {
        return this.findTransWPoint(aUserId, DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR));
    }
}
