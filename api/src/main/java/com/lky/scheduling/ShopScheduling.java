package com.lky.scheduling;

import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.ShopDao;
import com.lky.entity.Shop;
import com.lky.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 店铺处理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
@Component
public class ShopScheduling {

    private static final Logger log = LoggerFactory.getLogger(ShopScheduling.class);

    @Inject
    private ShopService shopService;

    @Inject
    private ShopDao shopDao;

    /**
     * 统计所有店铺近3个月的订单总数量
     */
    @Scheduled(cron = "40 45 1 * * ?")
    private void updateShopRecentSumOrder() {
        //获取3个月前的时间
        Date threeMothBefore = DateUtils.add(new Date(), Calendar.MONTH, -3);
        //获取每个有支付订单的店铺近期线上和线下订单总数量
        List<Object[]> recentOrdersList = shopDao.findShopRecentOrdersNumber(threeMothBefore);

        //更新所有店铺的近期订单数量
        List<Shop> shopList = shopService.findAll();
        if (!CollectionUtils.isEmpty(shopList)) {
            List<Shop> changeShop = new ArrayList<>();
            for (Shop shop : shopList) {
                if (!CollectionUtils.isEmpty(recentOrdersList)) {
                    for (Object[] objects : recentOrdersList) {
                        if ((int) objects[0] == shop.getId()) {
                            shop.setRecentSumOrder(((BigInteger) objects[1]).intValue());
                            break;
                        } else {
                            shop.setRecentSumOrder(0);
                        }
                    }
                } else {
                    shop.setRecentSumOrder(0);
                }
                changeShop.add(shop);
            }
            shopService.update(changeShop);
        }
    }


}
