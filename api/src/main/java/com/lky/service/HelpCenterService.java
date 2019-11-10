package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.HelpCenterDao;
import com.lky.entity.HelpCenter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * 帮助中心
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Service
public class HelpCenterService extends BaseService<HelpCenter, Integer> {

    @Inject
    private HelpCenterDao helpCenterDao;

    @Override
    public BaseDao<HelpCenter, Integer> getBaseDao() {
        return this.helpCenterDao;
    }

    public List<HelpCenter> findByType(String type) {
        return helpCenterDao.findByTypeOrderBySortIndexDesc(type);
    }

    public void create(HelpCenter helpCenter) {
        List<HelpCenter> helpCenterList = this.findByType(helpCenter.getType());
        int sortIndex = 1;
        if (!CollectionUtils.isEmpty(helpCenterList)) {
            //获取最大值加1
            sortIndex = helpCenterList.parallelStream()
                    .mapToInt(HelpCenter::getSortIndex)
                    .max()
                    .getAsInt() + 1;
        }
        helpCenter.setSortIndex(sortIndex);
        super.save(helpCenter);
    }

    public void swapPosition(HelpCenter sourceHelpCenter, HelpCenter destHelpCenter) {
        int sourceSortIndex = sourceHelpCenter.getSortIndex();
        int destSortIndex = destHelpCenter.getSortIndex();
        sourceHelpCenter.setSortIndex(destSortIndex);
        destHelpCenter.setSortIndex(sourceSortIndex);
        super.save(sourceHelpCenter);
        super.save(destHelpCenter);
    }
}
