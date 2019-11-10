package com.lky.mapper;

import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dto.BannerDto;
import com.lky.entity.Banner;
import com.lky.entity.Shop;
import com.lky.enums.dict.BannerDict;
import com.lky.service.ShopService;
import com.lky.utils.BeanUtils;
import org.mapstruct.Mapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * bannerDto广告转换
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/12
 */
@Mapper(componentModel = "jsr330")
public abstract class BannerMapper {

    @Inject
    private ShopService shopService;

    public BannerDto toDto(Banner banner) {
        if (banner == null) {
            return null;
        }
        BannerDto bannerDto = new BannerDto();
        BeanUtils.copyPropertiesIgnoreNull(banner, bannerDto);
        if (BannerDict.LINK_TYPE_SHOP.compare(banner.getLinkType())) {
            AssertUtils.notNull(PublicResCode.SERVER_EXCEPTION, banner.getLinkValue());
            Integer shopId = Integer.parseInt(banner.getLinkValue());
            Shop shop = shopService.findById(shopId);
            AssertUtils.notNull(PublicResCode.SERVER_EXCEPTION, shop);
            if (shop.getShopConfig().getOpenRPoint()) {
                bannerDto.setOpenRPoint(Boolean.TRUE);
            }
            if (shop.getShopConfig().getOpenWPoint()) {
                bannerDto.setOpenRPoint(Boolean.TRUE);
            }
        }
        return bannerDto;
    }

    public List<BannerDto> toDtoList(List<Banner> bannerList) {
        if (CollectionUtils.isEmpty(bannerList)) {
            return null;
        }
        List<BannerDto> list = new ArrayList<>(bannerList.size());
        for (Banner banner : bannerList) {
            list.add(this.toDto(banner));
        }
        return list;
    }

}
