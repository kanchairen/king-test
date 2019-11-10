package com.lky.service.async;

import com.lky.commons.utils.StringUtils;
import com.lky.entity.ApplyRecord;
import com.lky.entity.Orders;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.enums.dict.SmsLogDict;
import com.lky.enums.dict.UserMessageDict;
import com.lky.service.EnvironmentService;
import com.lky.service.ShopService;
import com.lky.service.SmsLogService;
import com.lky.service.UserMessageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lky.enums.dict.ApplyRecordDict.STATE_AGREE;

/**
 * 异步消息处理逻辑
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/4/13
 */
@Component
public class AsyncMessageService {

    @Inject
    private UserMessageService userMessageService;

    @Inject
    private ShopService shopService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private EnvironmentService environmentService;

    @Async
    public void sendOfflineOrdersPay(String mobile) {
        //发送短信通知用户
        if (environmentService.executeEnv()) {
            smsLogService.sendMobileSms(mobile, SmsLogDict.TYPE_OFFLINE_ORDERS_REMIND.getKey());
        }
    }

    @Async
    public void onlineOrdersPay(List<Orders> ordersList) {
        ordersList.forEach(this::sendOnlineOrdersPay);
    }

    private void sendOnlineOrdersPay(Orders orders) {
        User user = orders.getUser();
        Shop shop = shopService.findById(orders.getShopId());

        //发送用户系统消息，商品支付成功消息
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("orderId", orders.getId());
        map.put("shopName", shop.getName());
        map.put("price", String.valueOf(orders.getPrice()));
        map.put("rPointPrice", String.valueOf(orders.getRpointPrice()));
        userMessageService.create(UserMessageDict.TARGET_TYPE_CONCLUDE, map);
        //发送短信提醒
        if (environmentService.executeEnv()) {
            if (StringUtils.isNotEmpty(shop.getNotifyPhone())) {
                smsLogService.sendOrdersRemind(shop.getNotifyPhone());
            }
        }
    }

    /**
     * 发送异步消息
     *
     * @param applyRecord 申请记录
     */
    @Async
    public void sendShopApplyRecord(ApplyRecord applyRecord) {
        //构建发送申请消息参数
        Map<String, Object> map = new HashMap<>();
        map.put("userId", applyRecord.getUser().getId());
        map.put("applyRecordId", String.valueOf(applyRecord.getId()));
        map.put("time", applyRecord.getCreateTime());
        UserMessageDict userMessageDict = STATE_AGREE.compare(applyRecord.getState()) ?
                UserMessageDict.TARGET_TYPE_APPLY_SHOP_SUCCESS : UserMessageDict.TARGET_TYPE_APPLY_SHOP_FAIL;
        //审核结果发送申请店铺成功消息通知
        userMessageService.create(userMessageDict, map);
        //发送短信通知用户
        if (environmentService.executeEnv()) {
            smsLogService.sendMobileStateSms(applyRecord.getUser().getMobile(), SmsLogDict.TYPE_OPEN_SHOP, applyRecord.getState());
        }
    }

}
