package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.HomeRecommendDao;
import com.lky.dto.HomeRecommendDto;
import com.lky.entity.*;
import com.lky.enums.dict.HomeRecommendDict;
import com.lky.mapper.HomeRecommendMapper;
import com.lky.mapper.ImageMapper;
import com.lky.utils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * 首页推荐
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/10
 */
@Service
public class HomeRecommendService extends BaseService<HomeRecommend, Integer> {

    @Inject
    private HomeRecommendDao homeRecommendDao;

    @Inject
    private HomeRecommendMapper homeRecommendMapper;

    @Inject
    private ShopService shopService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ImageMapper imageMapper;

    @Override
    public BaseDao<HomeRecommend, Integer> getBaseDao() {
        return this.homeRecommendDao;
    }

    public List<HomeRecommend> findByTargetType(String targetType) {
        return homeRecommendDao.findByTargetTypeOrderBySortIndexAsc(targetType);
    }

    public void create(HomeRecommendDto homeRecommendDto) {
        HomeRecommend homeRecommend = homeRecommendMapper.formDto(homeRecommendDto);
        this.buildHomeRecommend(homeRecommend.getTargetType(), homeRecommend, homeRecommend.getTargetId());
        List<HomeRecommend> homeRecommendList = this.findByTargetType(homeRecommend.getTargetType());
        int sortIndex = 1;
        if (!CollectionUtils.isEmpty(homeRecommendList)) {
            //获取最大值加1
            sortIndex = homeRecommendList.parallelStream()
                    .mapToInt(HomeRecommend::getSortIndex)
                    .max()
                    .getAsInt() + 1;
        }
        homeRecommend.setSortIndex(sortIndex);
        super.save(homeRecommend);
    }

    public void modify(HomeRecommend homeRecommend, HomeRecommendDto homeRecommendDto) {
        BeanUtils.copyPropertiesIgnoreNull(homeRecommendDto, homeRecommend, "id");
        this.buildHomeRecommend(homeRecommend.getTargetType(), homeRecommend, homeRecommend.getTargetId());
        homeRecommend.setUpdateTime(new Date());
        super.update(homeRecommend);
    }

    private void buildHomeRecommend(String type, HomeRecommend homeRecommend, Integer id) {
        HomeRecommendDict targetType = HomeRecommendDict.getEnum(type);
        switch (targetType) {
            case TARGET_TYPE_SHOP:
                Shop shop = shopService.findById(id);
                AssertUtils.isTrue(PublicResCode.PARAMS_EXCEPTION, "targetId", shop != null);
                homeRecommend.setName(shop.getName());
                homeRecommend.setPreviewImg(shop.getLogoImg());
                break;
            case TARGET_TYPE_PRODUCT:
                ProductGroup productGroup = productGroupService.findById(id);
                AssertUtils.isTrue(PublicResCode.PARAMS_EXCEPTION, "targetId", productGroup != null);
                homeRecommend.setName(productGroup.getName());
                List<Image> imageList = imageMapper.imgIdsToList(productGroup.getShowImgIds());
                homeRecommend.setPreviewImg(imageList.get(0));
                break;
            default:
        }
    }

    public void swapPosition(HomeRecommend source, HomeRecommend dest) {
        int sourceSortIndex = source.getSortIndex();
        int destSortIndex = dest.getSortIndex();
        source.setSortIndex(destSortIndex);
        dest.setSortIndex(sourceSortIndex);
        super.save(source);
        super.save(dest);
    }
}
