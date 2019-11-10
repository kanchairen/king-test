package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.UserMessageDao;
import com.lky.entity.UserMessage;
import com.lky.enums.dict.UserMessageDict;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

/**
 * app用户消息通知
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-10
 */
@Service
public class UserMessageService extends BaseService<UserMessage, Integer> {

    @Inject
    private UserMessageDao userMessageDao;

    @Override
    public BaseDao<UserMessage, Integer> getBaseDao() {
        return this.userMessageDao;
    }

    public void create(UserMessageDict targetType, Map<String, Object> map) {
        UserMessage userMessage = new UserMessage();
        userMessage.setUserId((Integer) map.get("userId"));
        userMessage.setType(UserMessageDict.TYPE_SYSTEM.getKey());
        userMessage.setTargetType(targetType.getKey());
        userMessage.setTitle(targetType.getValue());
        String content;
        switch (targetType) {
            //商品支付成功
            case TARGET_TYPE_CONCLUDE:
                userMessage.setTargetId((String) map.get("orderId"));
                content = UserMessageDict.CONTENT_CONCLUDE.getValue();
                content = content.replace("time", DateUtils.toString(new Date(), "yyyy年MM月dd日 HH:mm:ss"))
                        .replace("shop", (String) map.get("shopName"))
                        .replace("X", (String) map.get("price"))
                        .replace("x", (String) map.get("rPointPrice"));
                userMessage.setContent(content);
                break;

            //商品发货
            case TARGET_TYPE_SEND:
                userMessage.setTargetId((String) map.get("orderId"));
                content = UserMessageDict.CONTENT_SEND.getValue();
                content = content.replace("shop", (String) map.get("shopName"));
                userMessage.setContent(content);
                break;

            //申请店铺成功
            case TARGET_TYPE_APPLY_SHOP_SUCCESS:
                userMessage.setTargetId((String) map.get("applyRecordId"));
                content = UserMessageDict.CONTENT_APPLY_SHOP_SUCCESS.getValue();
                content = content.replace("time", DateUtils.toString((Date) map.get("time"), "yyyy年MM月dd日 HH:mm:ss"));
                userMessage.setContent(content);
                break;

            //申请店铺失败
            case TARGET_TYPE_APPLY_SHOP_FAIL:
                userMessage.setTargetId((String) map.get("applyRecordId"));
                content = UserMessageDict.CONTENT_APPLY_SHOP_FAIL.getValue();
                content = content.replace("time", DateUtils.toString((Date) map.get("time"), "yyyy年MM月dd日 HH:mm:ss"));
                userMessage.setContent(content);
                break;
        }
        super.save(userMessage);
    }
}
