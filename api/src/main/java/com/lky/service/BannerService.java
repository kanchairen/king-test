package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.BannerDao;
import com.lky.entity.Banner;
import com.lky.entity.CustomText;
import com.lky.entity.ProductGroup;
import com.lky.entity.Shop;
import com.lky.enums.dict.BannerDict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.lky.enums.code.SetResCode.*;

/**
 * banner管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Service
public class BannerService extends BaseService<Banner, Integer> {

    @Inject
    private BannerDao bannerDao;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ShopService shopService;

    @Inject
    private CustomTextService customTextService;

    @Override
    public BaseDao<Banner, Integer> getBaseDao() {
        return this.bannerDao;
    }

    public List<Banner> findByType(String type) {
        return bannerDao.findByTypeOrderBySortIndexDesc(type);
    }

    public void create(Banner banner) {
        List<Banner> bannerList = this.findByType(banner.getType());
        int sortIndex = 1;
        if (!CollectionUtils.isEmpty(bannerList)) {
            //获取最大值加1
            sortIndex = bannerList.parallelStream()
                    .mapToInt(Banner::getSortIndex)
                    .max()
                    .getAsInt() + 1;
        }
        if (StringUtils.isNotEmpty(banner.getLinkType())) {
            BannerDict linkType = BannerDict.getEnum(banner.getLinkType());
            String linkValue = banner.getLinkValue();
            String linkName = this.checkLinkType(linkType, linkValue);
            banner.setLinkName(linkName);
        }

        banner.setSortIndex(sortIndex);
        super.save(banner);
    }

    public void modify(Integer id, Banner banner) {
        Banner bannerSource = super.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, bannerSource);
        if (StringUtils.isNotEmpty(banner.getLinkType())) {
            BannerDict linkType = BannerDict.getEnum(banner.getLinkType());
            String linkValue = banner.getLinkValue();
            String linkName = this.checkLinkType(linkType, linkValue);
            bannerSource.setLinkType(banner.getLinkType());
            bannerSource.setLinkValue(banner.getLinkValue());
            bannerSource.setLinkName(linkName);
        } else {
            bannerSource.setLinkType(null);
            bannerSource.setLinkValue(null);
            bannerSource.setLinkName(null);
        }
        bannerSource.setBannerImg(banner.getBannerImg());
        bannerSource.setUpdateTime(new Date());
        super.update(bannerSource);
    }

    public void swapPosition(Banner source, Banner dest) {
        int sourceSortIndex = source.getSortIndex();
        int destSortIndex = dest.getSortIndex();
        source.setSortIndex(destSortIndex);
        dest.setSortIndex(sourceSortIndex);
        super.save(source);
        super.save(dest);
    }

    private String checkLinkType(BannerDict linkType, String linkValue) {
        String linkName = "";
        switch (linkType) {
            case LINK_TYPE_PRODUCT:
                ProductGroup productGroup = productGroupService.findById(Integer.parseInt(linkValue));
                AssertUtils.notNull(PRODUCT_NOT_EXIST, productGroup);
                linkName = productGroup.getName();
                break;
            case LINK_TYPE_SHOP:
                Shop shop = shopService.findById(Integer.parseInt(linkValue));
                AssertUtils.notNull(SHOP_NOT_EXIST, shop);
                linkName = shop.getName();
                break;
            case LINK_TYPE_CUSTOM_TEXT:
                CustomText customText = customTextService.findById(Integer.parseInt(linkValue));
                AssertUtils.notNull(CUSTOM_TEXT_NOT_EXIST, customText);
                linkName = customText.getTitle();
                break;
            case LINK_TYPE_CUSTOM_LINK:
                linkName = linkValue;
                break;
            default:
        }
        return linkName;
    }

    /**
     * 小米/G米商城banner列表
     *
     * @param shopId 店铺id
     * @return 小米/G米商城banner列表
     */
    public List<Banner> findByShopId(Integer shopId) {
        SimpleSpecificationBuilder<Banner> builder = new SimpleSpecificationBuilder<>();
        builder.add("shopId", SpecificationOperator.Operator.eq, shopId);
        return super.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "sortIndex"));
    }
}
