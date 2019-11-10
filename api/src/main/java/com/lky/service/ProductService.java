package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.ProductDao;
import com.lky.dto.ProductGroupDto;
import com.lky.entity.*;
import com.lky.mapper.ProductMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.dict.ProductGroupDict.*;
import static com.lky.service.ProductGroupService.*;

/**
 * 商品管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/21
 */
@Service
public class ProductService extends BaseService<Product, Integer> {

    @Inject
    private ProductDao productDao;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ProductMapper productMapper;

    @Inject
    private CategoryService categoryService;

    @Override
    public BaseDao<Product, Integer> getBaseDao() {
        return this.productDao;
    }

    public Page<ProductGroupDto> findByShopIdAndState(Integer shopId, Integer pageNumber, Integer pageSize, String name,
                                                      String auditState, String sellState) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));

        SimpleSpecificationBuilder<ProductGroup> builder = new SimpleSpecificationBuilder<>();
        builder.add("shop", SpecificationOperator.Operator.eq, shopId);

        if (StringUtils.isNotEmpty(name)) {
            builder.add("name", SpecificationOperator.Operator.likeAll, name.trim());
        }
        if (StringUtils.isNotEmpty(auditState)) {
            AssertUtils.isContain(PARAMS_EXCEPTION, auditState, AUDIT_STATE_WAIT, AUDIT_STATE_YES, AUDIT_STATE_NO);
            builder.add("auditState", SpecificationOperator.Operator.eq, auditState);
            builder.add("offline", SpecificationOperator.Operator.eq, Boolean.FALSE);
        }
        if (StringUtils.isNotEmpty(sellState)) {
            AssertUtils.isInclude(PARAMS_EXCEPTION, sellState, STATE_ON_SELL, STATE_SELL_OUT, STATE_OFFLINE);
            switch (sellState) {
                case STATE_ON_SELL:
                    builder.add("offline", SpecificationOperator.Operator.eq, Boolean.FALSE);
                    builder.add("auditState", SpecificationOperator.Operator.eq, AUDIT_STATE_YES.getKey());
                    break;
                case STATE_OFFLINE:
                    builder.add("offlineNumber", SpecificationOperator.Operator.gt, 0);
                    break;
                case STATE_SELL_OUT:
                    builder.add("sellOutNumber", SpecificationOperator.Operator.gt, 0);
                    break;
            }
        }
        Page<ProductGroup> productGroupListPage = productGroupService.findAll(builder.generateSpecification(), pageable);
        List<ProductGroupDto> productGroupDtoList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(productGroupListPage.getContent())) {
            for (ProductGroup productGroup : productGroupListPage.getContent()) {
                ProductGroupDto productGroupDto = productMapper.toGroupDto(productGroup);
                productGroupDto.getProductDtoList().clear();
                if (!CollectionUtils.isEmpty(productGroup.getProductList())) {
                    for (Product p : productGroup.getProductList()) {
                        if (StringUtils.isNotEmpty(sellState)) {
                            switch (sellState) {
                                case STATE_ON_SELL:
                                    if (!p.getOffline()) {
                                        productGroupDto.getProductDtoList().add(productMapper.toDto(p));
                                    }
                                    break;
                                case STATE_OFFLINE:
                                    if (p.getOffline()) {
                                        productGroupDto.getProductDtoList().add(productMapper.toDto(p));
                                    }
                                    break;
                                case STATE_SELL_OUT:
                                    if (p.getStock() <= 0) {
                                        productGroupDto.getProductDtoList().add(productMapper.toDto(p));
                                    }
                                    break;
                            }
                        }
                        if (StringUtils.isNotEmpty(auditState)) {
                            if (!p.getOffline()) {
                                productGroupDto.getProductDtoList().add(productMapper.toDto(p));
                            }
                        }
                    }
                }
                productGroupDtoList.add(productGroupDto);
            }
        }
        return new PageImpl<>(productGroupDtoList, pageable, productGroupListPage.getTotalElements());

    }

    public Page<ProductGroupDto> findByCategoryId(int pageNumber, int pageSize, int categoryId, String state, Boolean desc) {
        AssertUtils.notNull(PARAMS_EXCEPTION, categoryService.findById(categoryId));
        Pageable pageable = new PageRequest(pageNumber, pageSize, this.getSort(state, desc));
        Page<ProductGroup> productGroupPage = productGroupService.findAll(
                this.buildProductGroupSpec(null, null, categoryId, null), pageable);
        List<ProductGroupDto> productGroupDtoList = productMapper.toPageDto(productGroupPage.getContent());
        return new PageImpl<>(productGroupDtoList, pageable, productGroupPage.getTotalElements());
    }

    public Page<ProductGroupDto> findByShopId(int shopId, int pageNumber, int pageSize, Integer shopKindId, String state, Boolean desc) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, getSort(state, desc));
        Page<ProductGroup> productGroupPage = productGroupService.findAll(
                this.buildProductGroupSpec(shopId, null, null, shopKindId), pageable);
        List<ProductGroupDto> productGroupDtoList = productMapper.toPageDto(productGroupPage.getContent());

        return new PageImpl<>(productGroupDtoList, pageable, productGroupPage.getTotalElements());
    }

    public Object findByName(int pageNumber, int pageSize, String name, String state, Boolean desc) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, this.getSort(state, desc));
        Page<ProductGroup> productGroupPage = productGroupService.findAll(
                this.buildProductGroupSpec(null, name, null, null), pageable);
        List<ProductGroupDto> productGroupDtoList = productMapper.toPageDto(productGroupPage.getContent());
        return new PageImpl<>(productGroupDtoList, pageable, productGroupPage.getTotalElements());
    }

    /**
     * 生成排序规则
     *
     * @param state 销量，价格， recentSumOrder price getWPoint newly
     * @param desc  是否降序
     * @return 排序规则 默认是按最近上线排序
     */
    private Sort getSort(String state, Boolean desc) {
        if (StringUtils.isNotEmpty(state) && desc != null) {
            AssertUtils.isInclude(PARAMS_EXCEPTION, state, SORT_SALE_NUM, SORT_PRICE, SORT_GET_W_POINT, SORT_NEWLY);
            switch (state) {
                case SORT_SALE_NUM:
                    return new Sort(desc ? Sort.Direction.DESC : Sort.Direction.ASC, "recentSold");
                case SORT_PRICE:
                    return new Sort(desc ? Sort.Direction.DESC : Sort.Direction.ASC, "price");
                case SORT_GET_W_POINT:
                    return new Sort(desc ? Sort.Direction.DESC : Sort.Direction.ASC, "getWPoint");
                case SORT_NEWLY:
                    return new Sort(desc ? Sort.Direction.DESC : Sort.Direction.ASC, "createTime");
                default:
            }
        }
        return new Sort(Sort.Direction.DESC, "createTime");
    }

    /**
     * 构建查询条件
     *
     * @param categoryId 分类id
     * @param name       商品名称
     * @param shopId     店铺id
     * @return 查询条件
     */
    public Specification<ProductGroup> buildProductGroupSpec(Integer shopId, String name, Integer categoryId, Integer shopKindId) {

        return (root, query, cb) -> {
            Join<ProductGroup, Shop> shopJoin = root.join("shop", JoinType.INNER);
            Join<Shop, ShopDatum> shopDatumJoin = shopJoin.join("shopDatum", JoinType.INNER);
            Join<Shop, ShopConfig> shopConfigJoin = shopJoin.join("shopConfig", JoinType.INNER);
            List<Predicate> predicates = Lists.newArrayList();
            if (shopId != null) {
                predicates.add(cb.equal(root.get("shop"), shopId));
            } else {
                predicates.add(cb.notEqual(shopConfigJoin.get("openRPoint"), Boolean.TRUE));
                predicates.add(cb.notEqual(shopConfigJoin.get("openWPoint"), Boolean.TRUE));
            }
            if (StringUtils.isNotEmpty(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category"), categoryId));
            }
            if (shopKindId != null && shopKindId != 0) {
                predicates.add(cb.like(root.get("shopKindIds"), "%," + shopKindId + ",%"));
            }
            predicates.add(cb.equal(root.get("auditState"), AUDIT_STATE_YES.getKey()));
            predicates.add(cb.equal(root.get("offline"), Boolean.FALSE));
            predicates.add(cb.greaterThanOrEqualTo(shopDatumJoin.get("openShopExpire"), new Date()));
            predicates.add(cb.equal(shopConfigJoin.get("openShop"), Boolean.TRUE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * 根据商品名称构建查询条件，包含积分商城商品
     *
     * @param name 商品名称
     * @param pointShop 是否展示积分商城商品
     * @return 查询条件
     */
    public Specification<ProductGroup> buildAllProductByName(String name, Boolean pointShop) {
        return (root, query, cb) -> {
            Join<ProductGroup, Shop> shopJoin = root.join("shop", JoinType.INNER);
            Join<Shop, ShopDatum> shopDatumJoin = shopJoin.join("shopDatum", JoinType.INNER);
            Join<Shop, ShopConfig> shopConfigJoin = shopJoin.join("shopConfig", JoinType.INNER);
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (!pointShop) {
                predicates.add(cb.notEqual(shopConfigJoin.get("openRPoint"), Boolean.TRUE));
                predicates.add(cb.notEqual(shopConfigJoin.get("openWPoint"), Boolean.TRUE));
            }
            predicates.add(cb.equal(root.get("auditState"), AUDIT_STATE_YES.getKey()));
            predicates.add(cb.equal(root.get("offline"), Boolean.FALSE));
            predicates.add(cb.greaterThanOrEqualTo(shopDatumJoin.get("openShopExpire"), new Date()));
            predicates.add(cb.equal(shopConfigJoin.get("openShop"), Boolean.TRUE));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * 获取积分商城banner中的商品列表
     *
     * @param pageNumber 页码
     * @param pageSize   每页条数
     * @param name       产品名称
     * @return 商品组Dto
     */
    public Page<ProductGroupDto> bannerProduct(int pageNumber, int pageSize, String name) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<ProductGroup> productGroupPage = productGroupService.findAll(this.buildAllProductByName(name, Boolean.TRUE), pageable);
        List<ProductGroupDto> productGroupDtoList = productMapper.toPageDto(productGroupPage.getContent());
        return new PageImpl<>(productGroupDtoList, pageable, productGroupPage.getTotalElements());
    }
}
