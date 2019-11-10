package com.lky.mapper;

import com.lky.commons.utils.CollectionUtils;
import com.lky.dto.ShopDto;
import com.lky.dto.ShopHeadDto;
import com.lky.dto.ShopSimpleDto;
import com.lky.entity.Image;
import com.lky.entity.Shop;
import com.lky.entity.ShopConfig;
import com.lky.service.ImageService;
import com.lky.utils.BeanUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺修改Dto转换
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/20
 */

@Mapper(componentModel = "jsr330")
public abstract class ShopMapper {

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private ImageService imageService;

    @Mappings({
            @Mapping(source = "shopBannerImgList", target = "bannerImgIds"),
            @Mapping(source = "shopLogoImg", target = "logoImg"),
    })
    public abstract Shop fromDto(ShopDto shopDto);

    @Mappings({
            @Mapping(source = "bannerImgIds", target = "shopBannerImgList"),
            @Mapping(source = "logoImg", target = "shopLogoImg"),
            @Mapping(source = "shopConfig.benefitRate", target = "benefitRate"),
            @Mapping(source = "shopConfig.showBanner", target = "showBanner"),
            @Mapping(source = "shopConfig.showKind", target = "showKind"),
            @Mapping(source = "shopConfig.openRPoint", target = "openRPoint"),
            @Mapping(source = "shopConfig.openWPoint", target = "openWPoint"),
            @Mapping(source = "shopDatum.licenseImgIds", target = "shopLicenseImgList"),
            @Mapping(source = "shopDatum.openShopExpire", target = "openShopExpire"),
            @Mapping(target = "distance", constant = "-1.0")
    })
    public abstract ShopDto toDto(Shop shop);

    public ShopHeadDto toHeadDto(Shop shop) {
        if (shop == null) {
            return null;
        }

        ShopHeadDto shopHeadDto = new ShopHeadDto();

        shopHeadDto.setId(shop.getId());
        shopHeadDto.setName(shop.getName());
        shopHeadDto.setAddress(shop.getAddress());
        shopHeadDto.setContactPhone(shop.getContactPhone());
        shopHeadDto.setLogoImg(shop.getLogoImg());
        shopHeadDto.setOpenRPoint(shop.getShopConfig().getOpenRPoint());
        shopHeadDto.setOpenWPoint(shop.getShopConfig().getOpenWPoint());
        return shopHeadDto;
    }

    public List<ShopDto> toDtoList(List<Shop> shopList) {
        if (shopList == null) {
            return null;
        }
        List<ShopDto> list = new ArrayList<>();
        for (Shop shop : shopList) {
            list.add(toDto(shop));
        }
        return list;
    }

    public ShopDto toSimpleDto(Shop shop) {
        if (shop == null) {
            return null;
        }
        ShopDto shopDto = new ShopDto();
        ShopConfig shopConfig = shop.getShopConfig();
        double benefitRate;
        if (shopConfig == null) {
            benefitRate = 0.0;
        } else {
            benefitRate = shopConfig.getBenefitRate();
        }
        shopDto.setBenefitRate(benefitRate);
        shopDto.setShopLogoImg(shop.getLogoImg());
        shopDto.setId(shop.getId());
        shopDto.setName(shop.getName());
        shopDto.setAddress(shop.getAddress());
        shopDto.setLat(shop.getLat());
        shopDto.setLng(shop.getLng());
        shopDto.setRecentSumOrder(shop.getRecentSumOrder());
        shopDto.setDistance(-1.0);
        return shopDto;
    }

    public List<ShopDto> toPageDto(List<Shop> shopList) {
        if (shopList == null) {
            return null;
        }
        List<ShopDto> list = new ArrayList<>();
        for (Shop shop : shopList) {
            list.add(toSimpleDto(shop));
        }
        return list;
    }

    public List<ShopDto> fromSimpleShop(List<ShopSimpleDto> simpleDtos) {
        List<ShopDto> shopDtoList = new ArrayList<>(simpleDtos.size());
        if (!CollectionUtils.isEmpty(simpleDtos)) {
            for (ShopSimpleDto shopSimpleDto : simpleDtos) {
                ShopDto shopDto = new ShopDto();
                BeanUtils.copyPropertiesIgnoreNull(shopSimpleDto, shopDto);
                if (shopSimpleDto.getLogoImgId() != null) {
                    shopDto.setShopLogoImg(imageService.findById(shopSimpleDto.getLogoImgId()));
                }
                shopDtoList.add(shopDto);
            }
        }
        return shopDtoList;
    }

    public List<ShopDto> fromSimpleShopLoseImg(List<ShopSimpleDto> simpleDtos) {
        List<ShopDto> shopDtoList = new ArrayList<>(simpleDtos.size());
        if (!CollectionUtils.isEmpty(simpleDtos)) {
            for (ShopSimpleDto shopSimpleDto : simpleDtos) {
                ShopDto shopDto = new ShopDto();
                BeanUtils.copyPropertiesIgnoreNull(shopSimpleDto, shopDto);
                if (shopSimpleDto.getLogoImgId() != null) {
                    Image image = new Image();
                    image.setId(shopSimpleDto.getLogoImgId());
                    shopDto.setShopLogoImg(image);
                }
                shopDtoList.add(shopDto);
            }
        }
        return shopDtoList;
    }

    public String imgListToStr(List<Image> imgList) {
        return imageMapper.imgListToStr(imgList);
    }

    public List<Image> imgIdsToList(String imgIds) {
        return imageMapper.imgIdsToList(imgIds);
    }

    public void addShopLogoImage(List<ShopDto> shopDtoList) {
        for (ShopDto shopDto : shopDtoList) {
            if (shopDto.getShopLogoImg() != null && shopDto.getShopLogoImg().getId() != 0) {
                shopDto.setShopLogoImg(imageService.findById(shopDto.getShopLogoImg().getId()));
            }
        }
    }
}
