package com.lky.global.oauth2;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * token
 *
 * @author luckyhua
 * @version 1.0.0
 * @since 2017/09/26
 */
public class OAuth2Token implements AuthenticationToken {

    private String token;

    private String type;

    public OAuth2Token(String token, String type) {
        this.token = token;
        this.type = type;
    }

    @Override
    public String getPrincipal() {
        return token + "|" + type;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
