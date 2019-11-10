package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.AIncomeRecordDao;
import com.lky.entity.AIncomeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.lky.enums.dict.AIncomeRecordDict.TYPE_DAY;

/**
 * 收益记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/26
 */
@Service
public class AIncomeRecordService extends BaseService<AIncomeRecord, Integer> {

    @Inject
    private AIncomeRecordDao aIncomeRecordDao;

    @Override
    public BaseDao<AIncomeRecord, Integer> getBaseDao() {
        return this.aIncomeRecordDao;
    }

    /**
     * 查找代理商按日、按月的收益记录
     *
     * @param type 查找类型，按日统计、按月统计（day，month）
     * @param aUserId  代理商id
     * @param pageable 分页信息
     * @return 代理商收益列表
     */
    public Page<AIncomeRecord> findByAUser(String type, Integer aUserId, Pageable pageable) {
        if (TYPE_DAY.getKey().equals(type)) {
            Specification<AIncomeRecord> spec = (root, query, cb) -> cb.and(cb.equal(root.get("aUserId"), aUserId));
            return super.findAll(spec, pageable);
        } else {
            List<AIncomeRecord> list = aIncomeRecordDao.findByMonth(aUserId, pageable.getOffset(), pageable.getPageSize());
            return new PageImpl<>(list, pageable, aIncomeRecordDao.countByMonth(aUserId));
        }
    }

    /**
     * 查找代理商昨天销售额产生的今日收益
     *
     * @param id 代理商id
     * @return 代理商昨日收益
     */
    public double findYesterdayIncome(Integer id) {
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        List<AIncomeRecord> aIncomeRecordList = aIncomeRecordDao.findByDate(id, new Date());
        if (!CollectionUtils.isEmpty(aIncomeRecordList)) {
            return aIncomeRecordList.get(0).getIncomeAmount();
        }
        return 0;
    }
}
