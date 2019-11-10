package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.ShopKindDao;
import com.lky.entity.Shop;
import com.lky.entity.ShopKind;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.lky.enums.code.ShopResCode.KIND_NAME_EXIST;

/**
 * 店铺内分类
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/23
 */
@Service
public class ShopKindService extends BaseService<ShopKind, Integer> {

    @Inject
    private ShopKindDao shopKindDao;

    @Override
    public BaseDao<ShopKind, Integer> getBaseDao() {
        return this.shopKindDao;
    }

    public List<ShopKind> findByShopId(Integer shopId) {
        return shopKindDao.findByShopIdOrderBySortIndexDesc(shopId);
    }

    public ShopKind create(Shop shop, String name) {
        List<ShopKind> shopKindList = this.findByShopId(shop.getId());
        int sortIndex = 1;
        if (!CollectionUtils.isEmpty(shopKindList)) {
            AssertUtils.isTrue(KIND_NAME_EXIST, shopKindList.stream().noneMatch(sk -> sk.getName().equals(name)));
            //获取最大值加1
            sortIndex = shopKindList.parallelStream()
                    .mapToInt(ShopKind::getSortIndex)
                    .max()
                    .getAsInt() + 1;
        }

        ShopKind shopKind = new ShopKind();
        shopKind.setShopId(shop.getId());
        shopKind.setName(name);
        shopKind.setSortIndex(sortIndex);
        super.save(shopKind);
        return shopKind;
    }

    public void modify(ShopKind shopKind, String name) {
        if (!name.equals(shopKind.getName())) {
            List<ShopKind> shopKindList = this.findByShopId(shopKind.getShopId());
            if (!CollectionUtils.isEmpty(shopKindList)) {
                AssertUtils.isTrue(KIND_NAME_EXIST, shopKindList.stream().noneMatch(sk -> sk.getName().equals(name)));
            }
        }

        shopKind.setName(name);
        shopKind.setUpdateTime(new Date());
        super.update(shopKind);
    }

    public void swapPosition(ShopKind sourceShopKind, ShopKind destShopKind) {
        int sourceShopKindSortIndex = sourceShopKind.getSortIndex();
        int destShopKindSortIndex = destShopKind.getSortIndex();
        sourceShopKind.setSortIndex(destShopKindSortIndex);
        destShopKind.setSortIndex(sourceShopKindSortIndex);
        super.save(sourceShopKind);
        super.save(destShopKind);
    }
}
