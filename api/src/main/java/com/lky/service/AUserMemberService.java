package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.AUserMemberDao;
import com.lky.entity.AUserMember;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/22
 */
@Service
public class AUserMemberService extends BaseService<AUserMember, Integer> {

    @Inject
    private AUserMemberDao aUserMemberDao;

    @Override
    public BaseDao<AUserMember, Integer> getBaseDao() {
        return this.aUserMemberDao;
    }

    public List<AUserMember> findByAUserId(Integer id) {
        return aUserMemberDao.findByAUserId(id);
    }
}
