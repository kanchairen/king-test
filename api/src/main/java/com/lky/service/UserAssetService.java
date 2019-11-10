package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.UserAssetDao;
import com.lky.entity.UserAsset;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 用户资产表
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/3/21
 */
@Service
public class UserAssetService extends BaseService<UserAsset, Integer> {

    @Inject
    private UserAssetDao userAssetDao;

    @Override
    public BaseDao<UserAsset, Integer> getBaseDao() {
        return this.userAssetDao;
    }

    public void transfer(Integer sourceId, Integer targetId, double amount) {
        userAssetDao.updateBalance(sourceId, -amount);
        userAssetDao.updateBalance(targetId, amount);
    }

    public void balancePay(Integer id, double amount) {
        userAssetDao.updateBalance(id, -amount);
    }

    public void covertUserUpdate(Integer id, double balance, double wpoint, double rpoint) {
        userAssetDao.covertUserUpdate(id, balance, wpoint, rpoint);
    }

    public void covertMerchantUpdate(Integer id, double balance, double wpoint, double rpoint) {
        userAssetDao.covertMerchantUpdate(id, balance, wpoint, rpoint);
    }
}
