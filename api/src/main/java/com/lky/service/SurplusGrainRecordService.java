package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.SurplusGrainDao;
import com.lky.dto.DetailRecordDto;
import com.lky.entity.SurplusGrainRecord;
import com.lky.entity.User;
import com.lky.enums.dict.SurplusGrainRecordDict;
import com.lky.utils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 余粮公社记录
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/27
 */
@Service
public class SurplusGrainRecordService extends BaseService<SurplusGrainRecord, Integer> {

    @Inject
    private SurplusGrainDao surplusGrainDao;

    @Override
    public BaseDao<SurplusGrainRecord, Integer> getBaseDao() {
        return surplusGrainDao;
    }

    public Page<DetailRecordDto> findListByCondition(User user, Long beginTime, Long endTime,
                                                     Pageable pageable, String type) {
        Specification<SurplusGrainRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user));
            if (StringUtils.isNotEmpty(type)) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (beginTime != null && endTime != null && beginTime != 0 && endTime != 0) {
                predicates.add(cb.between(root.get("createTime"), new Date(beginTime), new Date(endTime)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<SurplusGrainRecord> changList = super.findAll(spec, pageable);
        List<DetailRecordDto> detailRecordList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(changList.getContent())) {
            for (SurplusGrainRecord surplusGrainRecord : changList.getContent()) {
                DetailRecordDto detailDto = new DetailRecordDto();
                BeanUtils.copyPropertiesIgnoreNull(surplusGrainRecord, detailDto);
                detailDto.setChange(surplusGrainRecord.getChangeSurplusGrain());
                detailDto.setCurrent(surplusGrainRecord.getCurrentSurplusGrain());
                Date incomeTime = surplusGrainRecord.getIncomeTime();
                //类型转中文
                detailDto.setShowType(SurplusGrainRecordDict.getValue(surplusGrainRecord.getType()));
                if (String.valueOf(SurplusGrainRecordDict.TYPE_FROM_BALANCE).equals(surplusGrainRecord.getType())
                        && incomeTime != null) {
                    if (incomeTime.before(new Date())) {
                        detailDto.setState("已确认");
                    } else {
                        detailDto.setState("确认中");
                        detailDto.setIncomeTime(incomeTime);
                    }
                }
                detailRecordList.add(detailDto);
            }
        }
        return new PageImpl<>(detailRecordList, pageable, changList.getTotalElements());
    }

    /**
     * 获取用户余粮公社确认中金额
     *
     * @param user 用户
     * @return 余粮公社确认中金额
     */
    public double confirmSurplusGrain(User user) {
        Specification<SurplusGrainRecord> addSpec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.equal(root.get("type"), String.valueOf(SurplusGrainRecordDict.TYPE_FROM_BALANCE)));
            predicates.add(cb.greaterThan(root.get("incomeTime"), new Date()));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<SurplusGrainRecord> addRecordList = super.findList(addSpec, new Sort(Sort.Direction.ASC, "createTime"));
        if (!CollectionUtils.isEmpty(addRecordList)) {
            double sumConfirm = 0;
            for (SurplusGrainRecord addRecord : addRecordList) {
                sumConfirm += addRecord.getChangeSurplusGrain();
            }
//            Specification<SurplusGrainRecord> reduceSpec = (root, query, cb) -> {
//                List<Predicate> predicates = Lists.newArrayList();
//                predicates.add(cb.equal(root.get("user"), user));
//                predicates.add(cb.equal(root.get("type"), String.valueOf(SurplusGrainRecordDict.TYPE_TO_BALANCE)));
//                predicates.add(cb.greaterThan(root.get("createTime"), addRecordList.get(0).getCreateTime()));
//                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//            };
//            List<SurplusGrainRecord> reduceRecordList = super.findAll(reduceSpec);
//            if (!CollectionUtils.isEmpty(reduceRecordList)) {
//                for (SurplusGrainRecord reduceRecord : reduceRecordList) {
//                    //转出记录中的数量为负数
//                    sumConfirm += reduceRecord.getChangeSurplusGrain();
//                }
//                return sumConfirm > 0 ? sumConfirm : 0;
//            }
            return sumConfirm;
        }
        return 0;
    }
}
