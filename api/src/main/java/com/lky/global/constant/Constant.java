package com.lky.global.constant;

/**
 * 全局常量
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/30
 */
public class Constant {

    /**
     * token授权标识
     */
    public static final String AUTHORIZATION = "token";

    /**
     * 登录用户标识
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 当前所在的环境
     */
    public static final String PROFILE_ACTIVE_DEV = "dev";
    public static final String PROFILE_ACTIVE_TEST = "test";
    public static final String PROFILE_ACTIVE_PROD = "prod";

    /**
     * G米每天转换的标识
     */
    public static final String EVERY_CONVERT_SIGN = "lky:cache:wpoint:convert:";

    /**
     * G米每天转换的乐康指数
     */
    public static final String L_HEATH_INDEX = "lky:cache:lhealth_index:";

    public static final String USER_CONVERT_LIST = "lky:cache:user:convert:list";
    public static final String MERCHANT_CONVERT_LIST = "lky:cache:merchant:convert:list";
    public static final String AGENT_CONVERT_LIST = "lky:cache:agent:convert:list";

    /**
     * 易通代付redis的key
     */
    public static final String ETONE_DAI_FU = "lky:cache:etone:dai:fu";

    /**
     * 超级管理员username
     */
    public static final String ADMIN = "admin";

    /**
     * 代理商验证码
     */
    public static final String MKAPTCHA_SESSION_KEY = "MKAPTCHA_SESSION_KEY";
    /**
     * 代理商验证码
     */
    public static final String AKAPTCHA_SESSION_KEY = "AKAPTCHA_SESSION_KEY";

    /**
     * 易通金服代付接口长时间未回调，主动发起查询时间间隔:
     * 第一次查询间隔时间为10分钟
     * 其他次查询间隔时间为30分钟
     */
    public static final long FIRST_CYCLE = 10 * 60 * 1000;
    public static final long OTHER_CYCLE = 30 * 60 * 1000;

    /**
     * 用户增减类型导入常量
     */
    public static final String TYPE_WPOINT_CALCULATED = "用户G米(算代理商业绩)";
    public static final String TYPE_WPOINT_NOT_CALCULATED = "用户G米(不算代理商业绩)";
    public static final String TYPE_RPOINT = "用户小米";
    public static final String TYPE_LOCK_WPOINT = "用户存量G米";

    public static final String TYPE_MERCHANT_WPOINT = "商家G米";
    public static final String TYPE_MERCHANT_RPOINT = "商家小米";
    public static final String TYPE_MERCHANT_LOCK_WPOINT = "商家存量G米";

    /**
     * 用户银行卡绑定，默认银行logo链接地址
     */
    public static final String DEFAULT_BANK_IMAGE_LOGO = "https://gnc-image.oss-cn-hangzhou.aliyuncs.com/prod/sys/179301318.jpg";
}
