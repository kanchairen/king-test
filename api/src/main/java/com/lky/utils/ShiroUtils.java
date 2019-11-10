package com.lky.utils;

import com.lky.commons.exception.ResponseException;
import com.lky.entity.AUser;
import com.lky.entity.SUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import static com.lky.enums.code.UserResCode.KAPTCHA_INVALID;

/**
 * shiro工具类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/26
 */
public class ShiroUtils {

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static Session getSession() {
        return getSubject().getSession();
    }

    public static SUser getSUser() {
        return (SUser) getSubject().getPrincipal();
    }

    public static Integer getSUserId() {
        return getSUser().getId();
    }

    public static AUser getAUser() {
        return (AUser) getSubject().getPrincipal();
    }

    public static Integer getAUserId() {
        return getAUser().getId();
    }

    public static void setSessionAttribute(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static Object getSessionAttribute(Object key) {
        return getSession().getAttribute(key);
    }

    public static boolean isLogin() {
        return getSubject().getPrincipal() != null;
    }

    public static void logout() {
        getSubject().logout();
    }

    public static String getKaptcha(String key) {
        Object kaptcha = getSessionAttribute(key);
        if (kaptcha == null) {
            throw new ResponseException(KAPTCHA_INVALID);
        }
        getSession().removeAttribute(key);
        return kaptcha.toString();
    }

}
