package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.UserCollectDao;
import com.lky.dto.UserCollectDto;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.entity.UserCollect;
import com.lky.mapper.ShopMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.code.ShopResCode.SHOP_ALREADY_COLLECT;
import static com.lky.enums.code.ShopResCode.SHOP_NOT_COLLECT;

/**
 * 用户收藏
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@Service
public class UserCollectService extends BaseService<UserCollect, Integer> {

    @Inject
    private UserCollectDao userCollectDao;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopMapper shopMapper;

    @Override
    public BaseDao<UserCollect, Integer> getBaseDao() {
        return this.userCollectDao;
    }

    public UserCollect findByUserAndShopId(User user, Integer shopId) {
        return userCollectDao.findByUserAndShopId(user, shopId);
    }

    public List<UserCollect> findByUser(User user) {
        return userCollectDao.findByUser(user);
    }

    public void shopCollect(User user, Integer id) {
        UserCollect userCollect = this.findByUserAndShopId(user, id);
        AssertUtils.isNull(SHOP_ALREADY_COLLECT, userCollect);

        userCollect = new UserCollect();
        userCollect.setShopId(id);
        userCollect.setUser(user);
        super.save(userCollect);
    }

    public void cancelShopCollect(User user, Integer id) {
        UserCollect userCollect = this.findByUserAndShopId(user, id);
        AssertUtils.notNull(SHOP_NOT_COLLECT, userCollect);

        super.delete(userCollect);
    }

    public List<UserCollectDto> list(User user) {
        List<UserCollect> userCollectList = this.findByUser(user);
        if (!CollectionUtils.isEmpty(userCollectList)) {
            List<UserCollectDto> list = Lists.newArrayListWithCapacity(userCollectList.size());
            for (UserCollect userCollect : userCollectList) {
                UserCollectDto userCollectDto = new UserCollectDto();
                userCollectDto.setId(userCollect.getId());
                Shop shop = shopService.findById(userCollect.getShopId());
                userCollectDto.setShop(shopMapper.toHeadDto(shop));
                list.add(userCollectDto);
            }

            return list;
        }
        return null;
    }

    /**
     * 用户是否收藏该店铺
     *
     * @param user 用户
     * @param id   店铺id
     * @return 是否被收藏
     */
    public boolean isCollect(User user, Integer id) {
        UserCollect userCollect = this.findByUserAndShopId(user, id);
        return userCollect != null;
    }
}
