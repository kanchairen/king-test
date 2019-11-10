package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.JsonUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.ProductGroupDao;
import com.lky.dto.AddressDto;
import com.lky.dto.ProductDto;
import com.lky.dto.ProductGroupDto;
import com.lky.entity.*;
import com.lky.enums.code.ShopResCode;
import com.lky.enums.dict.ProductGroupDict;
import com.lky.mapper.ImageMapper;
import com.lky.mapper.ProductMapper;
import com.lky.utils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.*;
import static com.lky.enums.code.ShopResCode.*;
import static com.lky.service.FreightTemplateService.PRICE_VOLUME;
import static com.lky.service.FreightTemplateService.PRICE_WEIGHT;

/**
 * 商品组管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/21
 */
@Service
public class ProductGroupService extends BaseService<ProductGroup, Integer> {

    /**
     * 产品筛选条件：在售，售完，下架  最近上新  G米数量
     */
    public static final String STATE_ON_SELL = "onSell";
    public static final String STATE_SELL_OUT = "sellOut";
    public static final String STATE_OFFLINE = "offline";
    public static final String SORT_NEWLY = "newly";
    public static final String SORT_SALE_NUM = "saleNum";
    public static final String SORT_PRICE = "price";
    public static final String SORT_GET_W_POINT = "WPoint";

    @Inject
    private ProductGroupDao productGroupDao;

    @Inject
    private FreightTemplateService freightTemplateService;

    @Inject
    private ProductMapper productGroupMapper;

    @Inject
    private ShopService shopService;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private AreaService areaService;

    @Inject
    private ReceiveAddressService receiveAddressService;

    @Inject
    private CommentService commentService;

    @Inject
    private ProductMapper productMapper;

    @Inject
    private ProductService productService;

    @Inject
    private ComputeService computeService;

    @Inject
    private ImageService imageService;

    @Inject
    private ImageMapper imageMapper;

    @Override
    public BaseDao<ProductGroup, Integer> getBaseDao() {
        return this.productGroupDao;
    }

    /**
     * 参数效验
     *
     * @param productGroupDto 商品组dto
     */
    public void checkParams(ProductGroupDto productGroupDto) {
        String[] checkField = {"name", "category", "productList", "showImgList"};

        AssertUtils.notNull(PARAMS_IS_NULL, productGroupDto);
        AssertUtils.notNull(PARAMS_IS_NULL, checkField,
                productGroupDto.getName(), productGroupDto.getCategory(),
                productGroupDto.getProductDtoList(), productGroupDto.getShowImgList());
        HighConfig highConfig = baseConfigService.findH();
        for (ProductDto productDto : productGroupDto.getProductDtoList()) {
            AssertUtils.isTrue(PARAMS_EXCEPTION, productDto.getStock() >= 0 && productDto.getPrice() >= 0
                    && productDto.getWpointPrice() >= 0 && productDto.getBenefitRate() >= 0);
            //判断商品的让利比在高级配置中的最大和最小之间
            AssertUtils.isTrue(BENEFIT_RATE, (highConfig.getBenefitRateMin() <= productDto.getBenefitRate()) &&
                    (highConfig.getBenefitRateMax() >= productDto.getBenefitRate()));
            if (productGroupDto.getFreightTemplateId() != null && productGroupDto.getFreightTemplateId() != 0) {
                freightTemplateService.checkFreightTemplate(productGroupDto.getFreightTemplateId(),
                        productDto.getVolume(), productDto.getWeight());
            }
        }
    }

    /**
     * 添加商品组
     *
     * @param shop            店铺
     * @param productGroupDto 商品组dto
     * @return 商品组id
     */
    public Integer add(Shop shop, ProductGroupDto productGroupDto) {
        AssertUtils.notNull(PARAMS_EXCEPTION, shop);
        //判断店铺是否过期
        AssertUtils.isTrue(ShopResCode.SHOP_CLOSE, shopService.judgeShopExpire(shop));
        AssertUtils.isTrue(NAME_EXIST, !this.repeatTitle(productGroupDto.getName(), shop.getId()));
        ProductGroup productGroup = productGroupMapper.fromGroupDto(productGroupDto);

        //是否为红积分店铺
        boolean rPointShop = shop.getShopConfig().getOpenRPoint();
        productGroup.setSupportRPoint(rPointShop);
        productGroup.getProductList().forEach(product -> product.setSupportRPoint(rPointShop));
        productGroup.setShop(shop);
        productGroup.setOnSellNumber(productGroup.getProductList().size());

        int specNull = 0; //效验商品规格不能同时为空
        for (Product product : productGroup.getProductList()) {
            product.setGetWPoint(computeService.consumerGiveWPoint(product.getPrice(), product.getBenefitRate()));
            product.setShopId(shop.getId());
            product.setOffline(Boolean.FALSE);
            product.setName(productGroup.getName());
            if (product.getPreviewImg() == null) {
                product.setPreviewImg(productGroupDto.getShowImgList().get(0));
            }
            if (StringUtils.isEmpty(product.getSpec())) {
                ++specNull;
            }
        }
        AssertUtils.isTrue(SPEC_IS_NULL, specNull <= 1);
        //设置审核状态为审核中
        productGroup.setAuditState(ProductGroupDict.AUDIT_STATE_WAIT.getKey());
        this.updateStateNumberAndPrice(productGroup, Boolean.TRUE);
        super.save(productGroup);
        return productGroup.getId();
    }


    /**
     * 判断是否有重名商品组
     *
     * @param name   商品组名称
     * @param shopId 店铺id
     * @return true为有重名，false为没有重名
     */
    private Boolean repeatTitle(String name, Integer shopId) {
        SimpleSpecificationBuilder<ProductGroup> builder = new SimpleSpecificationBuilder<>();
        builder.add("shop", SpecificationOperator.Operator.eq, shopId);
        builder.add("name", SpecificationOperator.Operator.eq, name);
        List<ProductGroup> productGroupList = super.findAll(builder.generateSpecification());
        if (CollectionUtils.isEmpty(productGroupList)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public Boolean changeOffline(int shopId, Boolean offline, List<Product> productList) {

        productList.forEach(product -> {
            AssertUtils.isTrue(NOT_AUTHORIZED, product.getShopId() == shopId);
            product.setOffline(offline);
            productService.update(product);
        });
        //得到涉及的商品组id,并得到商品组列表
        Map<Integer, ProductGroup> groupMap = new HashMap<>();
        productList.forEach(product -> {
            Integer groupId = product.getProductGroupId();
            if (!groupMap.containsKey(groupId)) {
                groupMap.put(groupId, super.findById(groupId));
                this.updateStateNumberAndPrice(groupMap.get(groupId), offline);
                super.update(groupMap.get(groupId));
            }
        });
        return Boolean.TRUE;
    }

    /**
     * 更新商品组的在售商品数量、已售商品数量、上线状态、价格
     *
     * @param productGroup 商品组
     * @param offline      更改商品组的审核状态 false则改为待审核
     */
    public void updateStateNumberAndPrice(ProductGroup productGroup, Boolean offline) {
        //商品下架后再上架也需要审核
        if (!offline) {
            productGroup.setAuditState(ProductGroupDict.AUDIT_STATE_WAIT.getKey());
        }
        List<Product> productList = productGroup.getProductList();
        int onSellNumber = 0;
        int sellOutNumber = 0;
        int offlineNumber = 0;
        for (Product product : productList) {
            if (product.getOffline()) {
                offlineNumber++;
            } else {
                onSellNumber++;
                if (product.getStock() == 0) {
                    sellOutNumber++;
                }
            }
        }
        productGroup.setOnSellNumber(onSellNumber);
        productGroup.setSellOutNumber(sellOutNumber);
        productGroup.setOfflineNumber(offlineNumber);

        //更新商品组下架状态
        if (productGroup.getOnSellNumber() == 0) {
            productGroup.setOffline(true);
        } else {
            productGroup.setOffline(false);
        }

        //更新商品组展示价格、G米价格、获得G米数量、小米价格
        List<Product> onlineProductList = productGroup.getProductList()
                .stream().filter(p -> !p.getOffline())
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(onlineProductList)) {
            Product product = onlineProductList.get(0);
            productGroup.setPrice(product.getPrice());
            productGroup.setWpointPrice(product.getWpointPrice());
            //可获G米，根据公式计算消费额 *（让利比/比例基数）* 100，取商品中第一个，每次修改保存商品时重新计算
            double wPoint = computeService.consumerGiveWPoint(product.getPrice(), product.getBenefitRate());
            productGroup.setGetWPoint(wPoint);
        }
    }


    /**
     * 计算商品组的显示运费
     *
     * @param productGroup 商品组
     * @param userId       用户id
     * @param lng          经度
     * @param lat          纬度
     * @return 运费
     */
    public double freightCalculation(ProductGroup productGroup, int userId, String lng, String lat) {
        if (productGroup.getFreightTemplate() == null) {
            return 0;
        }
        FreightTemplate freightTemplate = productGroup.getFreightTemplate();
        FreightRule freightRule = null;
        Area cityArea = null;
        if (StringUtils.isNotEmpty(lng) && StringUtils.isNotEmpty(lat)) {
            cityArea = areaService.findCityByLngAndLat(lng, lat);
        }
        if (cityArea == null) {
            ReceiveAddress receiveAddress = receiveAddressService.findByFirstAndUserId(userId);
            if (receiveAddress != null) {
                AddressDto addressDto = JsonUtils.jsonToObject(receiveAddress.getAddressDetail(), AddressDto.class);
                if (addressDto.getCity() != null) {
                    cityArea = addressDto.getCity();
                }
            }
        }
        List<FreightRule> freightRuleList = freightTemplate.getFreightRuleList();
        if (cityArea != null) {
            outer:
            for (FreightRule rule : freightRuleList) {
                if (CollectionUtils.isEmpty(rule.getCitySet())) {
                    for (Area area : rule.getCitySet()) {
                        if (area.getId() == cityArea.getId()) {
                            freightRule = rule;
                            break outer;
                        }
                    }
                }
            }
        }
        if (freightRule == null) {
            freightRule = freightTemplate.getFreightRuleList().get(0);
        }
        double basePrice = freightRule.getBasePrice();
        double baseType = freightRule.getBase();
        double extraType = freightRule.getExtra();
        double extraPrice = freightRule.getExtraPrice();
        switch (freightTemplate.getPriceType()) {
            case PRICE_WEIGHT:
                double productWeight = productGroup.getProductList().get(0).getWeight();
                if (productWeight <= baseType)
                    return basePrice;
                else {
                    return (int) (basePrice + ((productWeight - baseType) * extraPrice) / extraType);
                }
            case PRICE_VOLUME:
                double productVolume = productGroup.getProductList().get(0).getVolume();
                if (productVolume <= baseType) {
                    return basePrice;
                } else {
                    return (int) (basePrice + ((productVolume - baseType) * extraPrice) / extraType);
                }
            default:
                return basePrice;
        }
    }

    /**
     * 商品组的综合评分
     *
     * @param productGroupId 商品组id
     * @return 商品组综合评分
     */
    public Double evaluateCalculation(int productGroupId) {
        SimpleSpecificationBuilder<Comment> builder = new SimpleSpecificationBuilder<>();
        builder.add("productGroupId", SpecificationOperator.Operator.eq, productGroupId);
        List<Comment> commentList = commentService.findAll(builder.generateSpecification());
        if (!CollectionUtils.isEmpty(commentList)) {
            DecimalFormat df = new DecimalFormat(".#");
            int sumScore = 0;
            for (Comment comment : commentList) {
                sumScore += comment.getScore();
            }
            return Double.valueOf(df.format((double) sumScore / commentList.size()));
        }
        return 5.0;
    }

    /**
     * 编辑商品组
     *
     * @param groupDto    商品组dto
     * @param sourceGroup 原商品组
     */
    public void modify(ProductGroupDto groupDto, ProductGroup sourceGroup) {
        //商品组重名效验
        if (!Objects.equals(groupDto.getName(), sourceGroup.getName())) {
            AssertUtils.isTrue(NAME_EXIST, !this.repeatTitle(groupDto.getName(), sourceGroup.getShop().getId()));
            sourceGroup.setName(groupDto.getName());
        }
        AssertUtils.isTrue(PRODUCT_LESS, this.checkDeleted(groupDto, sourceGroup));
        sourceGroup.setDetail(groupDto.getDetail());
        //是否为红积分店铺
        sourceGroup.setShopKindIds(productMapper.kindToStr(groupDto.getShopKindList()));
        sourceGroup.setFreightTemplate(productMapper.intToFreight(groupDto.getFreightTemplateId()));
        sourceGroup.setShowImgIds(productMapper.imgListToStr(groupDto.getShowImgList()));
        sourceGroup.setCategory(groupDto.getCategory());
        //更新产品
        List<Product> productList = sourceGroup.getProductList();
        productList.clear();
        groupDto.getProductDtoList().forEach(productDto ->
                productList.add(this.modifyProduct(productDto, sourceGroup)));
        //设置审核状态
        sourceGroup.setAuditState(ProductGroupDict.AUDIT_STATE_WAIT.getKey());
        this.updateStateNumberAndPrice(sourceGroup, Boolean.FALSE);
        super.update(sourceGroup);
    }


    /**
     * 修改商品
     *
     * @param productDto  商品dto
     * @param sourceGroup 原商品dto
     * @return
     */
    private Product modifyProduct(ProductDto productDto, ProductGroup sourceGroup) {
        Product product;
        if (productDto.getId() != null && productDto.getId() != 0) {
            product = productService.findById(productDto.getId());
            AssertUtils.notNull(PARAMS_EXCEPTION, product);
            product.setUpdateTime(new Date());
        } else {
            product = new Product();
            product.setShopId(sourceGroup.getShop().getId());
            product.setCreateTime(new Date());
        }
        //拷贝商品dto信息到商品对象
        BeanUtils.copyPropertiesIgnoreNull(productDto, product);
        product.setName(sourceGroup.getName());
        if (productDto.getPreviewImg() != null) {
            product.setPreviewImg(imageService.findById(productDto.getPreviewImg().getId()));
        } else {
            if (StringUtils.isNotEmpty(sourceGroup.getShowImgIds())) {
                product.setPreviewImg(imageMapper.imgIdsToList(sourceGroup.getShowImgIds()).get(0));
            }
        }
        product.setGetWPoint(computeService.consumerGiveWPoint(productDto.getPrice(), productDto.getBenefitRate()));
        product.setSupportRPoint(sourceGroup.getSupportRPoint());
        return product;
    }

    /**
     * 添加同款商品
     *
     * @param shop           店铺
     * @param productGroup   原商品组
     * @param productDtoList 需要添加的同款商品组
     */
    public void addSame(Shop shop, ProductGroup productGroup, List<ProductDto> productDtoList) {
        productDtoList.forEach(productDto -> {
            AssertUtils.isTrue(PARAMS_EXCEPTION, productDto.getPrice() > 0 && productDto.getStock() > 0);
            Product product = new Product();
            BeanUtils.copyPropertiesIgnoreNull(productDto, product);
            product.setSupportRPoint(productGroup.getSupportRPoint());

            //判断商品的让利比在高级配置中的最大和最小之间
            HighConfig highConfig = baseConfigService.findH();
            AssertUtils.isTrue(BENEFIT_RATE, (highConfig.getBenefitRateMin() <= product.getBenefitRate()) &&
                    (highConfig.getBenefitRateMax() >= product.getBenefitRate()));
            if (productDto.getPreviewImg() != null) {
                Image previewImg = imageService.findById(productDto.getPreviewImg().getId());
                AssertUtils.notNull(PARAMS_EXCEPTION, previewImg);
                product.setPreviewImg(previewImg);
            } else {
                product.setPreviewImg(imageMapper.imgIdsToList(productGroup.getShowImgIds()).get(0));
            }
            if (productGroup.getFreightTemplate() != null) {
                freightTemplateService.checkFreightTemplate(productGroup.getFreightTemplate().getId(),
                        productDto.getVolume(), productDto.getWeight());
            }
            product.setGetWPoint(computeService.consumerGiveWPoint(product.getPrice(), product.getBenefitRate()));
            product.setName(productGroup.getName());
            product.setShopId(shop.getId());
            productGroup.getProductList().add(product);
        });
        //设置审核状态
        if (ProductGroupDict.AUDIT_STATE_YES.getKey().equals(productGroup.getAuditState())) {
            productGroup.setAuditState(ProductGroupDict.AUDIT_STATE_WAIT.getKey());
        }
        this.updateStateNumberAndPrice(productGroup, Boolean.TRUE);
        super.update(productGroup);
    }


    /**
     * 根据店铺内部分类获取商品组
     *
     * @param shopId   店铺id
     * @param kindId   分类id
     * @param name     商品名称
     * @param pageable 分页
     * @return 商品组列表
     */
    public Page<ProductGroupDto> findListByKind(int shopId, int kindId, String name, Pageable pageable) {
        SimpleSpecificationBuilder<ProductGroup> builder = new SimpleSpecificationBuilder<>();
        builder.add("shop", SpecificationOperator.Operator.eq, shopId);
        builder.add("shopKindIds", SpecificationOperator.Operator.likeAll, "," + kindId + ",");
        if (!StringUtils.isEmpty(name)) {
            builder.add("name", SpecificationOperator.Operator.likeAll, name.trim());
        }
        Page<ProductGroup> productGroupPage = super.findAll(builder.generateSpecification(), pageable);
        List<ProductGroupDto> productGroupDtoList = productMapper.toListDto(productGroupPage.getContent());

        return new PageImpl<>(productGroupDtoList, pageable, productGroupPage.getTotalElements());

    }

    /**
     * 获取店铺的在售商品组数量
     *
     * @param shop 店铺
     * @return 店铺在售商品组数量
     */
    public int findShopSumProduct(Shop shop) {
        Specification<ProductGroup> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("shop"), shop.getId()));
            predicates.add(cb.equal(root.get("auditState"), ProductGroupDict.AUDIT_STATE_YES.getKey()));
            predicates.add(cb.isFalse(root.get("offline")));
//            predicates.add(cb.greaterThan(root.get("onSellNumber"), 0));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return (int) super.count(spec);
    }

    /**
     * 判断商品是否有被删除
     *
     * @param groupDto    商品组dto
     * @param sourceGroup 原商品组dto
     * @return true 没有少商品，false 异常情况
     */
    private Boolean checkDeleted(ProductGroupDto groupDto, ProductGroup sourceGroup) {
        List<Product> productList = sourceGroup.getProductList();
        List<ProductDto> productDtoList = groupDto.getProductDtoList();
        if (!CollectionUtils.isEmpty(productList)) {
            if (CollectionUtils.isEmpty(productDtoList)) {
                return Boolean.FALSE;
            }
            int count = 0;
            for (Product product : productList) {
                for (ProductDto productDto : productDtoList) {
                    if (productDto.getId() != null && productDto.getId() == product.getId()) {
                        count++;
                        break;
                    }
                }
            }
            if (count == productList.size()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
