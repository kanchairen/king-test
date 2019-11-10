package com.lky.global.inteceptor;

import com.lky.commons.utils.ExceptionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.constant.Constant;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.service.UserService;
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
 * 认证是否是商家
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/20
 */
@Component
public class BizInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(BizInterceptor.class);

    @Inject
    private TokenManager tokenManager;

    @Inject
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        AuthIgnore authIgnore;
        if (handler instanceof HandlerMethod) {
            authIgnore = ((HandlerMethod) handler).getMethodAnnotation(AuthIgnore.class);
        } else {
            return true;
        }

        //如果有@AuthIgnore注解，则不验证token
        if (authIgnore != null) {
            return true;
        }

        //从header中获取token
        String authToken = request.getHeader(Constant.AUTHORIZATION);
        if (StringUtils.isNotEmpty(authToken)) {
            authToken = URLDecoder.decode(authToken, "UTF-8");
            Integer userId = tokenManager.getUserIdByToken(TokenModel.TYPE_BIZ, authToken);
            if (userId == null) {
                ExceptionUtils.throwResponseException(USER_NO_LOGIN);
            }
            //检测是否是商家
            userService.checkIsMerchant(userId);
        }

        return true;
    }
}
