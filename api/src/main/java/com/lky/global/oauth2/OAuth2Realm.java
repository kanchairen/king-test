package com.lky.global.oauth2;

import com.lky.commons.utils.ExceptionUtils;
import com.lky.entity.AUser;
import com.lky.entity.SUser;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.service.AUserService;
import com.lky.service.SUserRoleService;
import com.lky.service.SUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

import static com.lky.commons.code.PublicResCode.USER_NO_LOGIN;
import static com.lky.enums.code.UserResCode.SYS_USER_LOCK;
import static com.lky.enums.dict.SUserDict.STATE_LOCK;

/**
 * 认证
 *
 * @author luckyhua
 * @version 1.0.0
 * @since 2017/08/23
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Inject
    @Lazy
    private SUserService sUserService;

    @Inject
    @Lazy
    private AUserService aUserService;

    @Inject
    @Lazy
    private TokenManager tokenManager;

    @Inject
    @Lazy
    private SUserRoleService sUserRoleService;

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        Object primaryPrincipal = principals.getPrimaryPrincipal();
        Set<String> permsSet = null;
        if (primaryPrincipal instanceof SUser) {
            SUser sUser = (SUser) primaryPrincipal;
            //用户权限列表
            permsSet = sUserRoleService.findPermsByUser(sUser);
            info.setStringPermissions(permsSet);
        } else if (primaryPrincipal instanceof AUser) {
            AUser aUser = (AUser) primaryPrincipal;
        }

        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String principal = (String) authenticationToken.getPrincipal();

        String[] principalArr = principal.split("\\|");
        String accessToken = principalArr[0];
        String type = principalArr[1];

        //根据accessToken，查询用户信息
        Integer userId = tokenManager.getUserIdByToken(type, accessToken);
        //token失效
        if (userId == null) {
            ExceptionUtils.throwResponseException(USER_NO_LOGIN);
        }

        SimpleAuthenticationInfo simpleAuthenticationInfo = null;
        if (TokenModel.TYPE_ACT.equals(type)) {
            AUser aUser = aUserService.findById(userId);
            //账号锁定
            if (STATE_LOCK.compare(aUser.getState())) {
                ExceptionUtils.throwResponseException(SYS_USER_LOCK);
            }
            simpleAuthenticationInfo = new SimpleAuthenticationInfo(aUser, accessToken, getName());
        } else if (TokenModel.TYPE_SYS.equals(type)) {
            SUser sUser = sUserService.findById(userId);
            //账号锁定
            if (STATE_LOCK.compare(sUser.getState())) {
                ExceptionUtils.throwResponseException(SYS_USER_LOCK);
            }
            simpleAuthenticationInfo = new SimpleAuthenticationInfo(sUser, accessToken, getName());
        }
        return simpleAuthenticationInfo;
    }
}
