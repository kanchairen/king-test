package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.FreightTemplateDao;
import com.lky.entity.FreightTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.dict.FreightTemplateDict.PRICE_TYPE_VOLUME;
import static com.lky.enums.dict.FreightTemplateDict.PRICE_TYPE_WEIGHT;

/**
 * 运费模板
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/23
 */
@Service
public class FreightTemplateService extends BaseService<FreightTemplate, Integer> {

    public static final String PRICE_NUM = "num";
    public static final String PRICE_WEIGHT = "weight";
    public static final String PRICE_VOLUME = "volume";


    @Inject
    private FreightTemplateDao freightTemplateDao;

    @Override
    public BaseDao<FreightTemplate, Integer> getBaseDao() {
        return this.freightTemplateDao;
    }

    /**
     * 判断模板名称是否重复
     *
     * @param shopId 店铺id
     * @param name   物流名称
     * @return 是否重复
     */
    public Boolean repeatTitle(Integer shopId, String name) {
        SimpleSpecificationBuilder<FreightTemplate> builder = new SimpleSpecificationBuilder<>();
        builder.add("shopId", SpecificationOperator.Operator.eq, shopId);
        builder.add("name", SpecificationOperator.Operator.eq, name);
        List<FreightTemplate> templateList = super.findAll(builder.generateSpecification());
        if (CollectionUtils.isEmpty(templateList)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void checkFreightTemplate(Integer id, Double volume, Double weight) {
        if (id != null) {
            FreightTemplate template = super.findById(id);
            AssertUtils.notNull(PARAMS_EXCEPTION, template);

            if (PRICE_TYPE_VOLUME.compare(template.getPriceType())) {
                AssertUtils.isTrue(PARAMS_EXCEPTION, "volume", volume != null && volume > 0);
            } else if (PRICE_TYPE_WEIGHT.compare(template.getPriceType())) {
                AssertUtils.isTrue(PARAMS_EXCEPTION, "weight", weight != null && weight > 0);
            }
        }
    }
}
