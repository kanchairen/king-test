package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.dao.ARPointRecordDao;
import com.lky.entity.ARPointRecord;
import com.lky.entity.AUser;
import com.lky.entity.RPointRecord;
import com.lky.entity.User;
import com.lky.enums.dict.ARPointRecordDict;
import com.lky.enums.dict.RPointRecordDict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * 代理商小米记录
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/26
 */
@Service
public class ARPointRecordService extends BaseService<ARPointRecord, Integer> {

    @Inject
    private ARPointRecordDao arPointRecordDao;

    @Inject
    private AUserService aUserService;

    @Inject
    private UserService userService;

    @Inject
    private RPointRecordService rPointRecordService;

    @Override
    public BaseDao<ARPointRecord, Integer> getBaseDao() {
        return arPointRecordDao;
    }


    public double sumTransRPoint(Integer id) {
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        SimpleSpecificationBuilder<ARPointRecord> builder = new SimpleSpecificationBuilder<>();
        builder.add("id", SpecificationOperator.Operator.eq, id);
        builder.add("type", SpecificationOperator.Operator.eq, ARPointRecordDict.TYPE_ROLL_OUT.getKey());
        List<ARPointRecord> arPointRecordList = super.findAll(builder.generateSpecification());
        return 0;
    }

    /**
     * 查找代理商小米变化记录
     *
     * @param aUserId  代理商id
     * @param type     类型
     * @param pageable 分页信息
     * @return 小米列表
     */
    public Page<ARPointRecord> findByType(Integer aUserId, String type, Pageable pageable) {
        Specification<ARPointRecord> spec = (root, query, cb) -> {
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

    /**
     * 代理商小米转出到用户小米
     *
     * @param aUser      转出用户
     * @param targetUser 转入用户
     * @param amount     转账大米
     */
    public void transfer(AUser aUser, User targetUser, double amount) {
        aUser.getAUserAsset().setRpoint(aUser.getAUserAsset().getRpoint() - amount);
        targetUser.getUserAsset().setRpoint(targetUser.getUserAsset().getRpoint() + amount);
        aUserService.update(aUser);
        userService.update(targetUser);

        //增加代理商小米明细记录
        ARPointRecord arPointRecord = new ARPointRecord();
        arPointRecord.setChangeRPoint(-amount);
        arPointRecord.setAUserId(aUser.getId());
        arPointRecord.setCurrentRPoint(aUser.getAUserAsset().getRpoint());
        arPointRecord.setType(String.valueOf(ARPointRecordDict.TYPE_ROLL_OUT));
        arPointRecord.setRemark(targetUser.getMobile());
        super.save(arPointRecord);

        //增加app用户小米明细记录
        RPointRecord rPointRecord = new RPointRecord();
        rPointRecord.setChangeRPoint(amount);
        rPointRecord.setCurrentRPoint(targetUser.getUserAsset().getRpoint());
        rPointRecord.setUser(targetUser);
        rPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_CONSUMER));
        rPointRecord.setType(String.valueOf(RPointRecordDict.TYPE_AGENT_TRANSFER));
        rPointRecord.setRemark(aUser.getMobile() + " 转账收入小米，小米增加：" + amount);
        rPointRecordService.save(rPointRecord);
    }
}
