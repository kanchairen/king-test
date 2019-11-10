package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.RPointRecordDao;
import com.lky.dto.DetailRecordDto;
import com.lky.entity.RPointRecord;
import com.lky.entity.User;
import com.lky.enums.dict.RPointRecordDict;
import com.lky.utils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 小米记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/1
 */
@Service
public class RPointRecordService extends BaseService<RPointRecord, Integer> {

    @Inject
    private RPointRecordDao rPointRecordDao;

    @Override
    public BaseDao<RPointRecord, Integer> getBaseDao() {
        return this.rPointRecordDao;
    }

    public Page<DetailRecordDto> findListByCondition(User user, String userType,
                                                    Long beginTime, Long endTime, Pageable pageable) {
        Specification<RPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.equal(root.get("userType"), userType));
            if (beginTime != null && endTime != null && beginTime != 0 && endTime != 0) {
                predicates.add(cb.between(root.get("createTime"), new Date(beginTime), new Date(endTime)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<RPointRecord> changList = super.findAll(spec, pageable);
        List<DetailRecordDto> detailRecordList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(changList.getContent())) {
            for (RPointRecord rPointRecord : changList.getContent()) {
                DetailRecordDto detailDto = new DetailRecordDto();
                BeanUtils.copyPropertiesIgnoreNull(rPointRecord, detailDto);
                detailDto.setChange(rPointRecord.getChangeRPoint());
                detailDto.setCurrent(rPointRecord.getCurrentRPoint());
                //类型转中文
                detailDto.setShowType(RPointRecordDict.getValue(rPointRecord.getType()));
                detailRecordList.add(detailDto);
            }
        }
        return new PageImpl<>(detailRecordList, pageable, changList.getTotalElements());
    }
}
