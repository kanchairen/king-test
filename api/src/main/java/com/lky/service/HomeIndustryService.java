package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.HomeIndustryDao;
import com.lky.entity.HomeIndustry;
import com.lky.entity.Industry;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 线下店铺首页推荐的店铺行业类型
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Service
public class HomeIndustryService extends BaseService<HomeIndustry, Integer> {

    @Inject
    private HomeIndustryDao homeIndustryDao;

    @Inject
    private IndustryService industryService;

    @Override
    public BaseDao<HomeIndustry, Integer> getBaseDao() {
        return this.homeIndustryDao;
    }

    public void modify(List<HomeIndustry> homeIndustryList) {
        String[] checkFields = {"homeIndustry", "name", "industryId", "sortIndex"};
        List<HomeIndustry> homeIndustryAll = super.findAll();
        Set<Integer> idSet = homeIndustryAll.stream().map(HomeIndustry::getId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(homeIndustryList)) {
            for (HomeIndustry homeIndustry : homeIndustryList) {
                AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, checkFields,
                        homeIndustry, homeIndustry.getName(),
                        homeIndustry.getIndustryId(), homeIndustry.getSortIndex());
                Industry industry = industryService.findById(homeIndustry.getIndustryId());
                AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, new String[]{"industryId"}, industry);
                AssertUtils.isTrue(PublicResCode.PARAMS_EXCEPTION, "industryId", industry.getLevel() == IndustryService.LEVEL_TWO);
                if (homeIndustry.getId() != null) {
                    super.update(homeIndustry);
                    idSet.remove(homeIndustry.getId());
                } else {
                    super.save(homeIndustry);
                }
            }
        }
        if (!CollectionUtils.isEmpty(idSet)) {
            for (Integer id : idSet) {
                super.deleteByIds(id);
            }
        }
    }
}
