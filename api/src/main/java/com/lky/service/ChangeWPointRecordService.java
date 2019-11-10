package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.ChangeWPointRecordDao;
import com.lky.entity.ChangeWPointRecord;
import com.lky.entity.User;
import com.lky.enums.dict.ChangeWPointRecordDict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

/**
 * 赠送/扣减G米记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/1/8
 */
@Service
public class ChangeWPointRecordService extends BaseService<ChangeWPointRecord, Integer> {

    @Inject
    private ChangeWPointRecordDao changeWPointRecordDao;

    @Override
    public BaseDao<ChangeWPointRecord, Integer> getBaseDao() {
        return this.changeWPointRecordDao;
    }

    public Page<ChangeWPointRecord> findByCondition(User user, String type,
                                                    Long beginTime, Long endTime, Pageable pageable) {
        Specification<ChangeWPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.equal(root.get("type"), type));
            predicates.add(cb.equal(root.get("auditState"), ChangeWPointRecordDict.AUDIT_STATE_AGREE.getKey()));
            if (beginTime != null && endTime != null && beginTime != 0 && endTime != 0) {
                predicates.add(cb.between(root.get("createTime"), new Date(beginTime), new Date(endTime)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<ChangeWPointRecord> changList = super.findAll(spec,pageable);
        return new PageImpl<>(changList.getContent(), pageable, changList.getTotalElements());
    }
}
