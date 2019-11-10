package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.redis.RedisHelper;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.ArithUtils;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.SmsUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.SmsLogDao;
import com.lky.entity.SmsLog;
import com.lky.enums.dict.ApplyRecordDict;
import com.lky.enums.dict.AuthRecordDict;
import com.lky.enums.dict.SmsLogDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalTime;

import static com.lky.enums.dict.SmsLogDict.*;

/**
 * 发送短信
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@Service
public class SmsLogService extends BaseService<SmsLog, Integer> {

    private static final Logger log = LoggerFactory.getLogger(SmsLogService.class);

    private static final int EXPIRE_MINUTE = 5; //有效时间

    //todo 子账号激活有效期测试改为10分钟
    public static final int EXPIRE_ACTIVE_ACCOUNT = 10; //激活有效时间1天

    private static final String MOBILE_CODE = "lky:cache:sms:mobile_code:";

    public static final String ACCOUNT_ACTIVE_CODE = "lky:cache:account:active_code:";

    @Inject
    private SmsLogDao smsLogDao;

    @Inject
    private RedisHelper redisHelper;

    @Override
    public BaseDao<SmsLog, Integer> getBaseDao() {
        return this.smsLogDao;
    }

    /**
     * 发送验证码类的短信，存入缓存中，用于校验
     *
     * @param mobile 手机号
     * @param type   业务类型
     * @return 发送状态
     */
    public boolean sendMobileCode(String mobile, String type) {
        String code = StringUtils.getNumberUUID(6);
        String content = String.format(SmsLogDict.getValue(type), code, 5);
        boolean sendState = sendSms(mobile, content, type);
        if (sendState) { //发送成功，放入redis中
            redisHelper.setExpire(MOBILE_CODE + type + ":" + mobile, code, EXPIRE_MINUTE * 60L);
        }
        return sendState;
    }

    /**
     * 发送验证码类的短信，存入缓存中，用于校验
     *
     * @param mobile 手机号
     * @return 发送状态
     */
    public boolean sendH5MobileCode(String mobile) {
        String type = "register_h5";
        String code = StringUtils.getNumberUUID(6);
        Boolean sendState = Integer.valueOf(0).equals(SmsUtils.sendVoiceCode(mobile, code).getCode());
        if (sendState) {
            redisHelper.setExpire(MOBILE_CODE + type + ":" + mobile, code, EXPIRE_MINUTE * 60L);
        }
        return sendState;
    }

    /**
     * 发送乐康转换之前通知
     *
     * @param mobile       手机号
     * @param lhealthIndex 乐康指数
     * @return 发送状态
     */
    public boolean sendBeforeConvertSms(String mobile, double lhealthIndex) {
        String type = String.valueOf(TYPE_BEFORE_WPOINT_CONVERT);
        String content = String.format(SmsLogDict.getValue(type), "20", ArithUtils.mul(String.valueOf(lhealthIndex), "100")) + "%。";
        return sendSms(mobile, content, type);
    }

    /**
     * 发送乐康转换通知
     *
     * @param mobile       手机号
     * @param time         发放时间
     * @param lhealthIndex 乐康指数
     * @return 发送状态
     */
    public boolean sendConvertSms(String mobile, LocalTime time, double lhealthIndex) {
        String type = String.valueOf(TYPE_WPOINT_CONVERT);
        String content = String.format(SmsLogDict.getValue(type), time, ArithUtils.mul(String.valueOf(lhealthIndex), "100")) + "%。";
        return sendSms(mobile, content, type);
    }

    /**
     * 订单通知
     *
     * @param mobile 手机号
     * @return 发送状态
     */
    public boolean sendOrdersRemind(String mobile) {
        String type = String.valueOf(TYPE_ORDERS_REMIND);
        String content = String.format(SmsLogDict.getValue(type));
        return sendSms(mobile, content, type);
    }

    /**
     * 发送业务类短信，直接发送通知内容
     *
     * @param mobile 手机号
     * @param type   业务类型
     * @return 发送状态
     */
    public boolean sendMobileSms(String mobile, String type) {
        String content = SmsLogDict.getValue(type);
        return sendSms(mobile, content, type);
    }

    /**
     * * 发送业务类短信，直接发送通知内容
     *
     * @param mobile  手机号
     * @param smsType 短信类型
     * @param state   操作结果-替换字段
     * @return 发送状态
     */
    public boolean sendMobileStateSms(String mobile, SmsLogDict smsType, String state) {
        String content = null;
        String type = null;
        switch (smsType) {
            case TYPE_OPEN_SHOP:
                content = String.format(SmsLogDict.TYPE_OPEN_SHOP.getValue(), ApplyRecordDict.getEnum(state).getValue());
                type = String.valueOf(TYPE_OPEN_SHOP);
                break;
            case TYPE_AUTHENTICATION:
                content = String.format(SmsLogDict.TYPE_AUTHENTICATION.getValue(), AuthRecordDict.getEnum(state).getValue());
                type = String.valueOf(TYPE_AUTHENTICATION);
                break;
        }
        AssertUtils.notNull(PublicResCode.SERVER_EXCEPTION, content, type);
        return sendSms(mobile, content, type);
    }

    /**
     * 校验手机号验证码
     *
     * @param mobile 手机号
     * @param code   手机验证码
     * @param type   业务类型
     * @return 校验结果
     */
    public boolean checkMobileCode(String mobile, String code, SmsLogDict type) {
        String key = MOBILE_CODE + type.getKey() + ":" + mobile;
        if (redisHelper.exists(key)) {
            String cacheCode = (String) redisHelper.get(key);
            if (cacheCode.equals(code)) {
                redisHelper.remove(key);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 发送短链接和授权激活码
     *
     * @param mobile 手机号
     * @param type   业务类型
     * @return 发送状态
     */
    public boolean sendActiveCode(String mobile, String type, String url, String code) {
        String content = String.format(SmsLogDict.getValue(type), url);
        boolean sendState = sendSms(mobile, content, type);
        if (sendState) { //发送成功，放入redis中
            redisHelper.setExpire(ACCOUNT_ACTIVE_CODE + type + ":" + code, mobile, EXPIRE_ACTIVE_ACCOUNT * 60L);
            redisHelper.setExpire(ACCOUNT_ACTIVE_CODE + type + ":" + mobile, code, EXPIRE_ACTIVE_ACCOUNT * 60L);
        }
        return sendState;
    }


    /**
     * 所有发送短信，统一调用该方法，记录发送日志
     *
     * @param mobile  手机号
     * @param content 内容
     * @param type    业务类型
     * @return 发送状态
     */
    private boolean sendSms(String mobile, String content, String type) {
        ResponseInfo result = SmsUtils.send(mobile, content);
        log.debug("send sms result : " + result);
        Boolean sendState = Integer.valueOf(0).equals(result.getCode());
        //插入发送日志
//        SmsLog smsLog = new SmsLog();
//        smsLog.setMobile(mobile);
//        smsLog.setContent(content);
//        smsLog.setType(type);
//        smsLog.setState(String.valueOf(sendState ? SmsLogDict.STATE_SUCCESS : SmsLogDict.STATE_FAIL));
//        smsLog.setBackResult(result.getMsg());
//        super.save(smsLog);
        return sendState;
    }
}
