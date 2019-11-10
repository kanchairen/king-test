package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.dao.IndustryDao;
import com.lky.dto.IndustryDto;
import com.lky.entity.ApplyRecord;
import com.lky.entity.HomeIndustry;
import com.lky.entity.Industry;
import com.lky.entity.Shop;
import com.lky.mapper.IndustryMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lky.enums.code.ShopResCode.INDUSTRY_NAME_EXIST;

/**
 * 店铺行业
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@Service
public class IndustryService extends BaseService<Industry, Integer> {

    public static final int LEVEL_ONE = 1;
    public static final int LEVEL_TWO = 2;

    @Inject
    private IndustryDao industryDao;

    @Inject
    private IndustryMapper industryMapper;

    @Inject
    private ShopService shopService;

    @Inject
    private ApplyRecordService applyRecordService;

    @Inject
    private HomeIndustryService homeIndustryService;

    @Override
    public BaseDao<Industry, Integer> getBaseDao() {
        return this.industryDao;
    }

    public List<Industry> listByParentId(Integer parentId) {
        return industryDao.findByParentId(parentId);
    }

    public List<Industry> findTwo() {
        SimpleSpecificationBuilder<Industry> builder = new SimpleSpecificationBuilder<>();
        builder.add("level", SpecificationOperator.Operator.eq, LEVEL_TWO);
        return super.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "id"));
    }

    public long countByLevelAndName(int level, String name) {
        SimpleSpecificationBuilder<Industry> builder = new SimpleSpecificationBuilder<>();
        builder.add("name", SpecificationOperator.Operator.eq, name);
        builder.add("level", SpecificationOperator.Operator.eq, level);
        return super.count(builder.generateSpecification());
    }

    public void create(IndustryDto industryDto) {
        Industry industry = industryMapper.fromDto(industryDto);
        if (industry.getParentId() != null) {
            AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, industryDto.getIcon());
            Industry parentIndustry = super.findById(industry.getParentId());
            AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, parentIndustry);
            AssertUtils.isTrue(INDUSTRY_NAME_EXIST, countByLevelAndName(LEVEL_TWO, industryDto.getName()) == 0);
            industry.setLevel(LEVEL_TWO);
        } else {
            AssertUtils.isTrue(INDUSTRY_NAME_EXIST, countByLevelAndName(LEVEL_ONE, industryDto.getName()) == 0);
            industry.setLevel(LEVEL_ONE);
        }
        super.save(industry);
    }

    /**
     * 判断 行业及其子行业是否被占用
     *
     * @param industry 行业
     * @return true 占用，false未被占用
     */
    public Boolean checkUsed(Industry industry) {
        List<Industry> industryList = this.listByParentId(industry.getId());
        industryList.add(industry);
        Set<Integer> idSet = industryList.stream().map(Industry::getId).collect(Collectors.toSet());

        Specification<Shop> shopSpecification = (root, query, cb) -> root.get("industry").in(idSet);
        Specification<ApplyRecord> applyRecordSpecification = (root, query, cb) -> root.get("industry").in(idSet);
        Specification<HomeIndustry> homeIndustrySpecification = (root, query, cb) -> root.get("industryId").in(idSet);

        return shopService.count(shopSpecification) > 0
                || applyRecordService.count(applyRecordSpecification) > 0
                || homeIndustryService.count(homeIndustrySpecification) > 0;
    }
}
