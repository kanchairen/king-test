package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.commons.utils.IdWorker;
import com.lky.dao.OfflineOrdersDao;
import com.lky.entity.*;
import com.lky.enums.code.ShopResCode;
import com.lky.enums.dict.OfflineOrdersDict;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.lky.enums.code.OrderResCode.*;
import static com.lky.enums.dict.OfflineOrdersDict.STATE_PAID;

/**
 * 线下订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/30
 */
@Service
public class OfflineOrdersService extends BaseService<OfflineOrders, String> {

    @Inject
    private ShopService shopService;

    @Inject
    private ComputeService computeService;

    @Inject
    private OfflineOrdersDao offlineOrdersDao;

    @Inject
    private HighConfigService highConfigService;

    @Override
    public BaseDao<OfflineOrders, String> getBaseDao() {
        return this.offlineOrdersDao;
    }

    public double giveWPoint(User user, Integer shopId, double amount) {
        //获取线下店铺让利比
        Shop shop = shopService.findById(shopId);
        ShopConfig shopConfig = shop.getShopConfig();
        double benefitRate = shopConfig.getBenefitRate();

        return computeService.consumerGiveWPoint(amount, benefitRate);
    }

    public String create(User user, Integer shopId, double amount) {

        OfflineOrders offlineOrders = new OfflineOrders();

        //判断店铺是否有效，获取线下店铺让利比
        Shop shop = shopService.findById(shopId);
        AssertUtils.isTrue(NO_BUY_SELF_PRODUCT, shop.getUser().getId() != user.getId());
        AssertUtils.isTrue(ShopResCode.SHOP_CLOSE, shopService.judgeShopExpire(shop));
        ShopConfig shopConfig = shop.getShopConfig();
        double benefitRate = shopConfig.getBenefitRate();

        //消费者获取的G米
        double consumerGiveWPoint = computeService.consumerGiveWPoint(amount, benefitRate);

        //生成线下订单
        String orderCode = IdWorker.getOrderCode();
        offlineOrders.setId(orderCode);
        offlineOrders.setShop(shop);
        offlineOrders.setUser(user);
        offlineOrders.setPrice(amount);
        offlineOrders.setAmount(amount);
        offlineOrders.setBenefitRate(benefitRate);
        offlineOrders.setGiveWPoint(consumerGiveWPoint);
        offlineOrders.setState(String.valueOf(OfflineOrdersDict.STATE_UNPAID));
        super.save(offlineOrders);
        return orderCode;
    }

    /**
     * 前一天商家所有已完成线下订单的金额
     * 包括现金和小米
     *
     * @return 成交总额
     */
    public double sumMerchantOfflineOrdersPrice() {
        Date yesterdayTime = DateUtils.add(new Date(), Calendar.DATE, -1);
        Date yesterdayBegin = DateUtils.getBeginDate(yesterdayTime, Calendar.DAY_OF_YEAR);
        Date yesterdayEnd = DateUtils.getEndDate(yesterdayTime, Calendar.DAY_OF_YEAR);
        Specification<OfflineOrders> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("state"), String.valueOf(STATE_PAID)));
            predicates.add(cb.between(root.get("updateTime"), yesterdayBegin, yesterdayEnd));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<OfflineOrders> ordersList = super.findAll(spec);
        return CollectionUtils.isEmpty(ordersList) ? 0 : ordersList.stream().mapToDouble(OfflineOrders::getAmount).sum();
    }

    /**
     * 统计该区域下昨天线下订单的用户消费金额
     * 包含现金和红包
     *
     * @param area 区域
     * @return 消费总额
     */
    public double sumConsumerAmountByArea(String area, Date beginTime, Date endTime) {
        Specification<OfflineOrders> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("state"), String.valueOf(STATE_PAID)));
            if (beginTime != null && endTime != null) {
                predicates.add(cb.between(root.get("updateTime"), beginTime, endTime));
            } else {
                Date yesterdayTime = DateUtils.add(new Date(), Calendar.DATE, -1);
                Date yesterdayBegin = DateUtils.getBeginDate(yesterdayTime, Calendar.DAY_OF_YEAR);
                Date yesterdayEnd = DateUtils.getEndDate(yesterdayTime, Calendar.DAY_OF_YEAR);
                predicates.add(cb.between(root.get("updateTime"), yesterdayBegin, yesterdayEnd));
            }
            Join<OfflineOrders, User> userJoin = root.join("user", JoinType.LEFT);
            predicates.add(cb.like(userJoin.get("area"), area.trim() + "%"));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<OfflineOrders> ordersList = super.findAll(spec);
        return CollectionUtils.isEmpty(ordersList) ? 0 : ordersList.stream().mapToDouble(OfflineOrders::getAmount).sum();
    }
}
