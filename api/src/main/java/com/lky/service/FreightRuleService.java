package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.ArithUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.FreightRuleDao;
import com.lky.entity.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.lky.enums.dict.FreightTemplateDict.*;

/**
 * 运费规则
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/27
 */
@Service
public class FreightRuleService extends BaseService<FreightRule, Integer> {

    @Inject
    private FreightRuleDao freightRuleDao;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ProductService productService;

    @Override
    public BaseDao<FreightRule, Integer> getBaseDao() {
        return this.freightRuleDao;
    }

    /**
     * 运费 = 基础运费 + 续费；
     * 基础运费的计算规则：一份订单中含有多个不同产品，产品的运费分别按照不同计费方式进行分开计算，
     * 具有同一计费方式的产品的运费，先比较基础运费，取其中最大的基础运费；
     * 如果基础运费相等，则比较后面的续费，取续费最小的产品的基础运费为最终的基础运费；
     * 续费的计算规则：续费选用产品本身的续费进行计算；那些基础运费未被选中的，则基础运费所包含的部分产品加入到续费中计算运费。
     *
     * @param productList 一份订单中包含的购买产品列表，Map包括产品id和数量number
     * @param cityId      买家的收货地址所在城市ID
     * @return 返回该份订单的运费
     */
    public double calculateFreight(List<Map<String, Object>> productList, Integer cityId) {
        Map<String, List<Freight>> freightMap = new HashMap<>();
        Map<String, Integer> maxIndexMap = new HashMap<>();

        //找出作为基础运费的产品运费计算规则的索引
        for (Map<String, Object> proTemp : productList) {
            Product product = productService.findById((Integer) proTemp.get("id"));
            ProductGroup productGroup = productGroupService.findById(product.getProductGroupId());
            FreightTemplate freightTemplate = productGroup.getFreightTemplate();

            if (freightTemplate == null || CollectionUtils.isEmpty(freightTemplate.getFreightRuleList())) {
                continue;
            }

            //创建新的运费规则列表并且克隆新的运费规则对象，防止级联更新引起的数据更改
            List<FreightRule> freightRuleList = new ArrayList<>();
            freightTemplate.getFreightRuleList().forEach(freightRule ->
                    freightRuleList.add((FreightRule) freightRule.clone()));
            FreightRule defaultFreightRule = freightRuleList.remove(0);

            out:
            for (FreightRule freightRule : freightRuleList) {
                for (Area area : freightRule.getCitySet()) {
                    if (area.getId() == cityId) {
                        defaultFreightRule = freightRule;
                        break out;
                    }
                }
            }

            String priceType = freightTemplate.getPriceType();
            if (!freightMap.containsKey(priceType)) {
                freightMap.put(priceType, new ArrayList<>());
                maxIndexMap.put(priceType, 0);
            }

            List<Freight> freightList = freightMap.get(priceType);
            Freight freight = getFreight(freightList, defaultFreightRule);
            if (freight == null) {
                freight = new Freight();
                freight.freightRule = defaultFreightRule;
                freight.productNumber = (int) proTemp.get("number");
                freight.product = product;
                freightList.add(freight);
            } else {
                freight.productNumber += (int) proTemp.get("number");
            }

            maxIndexMap.put(priceType, compareMax(freightMap.get(priceType),
                    maxIndexMap.get(priceType), freight, priceType));
        }

        double basePrice = 0;
        double extraPrice = 0;
        for (String priceType : freightMap.keySet()) {
            List<Freight> freightList = freightMap.get(priceType);
            int maxIndex = maxIndexMap.get(priceType);

            //计算运费中的基础运费
            basePrice += freightList.get(maxIndex).freightRule.getBasePrice();
            for (int i = 0; i < freightList.size(); i++) {
                Freight freight = freightList.get(i);

                //基础运费未被选中的产品，基础运费包含的部分产品其运费加入到续费中计算
                if (maxIndex != i) {
                    freight.freightRule.setBase(0.0);
                }

                //计算运费中的续费
                extraPrice += calculateExtraPrice(freight, priceType);
            }
        }
        return ArithUtils.round(basePrice + extraPrice, 2);
    }

    private Freight getFreight(Collection<Freight> freights, FreightRule freightRule) {
        if (!CollectionUtils.isEmpty(freights)) {
            for (Freight freight : freights) {
                if (freight.freightRule.getId() == freightRule.getId()) {
                    return freight;
                }
            }
        }
        return null;
    }

    /**
     * @param freightList 运费计算规则列表
     * @param maxIndex    当前最大索引
     * @param freight     运费的计算规则
     * @return 最终确定下来的基础运费的索引
     */
    private Integer compareMax(List<Freight> freightList, Integer maxIndex,
                               Freight freight, String priceType) {
        int index = freightList.indexOf(freight);
        if (freightList.size() == 1 || index == maxIndex) {
            return maxIndex;
        }

        if (freightList.indexOf(freight) != maxIndex) {
            Freight maxFreight = freightList.get(maxIndex);
            if (maxFreight.freightRule.getBasePrice() < freight.freightRule.getBasePrice()) {
                return index;
            } else if (maxFreight.freightRule.getBasePrice() == freight.freightRule.getBasePrice()) {
                if (calculateExtraPrice(freight, priceType) < calculateExtraPrice(maxFreight, priceType)) {
                    return index;
                }
            }
        }
        return maxIndex;
    }

    /**
     * 计算产品的续费
     *
     * @param freight 计算运费规则及同一产品的数量
     * @return 运费中的续费部分
     */
    private double calculateExtraPrice(Freight freight, String priceType) {
        double baseOfSome = 0;

        if (PRICE_TYPE_NUM.compare(priceType)) {
            baseOfSome = freight.productNumber - freight.freightRule.getBase();
        } else if (PRICE_TYPE_WEIGHT.compare(priceType)) {
            baseOfSome = freight.productNumber * freight.product.getWeight() - freight.freightRule.getBase();
        } else if (PRICE_TYPE_VOLUME.compare(priceType)) {
            baseOfSome = freight.productNumber * freight.product.getVolume() - freight.freightRule.getBase();
        }
        int maxOverNum = (int) Math.ceil(baseOfSome / freight.freightRule.getExtra());
        return Math.max(0, maxOverNum * freight.freightRule.getExtraPrice());
    }

    private class Freight {
        FreightRule freightRule;
        int productNumber;
        Product product;
    }
}
