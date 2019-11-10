package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.ABalanceRecordDao;
import com.lky.entity.ABalanceRecord;
import com.lky.entity.AUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * 代理商大米变动记录表Service
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-22
 */
@Service
public class ABalanceRecordService extends BaseService<ABalanceRecord, Integer> {

    @Inject
    private ABalanceRecordDao aBalanceRecordDao;

    @Override
    public BaseDao<ABalanceRecord, Integer> getBaseDao() {
        return this.aBalanceRecordDao;
    }

    /**
     * 添加代理商大米变动记录表
     *
     * @param aUser         代理商
     * @param changeBalance 变动大米
     * @param type          类型
     * @param remark        备注
     */
    public void create(AUser aUser, Double changeBalance, String type, String remark) {
        //添加大米变动记录表
        ABalanceRecord record = new ABalanceRecord();
        record.setChangeBalance(changeBalance);
        record.setCurrentBalance(aUser.getAUserAsset().getBalance() + changeBalance);
        record.setAUserId(aUser.getId());
        record.setType(type);
        record.setRemark(remark);
        super.save(record);
    }

    /**
     * 查找代理商大米变化记录
     *
     * @param aUserId  代理商id
     * @param type     类型
     * @param pageable 分页信息
     * @return 大米列表
     */
    public Page<ABalanceRecord> findByType(Integer aUserId, String type, Pageable pageable) {
        Specification<ABalanceRecord> spec = (root, query, cb) -> {
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
}
