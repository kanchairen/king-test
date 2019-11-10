package com.lky.global.resolver;

import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.global.constant.Constant;
import com.lky.service.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.inject.Inject;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/10
 */
@Component
public class LoginUserResolver implements HandlerMethodArgumentResolver {

    @Inject
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class) && parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
        //获取用户ID
        Object object = request.getAttribute(Constant.LOGIN_USER_KEY, RequestAttributes.SCOPE_REQUEST);
        if (object == null) {
            return null;
        }

        //获取用户信息返回
        return userService.findById((Integer) object);
    }
}
