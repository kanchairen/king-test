package com.lky.global.inteceptor;

import com.lky.commons.utils.ExceptionUtils;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.annotation.MerchantSign;
import com.lky.global.constant.Constant;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

import static com.lky.commons.code.PublicResCode.USER_NO_LOGIN;

/**
 * 权限(Token)验证
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/21
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Inject
    private UserService userService;

    @Inject
    private TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        AuthIgnore authIgnore;
        MerchantSign merchantSign;
        if (handler instanceof HandlerMethod) {
            authIgnore = ((HandlerMethod) handler).getMethodAnnotation(AuthIgnore.class);
            merchantSign = ((HandlerMethod) handler).getMethodAnnotation(MerchantSign.class);
        } else {
            return true;
        }

        //如果有@AuthIgnore注解，则不验证token
        if (authIgnore != null) {
            return true;
        }

        String requestURI = request.getRequestURI();
        String type = TokenModel.TYPE_APP;
        if (requestURI.startsWith("/biz")) {
            type = TokenModel.TYPE_BIZ;
        }

        //从header中获取token
        String authToken = request.getHeader(Constant.AUTHORIZATION);
        //如果header中不存在token，则从参数中获取token
        if (StringUtils.isBlank(authToken)) {
            authToken = request.getParameter(Constant.AUTHORIZATION);
        }

        //token为空
        if (StringUtils.isBlank(authToken)) {
            ExceptionUtils.throwResponseException(USER_NO_LOGIN);
        }

        authToken = URLDecoder.decode(authToken, "UTF-8");

        Integer userId = tokenManager.getUserIdByToken(type, authToken);
        if (userId == null) {
            ExceptionUtils.throwResponseException(USER_NO_LOGIN);
        }

        //检查token信息
        if (!tokenManager.checkToken(type, authToken)) {
            ExceptionUtils.throwResponseException(USER_NO_LOGIN);
        }

        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(Constant.LOGIN_USER_KEY, userId);

        //如果有@MerchantSign，则验证登录用户的角色
        if (merchantSign != null) {
            userService.checkIsMerchant(userId);
        }

        return true;
    }
}
