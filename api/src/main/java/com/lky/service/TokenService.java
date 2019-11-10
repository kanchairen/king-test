package com.lky.service;

import com.lky.global.session.TokenGenerator;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.dao.STokenDao;
import com.lky.entity.SToken;
import com.lky.enums.dict.STokenDict;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.lky.enums.code.UserResCode.CREATE_TOKEN_FAIL;
import static com.lky.enums.dict.STokenDict.TYPE_SYSTEM;

/**
 * token业务操作
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/26
 */
@Service
public class TokenService extends BaseService<SToken, Integer> {

    //系统token有效期24小时，app有效期30天
    private static final long SYSTEM_EXPIRE = 3600 * 24;
    private static final long APP_EXPIRE = 3600 * 24 * 30;

    @Inject
    private STokenDao tokenDao;

    @Override
    public BaseDao<SToken, Integer> getBaseDao() {
        return this.tokenDao;
    }

    public SToken findByUserIdAndType(Integer userId, STokenDict type) {
        return tokenDao.findByUserIdAndType(userId, String.valueOf(type));
    }

    public SToken findByToken(String token, STokenDict type) {
        return tokenDao.findByTokenAndType(token, String.valueOf(type));
    }

    public Map<String, Object> create(Integer userId, STokenDict type, Boolean isRefresh) {

        //token有效期
        long expire = TYPE_SYSTEM.equals(type) ? SYSTEM_EXPIRE : APP_EXPIRE;

        //生成一个token
        String token = TokenGenerator.generateValue();
        //过期时间
        Date expireTime = new Date(System.currentTimeMillis() + expire * 1000L);

        //判断是否生成过token
        SToken sToken = findByUserIdAndType(userId, type);
        //生成token
        if (sToken == null) {
            sToken = new SToken();
            sToken.setUserId(userId);
            sToken.setType(type.getKey());
            sToken.setToken(token);
            sToken.setUpdateTime(new Date());
            sToken.setExpireTime(expireTime);
            super.save(sToken);
        } else { //更新token
            if (isRefresh || System.currentTimeMillis() > sToken.getExpireTime().getTime()) {
                sToken.setToken(token);
            }
            sToken.setUpdateTime(new Date());
            sToken.setExpireTime(expireTime);
            super.update(sToken);
        }
        AssertUtils.isTrue(CREATE_TOKEN_FAIL, sToken.getId() != null);

        Map<String, Object> map = new HashMap<>();
        map.put("token", sToken.getToken());
        map.put("expire", expire);
        return map;
    }
}
