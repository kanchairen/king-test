package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.UserSqlDao;
import com.lky.dto.ShopDto;
import com.lky.dto.ShopSimpleDto;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.enums.code.ShopResCode;
import com.lky.global.annotation.AuthIgnore;
import com.lky.global.annotation.LoginUser;
import com.lky.global.annotation.MerchantSign;
import com.lky.global.session.TokenManager;
import com.lky.global.session.TokenModel;
import com.lky.mapper.BannerMapper;
import com.lky.mapper.ShopMapper;
import com.lky.service.*;
import com.lky.utils.BaiduMapUtil;
import io.swagger.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.service.ShopService.*;

/**
 * 店铺
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/20
 */
@RestController
@RequestMapping("api/shop")
@Api(value = "api/shop", description = "店铺")
public class MShopController extends BaseController {

    @Inject
    private ShopService shopService;

    @Inject
    private UserCollectService userCollectService;

    @Inject
    private ShopMapper shopMapper;

    @Inject
    private BannerService bannerService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ShopKindService shopKindService;

    @Inject
    private BannerMapper bannerMapper;

    @Inject
    private TokenManager tokenManager;

    @Inject
    private UserService userService;

    @Inject
    private UserSqlDao userSqlDao;

    @ApiOperation(value = "店铺列表", response = ShopDto.class, notes = "shopList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "店铺名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String name) {

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "recentSumOrder"));
        Specification<Shop> spec = shopService.buildSpec(null, name);
        Page<Shop> shopList = shopService.findAll(spec, pageable);

        List<ShopDto> shopDtoListList = shopMapper.toPageDto(shopList.getContent());
        Page<ShopDto> shopDtoPage = new PageImpl<>(shopDtoListList, pageable, shopList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopList", shopDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "店铺信息", response = ShopDto.class, notes = "shop, isCollect用户是否收藏该店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "店铺id", paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    @AuthIgnore
    public ResponseInfo get(HttpServletRequest request,
                            @PathVariable Integer id) throws UnsupportedEncodingException {
        Shop shop = shopService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, shop);
        //判断店铺是否已经过期/关闭。
        AssertUtils.isTrue(ShopResCode.SHOP_CLOSE, shopService.judgeShopExpire(shop));
        ShopDto shopDto = shopMapper.toDto(shop);
        shopDto.setSumProduct(productGroupService.findShopSumProduct(shop));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shop", shopDto);
        if ((shopDto.getOpenWPoint() != null && shopDto.getOpenWPoint())
                || (shopDto.getOpenRPoint() != null && shopDto.getOpenRPoint())) {
            if (shopDto.getShowKind()) {
                responseInfo.putData("shopKinds", shopKindService.findByShopId(shop.getId()));
            }
            if (shopDto.getShowBanner()) {
                responseInfo.putData("bannerList", bannerMapper.toDtoList(bannerService.findByShopId(shop.getId())));
            }
        }
        String authToken = super.getAppUserToken(request);
        if (authToken != null) {
            Integer userId = tokenManager.getUserIdByToken(TokenModel.TYPE_APP, authToken);
            if (userId != null) {
                responseInfo.putData("isCollect", userCollectService.isCollect(userService.findById(userId), id));
            }
        }
        return responseInfo;
    }


    @ApiOperation(value = "获取自己店铺的信息", response = ShopDto.class, notes = "shop")
    @GetMapping(value = "get")
    public ResponseInfo getSelfShop(@ApiIgnore @LoginUser User user) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(PARAMS_EXCEPTION, shop);
        ShopDto shopDto = shopMapper.toDto(shop);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        if (shop.getShopConfig() != null && shop.getShopConfig().getOpenShop() != null) {
            shopDto.setLocked(!shop.getShopConfig().getOpenShop());
        }
        responseInfo.putData("shop", shopDto);
        return responseInfo;
    }

    @ApiOperation(value = "友情店铺", response = ShopDto.class, notes = "shopList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "店铺id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "lat", value = "当前用户的经度", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lng", value = "当前用户的纬度", paramType = "query", dataType = "String")
    })
    @GetMapping(value = "list/{id}")
    public ResponseInfo list(@PathVariable Integer id,
                             @RequestParam(required = false) String lng,
                             @RequestParam(required = false) String lat) {
        Shop shop = shopService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, shop);
        int industryId = shop.getIndustry().getId();
        List<ShopSimpleDto> shopSimpleDtos = userSqlDao.findShopListByCondition(industryId, id);
        Boolean gpsIsOpen = shopService.gpsIsOpen(lat, lng); //手机app是否开启定位
        if (!CollectionUtils.isEmpty(shopSimpleDtos)) {
            //求距离当前店铺10公里以内店铺，距离保留一位小数，返回的是当前用户距离附件店铺的距离
            DecimalFormat df = new DecimalFormat(".#");
            shopSimpleDtos = shopSimpleDtos.stream()
                    .map(shopDto -> {
                        if (gpsIsOpen) {
                            shopDto.setDistance(
                                    Double.valueOf(
                                            df.format(BaiduMapUtil.getDistance(shopDto.getLat(), shopDto.getLng(), shop.getLat(), shop.getLng()))
                                    ));
                        }
                        return shopDto;
                    })
                    .filter(shopDto -> shopDto.getDistance() <= 10)
                    .sorted(Comparator.comparing(ShopSimpleDto::getDistance))
                    .map(shopDto -> {
                        if (gpsIsOpen) {
                            shopDto.setDistance(
                                    Double.valueOf(
                                            df.format(BaiduMapUtil.getDistance(shopDto.getLat(), shopDto.getLng(), lat, lng))
                                    ));
                        } else {
                            shopDto.setDistance(-1.0);
                        }
                        return shopDto;
                    })
                    .collect(Collectors.toList());
        }
        List<ShopDto> shopDtoList = shopMapper.fromSimpleShop(shopSimpleDtos);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopList", shopDtoList);
        return responseInfo;
    }

    @ApiOperation(value = "线下店铺附近商家列表", response = ShopDto.class, notes = "shopList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lat", value = "当前用户的经度", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lng", value = "当前用户的纬度", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "offline/list")
    public ResponseInfo list(@RequestParam(required = false) String lng,
                             @RequestParam(required = false) String lat,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = new PageRequest(pageNumber, pageSize);
        List<ShopSimpleDto> shopSimpleDtos = userSqlDao.findShopListByCondition(null, null);
        long total = 0;
        if (!CollectionUtils.isEmpty(shopSimpleDtos)) {
            //求附近10公里以内店铺，距离保留一位小数
            DecimalFormat df = new DecimalFormat(".#");
            shopSimpleDtos = shopSimpleDtos.stream()
                    .map(shopSimpleDto -> {
                        if (shopService.gpsIsOpen(lat, lng)) {
                            shopSimpleDto.setDistance(
                                    Double.valueOf(
                                            df.format(BaiduMapUtil.getDistance(shopSimpleDto.getLat(), shopSimpleDto.getLng(), lat, lng))
                                    ));
                        }
                        return shopSimpleDto;
                    })
                    .filter(shopDto -> shopDto.getDistance() <= 10)
                    .sorted(Comparator.comparing(ShopSimpleDto::getDistance))
                    .collect(Collectors.toList());
            total = shopSimpleDtos.size();
            shopSimpleDtos = shopSimpleDtos.stream().skip(pageNumber * pageSize).limit(pageSize).collect(Collectors.toList());
        }
        List<ShopDto> dtoList = shopMapper.fromSimpleShop(shopSimpleDtos);
        Page<ShopDto> shopDtoPage = new PageImpl<>(dtoList, pageable, total);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopList", shopDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "线下店铺按行业分类列表", response = ShopDto.class, notes = "shopList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "industryId", value = "行业", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "lat", value = "当前用户的经度", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lng", value = "当前用户的纬度", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "filed", value = "排序字段", allowableValues = "distance,recentSumOrder,benefitRate",
                    paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "desc", value = "排序类型", paramType = "query", dataType = "boolean"),
    })
    @GetMapping(value = "offline/industry/list")
    public ResponseInfo list(@RequestParam(required = false) String lng,
                             @RequestParam(required = false) String lat,
                             @RequestParam(required = false) String filed,
                             @RequestParam(required = false) Boolean desc,
                             @RequestParam Integer industryId,
                             @RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        Sort sort = null;
        if (StringUtils.isNotEmpty(filed) && desc != null) {
            AssertUtils.isInclude(PARAMS_EXCEPTION, filed, SORT_BENEFIT_RATE, SORT_RECENT_SUM_ORDER, SORT_DISTANCE);
            switch (filed) {
                case SORT_RECENT_SUM_ORDER:
                    sort = new Sort(desc ? Sort.Direction.DESC : Sort.Direction.ASC, filed);
                    break;
                case SORT_BENEFIT_RATE:
                    sort = new Sort(desc ? Sort.Direction.DESC : Sort.Direction.ASC, "shopConfig." + filed);
                    break;
                default:
            }
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, sort);
        Specification<Shop> spec = shopService.buildSpec(industryId, null);
        List<ShopDto> shopDtoList;
        List<Shop> shopList;
        Boolean loseLogoImage = Boolean.FALSE;
        long total;
        if (sort == null && desc != null && SORT_DISTANCE.equals(filed)) {
            List<ShopSimpleDto> shopSimpleDtos = userSqlDao.findShopListByCondition(industryId, null);
            shopDtoList = shopMapper.fromSimpleShopLoseImg(shopSimpleDtos);
            total = shopDtoList.size();
            loseLogoImage = Boolean.TRUE;
        } else {
            Page<Shop> shopListPage = shopService.findAll(spec, pageable);
            shopList = shopListPage.getContent();
            shopDtoList = shopMapper.toPageDto(shopList);
            total = shopListPage.getTotalElements();
        }

        if (total != 0) {
            DecimalFormat df = new DecimalFormat(".#");
            shopDtoList = shopDtoList.stream()
                    .map(shopDto -> {
                        //经纬度判断是否正确
                        if (shopService.gpsIsOpen(lat, lng)) {
                            shopDto.setDistance(
                                    Double.valueOf(
                                            df.format(BaiduMapUtil.getDistance(shopDto.getLat(), shopDto.getLng(), lat, lng))
                                    ));
                        }
                        return shopDto;
                    })
                    .sorted((s1, s2) -> {
                        if (StringUtils.isNotEmpty(filed) && desc != null && SORT_DISTANCE.equals(filed)) {
                            if (desc) {
                                return s1.getDistance() - s2.getDistance() > 0 ? -1 : 1;
                            } else {
                                return s1.getDistance() - s2.getDistance() > 0 ? 1 : -1;
                            }
                        }
                        return 0;
                    })
                    .collect(Collectors.toList());
            if (sort == null && desc != null && SORT_DISTANCE.equals(filed)) {
                shopDtoList = shopDtoList.stream().skip(pageNumber * pageSize).limit(pageSize).collect(Collectors.toList());
            }
        }
        if (loseLogoImage && !CollectionUtils.isEmpty(shopDtoList)) {
            shopMapper.addShopLogoImage(shopDtoList);
        }
        Page<ShopDto> shopDtoPage = new PageImpl<>(shopDtoList, pageable, total);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopList", shopDtoPage);
        return responseInfo;
    }


    @ApiOperation(value = "修改店铺信息", response = Shop.class, notes = "shop")
    @PutMapping("")
    @MerchantSign
    public ResponseInfo editInfo(@ApiIgnore @LoginUser User user,
                                 @ApiParam(name = "ShopDto", value = "店铺dto")
                                 @RequestBody ShopDto shopDto) {

        String[] checkFiled = {"shopDto", "benefitRate", "shopLogoImg", "contactPhone", "name"};

        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, shopDto, shopDto.getBenefitRate(),
                shopDto.getShopLogoImg(), shopDto.getContactPhone(), shopDto.getName());

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(PARAMS_IS_NULL, shop);
        shopDto.setId(shop.getId());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shop", shopMapper.toDto(shopService.modify(shop, shopDto, Boolean.TRUE)));
        return responseInfo;

    }
}
