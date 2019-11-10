package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.RankDao;
import com.lky.entity.Rank;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 排行榜
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/13
 */
@Service
public class RankService extends BaseService<Rank, Integer> {

    @Inject
    private RankDao rankDao;

    @Override
    public BaseDao<Rank, Integer> getBaseDao() {
        return this.rankDao;
    }

}
