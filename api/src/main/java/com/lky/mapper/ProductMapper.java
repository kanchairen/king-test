package com.lky.mapper;

import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.ProductDto;
import com.lky.dto.ProductGroupDto;
import com.lky.dto.ShopDto;
import com.lky.entity.*;
import com.lky.service.FreightTemplateService;
import com.lky.service.ShopKindService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建商品组Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/24
 */
@Mapper(componentModel = "jsr330")
public abstract class ProductMapper {

    @Inject
    private ShopKindService shopKindService;

    @Inject
    private FreightTemplateService freightTemplateService;

    @Inject
    private ImageMapper imageMapper;

    @Mappings({
            @Mapping(source = "shopKindList", target = "shopKindIds"),
            @Mapping(source = "freightTemplateId", target = "freightTemplate"),
            @Mapping(source = "showImgList", target = "showImgIds"),
            @Mapping(source = "productDtoList", target = "productList"),
            @Mapping(source = "shopDto", target = "shop", ignore = true),
            @Mapping(source = "createTime", target = "createTime", ignore = true)
    })
    public abstract ProductGroup fromGroupDto(ProductGroupDto productGroupDto);

    public List<ProductGroupDto> toPageDto(List<ProductGroup> productGroupListPage) {
        if ( productGroupListPage == null ) {
            return null;
        }

        List<ProductGroupDto> list = new ArrayList<ProductGroupDto>();
        for ( ProductGroup productGroup : productGroupListPage ) {
            list.add( this.toGroupDto( productGroup ) );
        }

        return list;
    }

    public  List<ProductGroupDto> toListDto(List<ProductGroup> productGroupListPage){

            if ( productGroupListPage == null ) {
                return null;
            }

            List<ProductGroupDto> list = new ArrayList<ProductGroupDto>();
            for ( ProductGroup productGroup : productGroupListPage ) {
                list.add( this.toGroupDtoAddDetail( productGroup ) );
            }

            return list;

    }

    @Mappings({
            @Mapping(source = "shopKindIds", target = "shopKindList"),
            @Mapping(source = "freightTemplate", target = "freightTemplateId"),
            @Mapping(source = "showImgIds", target = "showImgList"),
            @Mapping(source = "productList", target = "productDtoList"),
            @Mapping(source = "shop", target = "shopDto"),
            @Mapping(source = "detail", target = "detail", ignore = true),
    })
    public abstract ProductGroupDto toGroupDto(ProductGroup productGroup);

    @Mappings({
            @Mapping(source = "shopKindIds", target = "shopKindList"),
            @Mapping(source = "freightTemplate", target = "freightTemplateId"),
            @Mapping(source = "showImgIds", target = "showImgList"),
            @Mapping(source = "productList", target = "productDtoList"),
            @Mapping(source = "shop", target = "shopDto"),
    })
    public abstract ProductGroupDto toGroupDtoAddDetail(ProductGroup productGroup);

    abstract Product fromDto(ProductDto productDto);

    public abstract ProductDto toDto(Product product);

    public ShopDto shopToShopDto(Shop shop) {
        ShopDto shopDto = new ShopDto();
        shopDto.setId(shop.getId());
        shopDto.setName(shop.getName());
        shopDto.setAddress(shop.getAddress());
        shopDto.setShopLogoImg(shop.getLogoImg());
        shopDto.setContactQq(shop.getContactQq());
        shopDto.setContactPhone(shop.getContactPhone());
        shopDto.setNotifyPhone(shop.getNotifyPhone());
        shopDto.setOpenRPoint(shop.getShopConfig().getOpenRPoint());
        shopDto.setOpenWPoint(shop.getShopConfig().getOpenWPoint());
        return shopDto;
    }

    public String imgListToStr(List<Image> imageList) {
        return imageMapper.imgListToStr(imageList);
    }

    public List<Image> strToImgList(String imgIds) {
        return imageMapper.imgIdsToList(imgIds);
    }

    public List<Product> fromDtoList(List<ProductDto> productDtoList) {
        List<Product> productList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productDtoList)) {
            productDtoList.forEach(productDto -> productList.add(this.fromDto(productDto)));
        }
        return productList;
    }

    public List<ProductDto> toDtoList(List<Product> productList) {
        List<ProductDto> productDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productList)) {
            productList.forEach(product -> productDtoList.add(this.toDto(product)));
        }
        return productDtoList;
    }

    public FreightTemplate intToFreight(Integer freightTemplateId) {
        if (freightTemplateId != null && freightTemplateId != 0) {
            return freightTemplateService.findById(freightTemplateId);
        }
        return null;
    }

    public Integer freightToInt(FreightTemplate freightTemplate) {
        if (freightTemplate != null) {
            return freightTemplate.getId();
        }
        return null;
    }

    public String kindToStr(List<ShopKind> shopKindList) {
        if (CollectionUtils.isEmpty(shopKindList)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        shopKindList.forEach(shopKind -> {
            if (shopKind != null) {
                builder.append(",").append(shopKind.getId()).append(",");
            }
        });
        return builder.toString();
    }

    public List<ShopKind> kindToStr(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return null;
        }
        String[] shopArray = ids.split(",");
        if (CollectionUtils.isEmpty(shopArray)) {
            return null;
        }
        List<ShopKind> shopKindList = new ArrayList<>(shopArray.length);
        for (String shopId : shopArray) {
            if (StringUtils.isEmpty(shopId)) {
                continue;
            }
            ShopKind shopKind = shopKindService.findById(Integer.valueOf(shopId));
            if (shopKind == null) {
                continue;
            }
            shopKindList.add(shopKind);
        }
        return shopKindList;
    }
}
