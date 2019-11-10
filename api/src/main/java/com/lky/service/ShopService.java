package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.ShopDao;
import com.lky.dto.ShopDto;
import com.lky.entity.Shop;
import com.lky.entity.ShopConfig;
import com.lky.entity.ShopDatum;
import com.lky.entity.User;
import com.lky.mapper.ImageMapper;
import com.lky.mapper.ShopMapper;
import com.lky.utils.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.UserResCode.MOBILE_FORMAT_ERROR;

/**
 * 店铺
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@Service
public class ShopService extends BaseService<Shop, Integer> {

    /**
     * 排序类型：距离，销量，让利）
     */
    public static final String SORT_DISTANCE = "distance";
    public static final String SORT_RECENT_SUM_ORDER = "recentSumOrder";
    public static final String SORT_BENEFIT_RATE = "benefitRate";

    @Inject
    private ShopDao shopDao;

    @Inject
    private ShopMapper shopMapper;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private ImageMapper imageMapper;

    @Override
    public BaseDao<Shop, Integer> getBaseDao() {
        return this.shopDao;
    }

    public Shop findByUser(User user) {
        return shopDao.findByUser(user);
    }

    public Shop modify(Shop shop, ShopDto shopDto, Boolean api) {

        BeanUtils.copyPropertiesIgnoreNull(shopDto, shop);
        ShopConfig shopConfig = shop.getShopConfig();
        shop.setBannerImgIds(imageMapper.imgListToStr(shopDto.getShopBannerImgList()));
        if (shopDto.getShowBanner() != null) {
            shopConfig.setShowBanner(shopDto.getShowBanner());
        }
        if (shopDto.getShowKind() != null) {
            shopConfig.setShowKind(shopDto.getShowKind());
        }
        shop.setLogoImg(shopDto.getShopLogoImg());
        shopConfig.setBenefitRate(shopDto.getBenefitRate());
        if (!api) {
            shop.getShopDatum().setLicenseImgIds(shopMapper.imgListToStr(shopDto.getShopLicenseImgList()));
        }
        if (StringUtils.isNotEmpty(shopDto.getNotifyPhone())) {
            AssertUtils.isMobile(MOBILE_FORMAT_ERROR, shopDto.getNotifyPhone());
        } else {
            shop.setNotifyPhone(null);
        }
        shop.setUpdateTime(new Date());
        return super.update(shop);
    }

    /**
     * 判断店铺是否过期
     * true：不过期 false：过期
     *
     * @param shopId 店铺id
     * @return 是否过期
     */
    public Boolean judgeShopExpire(Integer shopId) {
        Shop shop = super.findById(shopId);
        return this.judgeShopExpire(shop);
    }

    /**
     * 判断店铺是否过期且未被管理员关闭
     * true：不过期 false：过期
     *
     * @param shop 店铺
     * @return 是否过期
     */
    public Boolean judgeShopExpire(Shop shop) {
        AssertUtils.notNull(PARAMS_EXCEPTION, shop, shop.getShopDatum());
        if (shop.getShopDatum().getOpenShopExpire() == null ||
                shop.getShopDatum().getOpenShopExpire().before(new Date()) ||
                !shop.getShopConfig().getOpenShop()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 判断是否已经获取用户的定位信息
     *
     * @param lat 经度
     * @param lng 纬度
     * @return true 定位有效， 未获取到定位
     */
    public Boolean gpsIsOpen(String lat, String lng) {
        if (StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)) {
            if ("5e-324".equals(lat) || "5e-324".equals(lng)) {
                return Boolean.FALSE;
            }
            if (Math.abs(Double.parseDouble(lng)) > 0.01 || Math.abs(Double.parseDouble(lat)) > 0.01) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 构建查询条件
     *
     * @param industryId 行业id
     * @param name       店铺名称
     * @return 查询条件
     */
    public Specification<Shop> buildSpec(Integer industryId, String name) {

        return (root, query, cb) -> {
            // 设置sql链接
            Join<Shop, ShopDatum> shopDatumJoin = root.join("shopDatum", JoinType.INNER);
            Join<Shop, ShopConfig> shopConfigJoin = root.join("shopConfig", JoinType.INNER);

            List<Predicate> predicates = Lists.newArrayList();
            if (industryId != null) {
                predicates.add(cb.equal(root.get("industry"), industryId));
            }
            if (StringUtils.isNotEmpty(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }
            predicates.add(cb.notEqual(shopConfigJoin.get("openRPoint"), Boolean.TRUE));
            predicates.add(cb.notEqual(shopConfigJoin.get("openWPoint"), Boolean.TRUE));
            predicates.add(cb.greaterThanOrEqualTo(shopDatumJoin.get("openShopExpire"), new Date()));
            predicates.add(cb.equal(shopConfigJoin.get("openShop"), Boolean.TRUE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * 构建查询条件
     *
     * @param name 店铺名称
     * @return 查询条件
     */
    public Specification<Shop> buildBannerSpec(String name) {
        return (root, query, cb) -> {
            // 设置sql链接
            Join<Shop, ShopDatum> shopDatumJoin = root.join("shopDatum", JoinType.INNER);
            Join<Shop, ShopConfig> shopConfigJoin = root.join("shopConfig", JoinType.INNER);
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }
            predicates.add(cb.greaterThanOrEqualTo(shopDatumJoin.get("openShopExpire"), new Date()));
            predicates.add(cb.equal(shopConfigJoin.get("openShop"), Boolean.TRUE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * 获取红积分商城的店铺id
     *
     * @return 红积分店铺id
     */
    public Integer findRPointId() {
        Specification<Shop> spec = (root, query, cb) -> {
            // 设置sql链接
            Join<Shop, ShopConfig> shopConfigJoin = root.join("shopConfig", JoinType.INNER);
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(shopConfigJoin.get("openRPoint"), Boolean.TRUE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Shop> shopList = super.findAll(spec);
        if (!CollectionUtils.isEmpty(shopList)) {
            return shopList.get(0).getId();
        }
        return null;
    }

    /**
     * 获取白积分商城的店铺id
     *
     * @return 白积分店铺id
     */
    public Integer findWPointId() {
        Specification<Shop> spec = (root, query, cb) -> {
            // 设置sql链接
            Join<Shop, ShopConfig> shopConfigJoin = root.join("shopConfig", JoinType.INNER);
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(shopConfigJoin.get("openWPoint"), Boolean.TRUE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Shop> shopList = super.findAll(spec);
        if (!CollectionUtils.isEmpty(shopList)) {
            return shopList.get(0).getId();
        }
        return null;
    }

    /**
     * 判断店铺是否为积分店铺
     *
     * @param shopId 店铺Id
     * @return true 积分店铺， false非积分店铺
     */
    public Boolean checkPointShop(Integer shopId) {
        Shop shop = super.findById(shopId);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, shop);
        if (shop.getShopConfig().getOpenRPoint() || shop.getShopConfig().getOpenWPoint()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
