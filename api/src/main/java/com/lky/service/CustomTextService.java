package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.dao.CustomTextDao;
import com.lky.entity.CustomText;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * 自定义文本
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/9
 */
@Service
public class CustomTextService extends BaseService<CustomText, Integer> {

    @Inject
    private CustomTextDao customTextDao;

    @Override
    public BaseDao<CustomText, Integer> getBaseDao() {
        return this.customTextDao;
    }

    public void create(CustomText customText) {
        super.save(customText);
    }
}
