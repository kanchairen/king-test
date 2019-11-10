package com.lky.modules.api;

import com.google.common.collect.Lists;
import com.lky.LkyApplication;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.WPointRecordDao;
import com.lky.entity.OfflineOrders;
import com.lky.entity.Orders;
import com.lky.entity.User;
import com.lky.entity.WPointRecord;
import com.lky.enums.dict.WPointRecordDict;
import com.lky.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.*;

import static com.lky.enums.dict.OfflineOrdersDict.STATE_PAID;
import static com.lky.enums.dict.OrdersDict.STATE_OVER;

/**
 * G米记录测试类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-11-24
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LkyApplication.class)
public class PointRecordTest {

    @Inject
    private WPointRecordDao wPointRecordDao;

    @Inject
    private UserService userService;

    @Inject
    private ComputeService computeService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private OrdersService ordersService;

    @Inject
    private OfflineOrdersService offlineOrdersService;

    @Inject
    private OrdersReturnService ordersReturnService;

    @Test
    public void insertList() {


        User user1 = userService.findById(1);

        List<WPointRecord> wPointRecordList = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            WPointRecord wPointRecord = new WPointRecord();
            wPointRecord.setUser(user1);
            wPointRecord.setChangeWPoint(10);
            wPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
            wPointRecord.setType(String.valueOf(WPointRecordDict.TYPE_CONSUMER_LOCK_WPOINT_CONVERT));
            wPointRecord.setCurrentWPoint(10000);
            wPointRecord.setRemark("G米");
            wPointRecordList.add(wPointRecord);
        }

        wPointRecordDao.save(wPointRecordList);

//        pool.execute(() -> wPointRecordDao.save(wPointRecordList));
    }

    @Test
    public void save() {
        double merchantTransWPoint = 0.0;
        List<User> allUser = userService.findAll();
        if (!CollectionUtils.isEmpty(allUser)) {
            for (User user : allUser) {
//                merchantTransWPoint += wPointRecordService.findMerchantTransWPoint(user.getId());
            }
        }
        double lHealthIndex = 0.1150;
        //转换掉的商家G米
        double convertMerchantWPoint = computeService.convertOverWPoint(merchantTransWPoint, lHealthIndex);
        Map<String, Double> convertOverWPointMap = computeService.convertOverWPointMap(0, convertMerchantWPoint);
        if (CollectionUtils.isEmpty(convertOverWPointMap)) {
            return;
        }
        double merchantWPointConvertRPoint = convertOverWPointMap.get("merchantConvertRPoint");

        if (merchantWPointConvertRPoint > 0) {
            System.out.println("1111");
        } else {
            System.out.println("22222");
        }
    }

    @Test
    public void test() {
        //累计到前一天所有G米
        double sumTransWPoint = 0;
        Date add = DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR);
        List<User> allUser = userService.findAll();
        if (!CollectionUtils.isEmpty(allUser)) {
            double userTransWPoint = 0;
            double merchantTransWPoint = 0;
            for (User user : allUser) {
//                userTransWPoint += wPointRecordService.findUserTransWPoint(user.getId());
//                merchantTransWPoint += wPointRecordService.findMerchantTransWPoint(user.getId());
            }
            sumTransWPoint = userTransWPoint + merchantTransWPoint;
        }

        Date yesterdayTime = DateUtils.add(new Date(), Calendar.DATE, -1);
        Date yesterdayBegin = DateUtils.getBeginDate(yesterdayTime, Calendar.DAY_OF_YEAR);
        Date yesterdayEnd = DateUtils.getEndDate(yesterdayTime, Calendar.DAY_OF_YEAR);
        Specification<Orders> spec1 = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("state"), String.valueOf(STATE_OVER)));
            predicates.add(cb.between(root.get("overTime"), yesterdayBegin, yesterdayEnd));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Orders> ordersList1 = ordersService.findAll(spec1);

        double sum = ordersList1.stream().mapToDouble(Orders::getAmount).sum();
        //计算退款的金额
        double returnSum = ordersReturnService.sumOrdersReturned(ordersList1);
        double onlineOrdersPrice = sum - returnSum;


        Specification<OfflineOrders> spec2 = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("state"), String.valueOf(STATE_PAID)));
            predicates.add(cb.between(root.get("updateTime"), yesterdayBegin, yesterdayEnd));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<OfflineOrders> ordersList2 = offlineOrdersService.findAll(spec2);
        double offlineOrdersPrice = CollectionUtils.isEmpty(ordersList2) ? 0 : ordersList2.stream().mapToDouble(OfflineOrders::getAmount).sum();

        //前一天所有商家的现金成交额
        double sumMerchantOrdersPrice = onlineOrdersPrice + offlineOrdersPrice;

        double lHealthIndex = computeService.lHealthIndex(sumMerchantOrdersPrice, sumTransWPoint);
        System.out.println(lHealthIndex);
    }
}
