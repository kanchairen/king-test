package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.InformationDao;
import com.lky.entity.Information;
import com.lky.enums.dict.InformationDict;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.SetResCode.BEGIN_TIME_AFTER_END_TIME;
import static com.lky.enums.dict.InformationDict.TYPE_NOTICE;

/**
 * 资讯管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/8
 */
@Service
public class InformationService extends BaseService<Information, Integer> {

    @Inject
    private InformationDao informationDao;

    @Override
    public BaseDao<Information, Integer> getBaseDao() {
        return this.informationDao;
    }

    public List<Information> findByType(String type) {
        return informationDao.findByType(type);
    }

    public void create(Information information) {
        super.save(information);
    }

    /**
     * 若设置了悬浮或是弹窗公告则检查数据库是否已存在并修改之前的为非弹窗或悬浮。
     *
     * @param information 新增或是修改的消息
     */
    public void checkPopUpAndSuspend(Information information, Integer id) {
        if (String.valueOf(TYPE_NOTICE).equals(information.getType())) {
            if (information.getPopUp() != null && information.getPopUp()) {
                SimpleSpecificationBuilder<Information> builder = new SimpleSpecificationBuilder<>();
                if (id != null) {
                    builder.add("id", SpecificationOperator.Operator.ne, information.getId());
                }
                builder.add("popUp", SpecificationOperator.Operator.eq, Boolean.TRUE);
                List<Information> informationList = super.findAll(builder.generateSpecification());
                if (!CollectionUtils.isEmpty(informationList)) {
                    for (Information temp : informationList) {
                        temp.setPopUp(Boolean.FALSE);
                        super.update(temp);
                    }
                }
            }
            if (information.getSuspend() != null && information.getSuspend()) {
                SimpleSpecificationBuilder<Information> builder = new SimpleSpecificationBuilder<>();
                if (id != null) {
                    builder.add("id", SpecificationOperator.Operator.ne, information.getId());
                }
                AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"beginTime", "endTime"} ,information.getBeginTime(), information.getEndTime());
                AssertUtils.isTrue(BEGIN_TIME_AFTER_END_TIME, information.getBeginTime().before(information.getEndTime()));
                builder.add("suspend", SpecificationOperator.Operator.eq, Boolean.TRUE);
                List<Information> informationList = super.findAll(builder.generateSpecification());
                if (!CollectionUtils.isEmpty(informationList)) {
                    for (Information temp : informationList) {
                        temp.setSuspend(Boolean.FALSE);
                        super.update(temp);
                    }
                }
            }
        }
    }

    /**
     * 获取弹窗公告和悬停公告
     *
     * @param type suspend/popUp, 悬浮公告/弹窗公告
     * @return 公告资讯
     */
    public Information findByNoticeType(String type) {
        Specification<Information> spec = (root, query, cb) -> {
          List<Predicate> predicates = Lists.newArrayList();
          predicates.add(cb.equal(root.get(type), Boolean.TRUE));
            if (String.valueOf(InformationDict.TYPE_NOTICE_SUSPEND).equals(type)) {
                Date now = new Date();
                predicates.add(cb.greaterThanOrEqualTo(root.get("endTime"), now));
                predicates.add(cb.lessThanOrEqualTo(root.get("beginTime"), now));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Information> informationList = this.findAll(spec);
        if (!CollectionUtils.isEmpty(informationList)) {
            return informationList.get(0);
        }
        return null;
    }
}
