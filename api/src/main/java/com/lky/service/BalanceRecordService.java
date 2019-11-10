package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.BalanceRecordDao;
import com.lky.dto.DetailRecordDto;
import com.lky.entity.BalanceRecord;
import com.lky.entity.User;
import com.lky.enums.dict.BalanceRecordDict;
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
 * 大米记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/31
 */
@Service
public class BalanceRecordService extends BaseService<BalanceRecord, Integer> {

    @Inject
    private BalanceRecordDao balanceRecordDao;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private UserAssetService userAssetService;

    @Override
    public BaseDao<BalanceRecord, Integer> getBaseDao() {
        return this.balanceRecordDao;
    }

    public void create(User user, Double changeBalance, String type, String remark) {
        //添加大米变动记录表
        BalanceRecord balanceRecord = new BalanceRecord();
        balanceRecord.setChangeBalance(changeBalance);
        balanceRecord.setCurrentBalance(user.getUserAsset().getBalance() + changeBalance);
        balanceRecord.setUser(user);
        balanceRecord.setType(type);
        balanceRecord.setRemark(remark);
        super.save(balanceRecord);
    }

    /**
     * 大米转账
     *
     * @param user       转出用户
     * @param targetUser 转入用户
     * @param amount     转账大米
     */
    public void transfer(User user, User targetUser, double amount) {
        userAssetService.transfer(user.getUserAsset().getId(), targetUser.getUserAsset().getId(), amount);

        //增加大米明细记录
        BalanceRecord balanceRecordSource = new BalanceRecord();
        BalanceRecord balanceRecordTarget = new BalanceRecord();

        balanceRecordSource.setChangeBalance(-amount);
        balanceRecordSource.setCurrentBalance(user.getUserAsset().getBalance() - amount);
        balanceRecordSource.setUser(user);
        balanceRecordSource.setType(String.valueOf(BalanceRecordDict.TYPE_TRANSFER_REDUCE));
        balanceRecordSource.setRemark("大米转账给 " + targetUser.getMobile() + "，大米减少：" + amount);
        balanceRecordService.save(balanceRecordSource);

        balanceRecordTarget.setChangeBalance(amount);
        balanceRecordTarget.setCurrentBalance(targetUser.getUserAsset().getBalance() + amount);
        balanceRecordTarget.setUser(targetUser);
        balanceRecordTarget.setType(String.valueOf(BalanceRecordDict.TYPE_TRANSFER_INCREASE));
        balanceRecordTarget.setRemark(user.getMobile() + " 转账收入大米，大米增加：" + amount);
        balanceRecordService.save(balanceRecordTarget);
    }

    public Page<DetailRecordDto> findListByCondition(User user, Long beginTime, Long endTime, Pageable pageable) {
        Specification<BalanceRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user));
            if (beginTime != null && endTime != null && beginTime != 0 && endTime != 0) {
                predicates.add(cb.between(root.get("createTime"), new Date(beginTime), new Date(endTime)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<BalanceRecord> changList = super.findAll(spec, pageable);
        List<DetailRecordDto> detailRecordList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(changList.getContent())) {
            for (BalanceRecord balanceRecord : changList.getContent()) {
                DetailRecordDto detailDto = new DetailRecordDto();
                BeanUtils.copyPropertiesIgnoreNull(balanceRecord, detailDto);
                detailDto.setChange(balanceRecord.getChangeBalance());
                detailDto.setCurrent(balanceRecord.getCurrentBalance());
                //类型转中文
                detailDto.setShowType(BalanceRecordDict.getValue(balanceRecord.getType()));
                detailRecordList.add(detailDto);
            }
        }
        return new PageImpl<>(detailRecordList, pageable, changList.getTotalElements());
    }
}
