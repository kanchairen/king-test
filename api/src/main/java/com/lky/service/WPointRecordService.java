package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.redis.RedisHelper;
import com.lky.commons.utils.*;
import com.lky.dao.WPointRecordDao;
import com.lky.dto.DetailRecordDto;
import com.lky.entity.HighConfig;
import com.lky.entity.User;
import com.lky.entity.UserAsset;
import com.lky.entity.WPointRecord;
import com.lky.enums.dict.HighConfigDict;
import com.lky.enums.dict.WPointRecordDict;
import com.lky.utils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;

import static com.lky.enums.dict.WPointRecordDict.*;
import static com.lky.global.constant.Constant.MERCHANT_CONVERT_LIST;
import static com.lky.global.constant.Constant.USER_CONVERT_LIST;

/**
 * G米记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/31
 */
@Service
public class WPointRecordService extends BaseService<WPointRecord, Integer> {

    @Inject
    private WPointRecordDao wPointRecordDao;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private UserService userService;

    @Inject
    private ComputeService computeService;

    @Inject
    private RedisHelper redisHelper;

    @Override
    public BaseDao<WPointRecord, Integer> getBaseDao() {
        return this.wPointRecordDao;
    }

    public void init() {
        List<Object[]> allUserTransWPoint = wPointRecordDao.allTransWPoint(String.valueOf(USER_TYPE_CONSUMER), DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR));
        if (!CollectionUtils.isEmpty(allUserTransWPoint)) {
            for (Object[] objArray : allUserTransWPoint) {
                Integer userId = (Integer) objArray[0];
                double transWPoint = ((BigDecimal) objArray[1]).doubleValue();
                if (transWPoint > 0) {
                    wPointRecordDao.updateTransWPoint(userId, transWPoint);
                    redisHelper.leftPush(USER_CONVERT_LIST, userId);
                }
            }
        }
        List<Object[]> allMerchantTransWPoint = wPointRecordDao.allTransWPoint(String.valueOf(USER_TYPE_MERCHANT), DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR));
        if (!CollectionUtils.isEmpty(allMerchantTransWPoint)) {
            for (Object[] objArray : allMerchantTransWPoint) {
                Integer userId = (Integer) objArray[0];
                double transWPoint = ((BigDecimal) objArray[1]).doubleValue();
                if (transWPoint > 0) {
                    wPointRecordDao.updateMerchantTransWPoint(userId, transWPoint);
                    redisHelper.leftPush(MERCHANT_CONVERT_LIST, userId);
                }
            }
        }
    }

    /**
     * 所有用户和商家的可激励G米之和
     *
     * @return 计算结果
     */
    public double sumTransWPoint() {
        double sumUserTransWPoint = wPointRecordDao.sumTransWPoint(String.valueOf(USER_TYPE_CONSUMER), DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR));
        double sumMerchantTransWPoint = wPointRecordDao.sumTransWPoint(String.valueOf(USER_TYPE_MERCHANT), DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR));
        return sumUserTransWPoint + sumMerchantTransWPoint;
    }

    /**
     * 消费者确认收货，计算消费者上线分成
     *
     * @param user   消费者
     * @param wPoint 消费者获得G米
     */
    public void userSharingWPoint(User user, Double wPoint) {
        HighConfig highConfig = baseConfigService.findH();
        if (highConfig == null || StringUtils.isEmpty(highConfig.getMemberRights())) {
            return;
        }

        //获取分成比例
        Map<String, Double> map = JsonUtils.jsonToMap(highConfig.getMemberRights(), String.class, Double.class);
        if (map == null) {
            return;
        }
        Double sharingRate = map.get(HighConfigDict.SHARING_FIRST.getKey());

        if (user.getParentId() == null) {
            return;
        }
        User parent = userService.findById(user.getParentId());
        for (int i = 1; i < 3; i++) {
            if (parent == null) {
                break;
            }

            if (i == 2) {
                sharingRate = map.get(HighConfigDict.SHARING_SECOND.getKey());
            }

            double sharingWPoint = computeService.sharingWPoint(wPoint, sharingRate);
            if (sharingWPoint > 0) {
                UserAsset parentUserAsset = parent.getUserAsset();
                //添加G米变动记录
                WPointRecord record = new WPointRecord();
                record.setUser(parent);
                record.setChangeWPoint(sharingWPoint);
                record.setUserType(String.valueOf(USER_TYPE_CONSUMER));
                record.setType(String.valueOf(TYPE_CONSUMER_DIVIDE));
                record.setCurrentWPoint(parentUserAsset.getWpoint() + sharingWPoint);
                //下线（张三 13012341234）分成获得G米
                String name = user.getNickname() != null ? user.getNickname() + " " : "";
                record.setRemark("推荐用户(" + name + user.getMobile() + ")消费奖励获得G米");
                super.save(record);

                //更新用户G米
                parentUserAsset.setWpoint(parentUserAsset.getWpoint() + sharingWPoint);
                parent.setUpdateTime(new Date());
                userService.update(parent);
            }

            //获取用户上上级
            if (parent.getParentId() == null) {
                return;
            }
            parent = userService.findById(parent.getParentId());
        }
    }

    /**
     * 商家有销售额，计算商家上线分成
     *
     * @param merchant 商家
     * @param wPoint   商家销售获得G米
     */
    public void merchantSharingWPoint(User merchant, Double wPoint) {
        HighConfig highConfig = baseConfigService.findH();
        if (highConfig == null || StringUtils.isEmpty(highConfig.getMemberRights())) {
            return;
        }

        //获取分成比例
        Map<String, Double> map = JsonUtils.jsonToMap(highConfig.getMemberRights(), String.class, Double.class);
        if (map == null) {
            return;
        }
        Double sharingRate = map.get(HighConfigDict.SHARING_MERCHANT.getKey());

        if (merchant.getParentId() == null) {
            return;
        }
        User parent = userService.findById(merchant.getParentId());
        if (parent == null) {
            return;
        }

        double sharingWPoint = computeService.sharingWPoint(wPoint, sharingRate);
        if (sharingWPoint > 0) {
            UserAsset parentUserAsset = parent.getUserAsset();
            //添加G米变动记录
            WPointRecord record = new WPointRecord();
            record.setUser(parent);
            record.setChangeWPoint(sharingWPoint);
            record.setUserType(String.valueOf(USER_TYPE_CONSUMER));
            record.setType(String.valueOf(TYPE_CONSUMER_DIVIDE));
            record.setCurrentWPoint(parentUserAsset.getWpoint() + sharingWPoint);
            //下线商家（张三 13012341234）销售分成获得G米
            String name = merchant.getNickname() != null ? merchant.getNickname() + " " : "";
            record.setRemark("推荐商家(" + name + merchant.getMobile() + ")消费奖励获得G米");
            super.save(record);

            //更新用户G米
            parentUserAsset.setWpoint(parentUserAsset.getWpoint() + sharingWPoint);
            parent.setUpdateTime(new Date());
            userService.update(parent);
        }
    }

    /**
     * 统计需要计算代理商收益的该区域下用户白积分总和
     *
     * @param area 区域
     * @return 代理商收益白积分总和
     */
    public double sumAgentIncomeWPoint(String area, Date beginTime, Date endTime) {
        Specification<WPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("userType"), String.valueOf(USER_TYPE_CONSUMER)));
            predicates.add(root.get("type").in(
                    String.valueOf(TYPE_CONSUMER_ONLINE_ORDERS), String.valueOf(TYPE_CONSUMER_OFFLINE_ORDERS),
                    String.valueOf(TYPE_CONSUMER_SYS_GIVE), String.valueOf(TYPE_CONSUMER_QR_CODE),
                    String.valueOf(TYPE_CONSUMER_SYS_REDUCE)
            ));
            predicates.add(cb.equal(root.get("calculated"), Boolean.TRUE));
            if (beginTime != null && endTime != null) {
                predicates.add(cb.between(root.get("createTime"), beginTime, endTime));
            } else {
                Date yesterdayTime = DateUtils.add(new Date(), Calendar.DATE, -1);
                Date yesterdayBegin = DateUtils.getBeginDate(yesterdayTime, Calendar.DAY_OF_YEAR);
                Date yesterdayEnd = DateUtils.getEndDate(yesterdayTime, Calendar.DAY_OF_YEAR);
                predicates.add(cb.between(root.get("createTime"), yesterdayBegin, yesterdayEnd));
            }
            Join<WPointRecord, User> userJoin = root.join("user", JoinType.LEFT);
            predicates.add(cb.like(userJoin.get("area"), area.trim() + "%"));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<WPointRecord> wPointRecordList = super.findAll(spec);
        return ArithUtils.round(CollectionUtils.isEmpty(wPointRecordList) ? 0 :
                wPointRecordList.stream().mapToDouble(WPointRecord::getChangeWPoint).sum(), 2);
    }

    public Page<DetailRecordDto> findListByCondition(User user, String userType,
                                                     Long beginTime, Long endTime, Pageable pageable) {
        Specification<WPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.equal(root.get("userType"), userType));
            if (beginTime != null && endTime != null && beginTime != 0 && endTime != 0) {
                predicates.add(cb.between(root.get("createTime"), new Date(beginTime), new Date(endTime)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<WPointRecord> changList = super.findAll(spec, pageable);
        List<DetailRecordDto> detailRecordList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(changList.getContent())) {
            for (WPointRecord wPointRecord : changList.getContent()) {
                DetailRecordDto detailDto = new DetailRecordDto();
                BeanUtils.copyPropertiesIgnoreNull(wPointRecord, detailDto);
                detailDto.setChange(wPointRecord.getChangeWPoint());
                detailDto.setCurrent(wPointRecord.getCurrentWPoint());
                //类型转中文
                detailDto.setShowType(WPointRecordDict.getValue(wPointRecord.getType()));
                detailRecordList.add(detailDto);
            }
        }
        return new PageImpl<>(detailRecordList, pageable, changList.getTotalElements());
    }

    /**
     * 获取用户昨日余粮公社收益G米
     *
     * @param user 用户
     * @return 昨日余粮公社收益
     */
    public double findSurplusGrainIncome(User user) {
        Date yesterday = DateUtils.add(new Date(), Calendar.DATE, -1);
        Specification<WPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user));
            predicates.add(cb.equal(root.get("type"), String.valueOf(TYPE_CONSUMER_SURPLUS_GRAIN_INCOME)));
            predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), yesterday));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<WPointRecord> wPointRecordList = super.findAll(spec);
        return CollectionUtils.isEmpty(wPointRecordList) ? 0 : wPointRecordList.get(0).getChangeWPoint();
    }
}
