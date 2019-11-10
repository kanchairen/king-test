package com.lky.dao;

import com.lky.commons.base.BaseDao;
import com.lky.entity.AUserMember;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 代理商成员
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/22
 */
@Repository
public interface AUserMemberDao extends BaseDao<AUserMember, Integer> {

    List<AUserMember> findByAUserId(Integer id);
}
