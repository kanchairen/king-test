package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AUserInfo;
import org.springframework.stereotype.Repository;

/**
 * 商户信息
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/22
 */
@Repository
public interface AUserInfoDao extends BaseDao<AUserInfo, Integer> {

    AUserInfo findByArea(String area);
}
