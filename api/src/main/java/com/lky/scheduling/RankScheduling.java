package com.lky.scheduling;

import com.google.common.collect.Lists;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.RankDao;
import com.lky.entity.Rank;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.entity.UserAsset;
import com.lky.service.RankService;
import com.lky.service.ShopService;
import com.lky.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.lky.enums.dict.RankDict.TYPE_SOLD;
import static com.lky.enums.dict.RankDict.TYPE_WPoint;

/**
 * 排行榜定时任务
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/13
 */
@Component
public class RankScheduling {

    private static final Logger log = LoggerFactory.getLogger(RankScheduling.class);

    @Inject
    private RankService rankService;

    @Inject
    private UserService userService;

    @Inject
    private ShopService shopService;

    @Inject
    private RankDao rankDao;

    /**
     * 用户排行榜
     */
    @Scheduled(cron = "0 10 0 * * ?")
    private void updateUserRank() throws UnknownHostException {
        if (!this.judgeHaveInsert(TYPE_WPoint.getKey())) {
            Specification<User> spec = (root, query, cb) -> {
                Join<User, UserAsset> userJoin = root.join("userAsset", JoinType.INNER);
                List<Predicate> predicates = Lists.newArrayList();
                predicates.add(cb.gt(userJoin.get("wpoint"), 0));
                cb.and(predicates.toArray(new Predicate[predicates.size()]));
                query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
                query.orderBy(cb.desc(userJoin.get("wpoint")));
                return query.getRestriction();
            };
            Pageable pageable = new PageRequest(0, 300);
            Page<User> userListPage = userService.findAll(spec, pageable);
            List<User> userList = userListPage.getContent();
            if (!CollectionUtils.isEmpty(userList)) {
                List<Rank> rankList = new ArrayList<>(userList.size());
                for (User user : userList) {
                    Rank rank = new Rank();
                    rank.setUser(user);
                    rank.setType(TYPE_WPoint.getKey());
                    rank.setNum(user.getUserAsset().getWpoint());
                    rankList.add(rank);
                }
                rankService.save(rankList);
                log.debug("ZGH1007: update user ranks ip --- " + InetAddress.getLocalHost());
            }
        }
    }

    /**
     * 商户排行榜
     */
    @Scheduled(cron = "0 15 0 * * ?")
    private void updateMerchantRank() throws UnknownHostException {
        if (!this.judgeHaveInsert(TYPE_SOLD.getKey())) {
            //获取商家的id和对应的销售额，销售额含线上和线下的。
            List<Object[]> dataList = rankDao.merchantRank();
            if (!CollectionUtils.isEmpty(dataList)) {
                List<Rank> rankList = new ArrayList<>(dataList.size());
                for (Object[] params : dataList) {
                    double number = Double.valueOf(params[1].toString());
                    Shop shop = shopService.findById((int) params[0]);
                    if (shop != null) {
                        Rank rank = new Rank();
                        rank.setShopId(shop.getId());
                        rank.setShopName(shop.getName());
                        rank.setUser(shop.getUser());
                        rank.setType(TYPE_SOLD.getKey());
                        rank.setNum(number);
                        rankList.add(rank);
                    }
                }
                rankService.save(rankList);
                log.debug("ZGH1008: update merchant ranks ip --- " + InetAddress.getLocalHost());
            }
        }
    }

    /**
     * 定时删除排行榜
     */
    @Scheduled(cron = "20 20 0 ? * MON")
    private void deletedOldData() {
        Date sevenDay = DateUtils.add(new Date(), Calendar.WEEK_OF_MONTH, -1);
        Specification<Rank> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), sevenDay));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        List<Rank> rankList = rankService.findAll(spec);
        if (!CollectionUtils.isEmpty(rankList)) {
            rankService.delete(rankList);
        }
    }

    /**
     * 判断今天是否已经插入排行榜
     *
     * @param type 类型
     * @return true 今天已经排过，false 今天还未排行
     */
    private Boolean judgeHaveInsert(String type) {
        Date beginToday = DateUtils.getBeginDate(new Date(), Calendar.DAY_OF_YEAR);
        Specification<Rank> rankSpec = (root, query, cb) ->{
            List<Predicate> predicateList = Lists.newArrayList();
            predicateList.add(cb.greaterThan(root.get("createTime"), beginToday));
            predicateList.add(cb.equal(root.get("type"), type));
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
        return rankService.count(rankSpec) > 0;
    }

}
