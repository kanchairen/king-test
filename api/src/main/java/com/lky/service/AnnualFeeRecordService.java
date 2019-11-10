package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.dao.AnnualFeeRecordDao;
import com.lky.entity.AnnualFeeRecord;
import com.lky.entity.HighConfig;
import com.lky.entity.Shop;
import com.lky.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static com.lky.enums.code.ShopResCode.SHOP_NOT_EXIST;
import static com.lky.enums.dict.AnnualFeeRecordDict.*;

/**
 * 缴纳年费记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/10
 */
@Service
public class AnnualFeeRecordService extends BaseService<AnnualFeeRecord, Integer> {

    @Inject
    private AnnualFeeRecordDao annualFeeRecordDao;

    @Inject
    private ShopService shopService;

    @Inject
    private BaseConfigService baseConfigService;

    @Override
    public BaseDao<AnnualFeeRecord, Integer> getBaseDao() {
        return this.annualFeeRecordDao;
    }

    public Integer create(User user, String type, Integer number) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(SHOP_NOT_EXIST, shop);

        HighConfig highConfig = baseConfigService.findH();
        double amount = 0;
        if (TYPE_SHOP.compare(type)) {
            amount = highConfig.getOpenShopFee() * number;
        }

        AnnualFeeRecord annualFeeRecord = new AnnualFeeRecord();
        annualFeeRecord.setState(String.valueOf(STATE_UNPAID));
        annualFeeRecord.setShopId(shop.getId());
        annualFeeRecord.setUser(user);
        annualFeeRecord.setNumber(number);
        annualFeeRecord.setType(type);
        annualFeeRecord.setAmount(amount);
        super.save(annualFeeRecord);
        return annualFeeRecord.getId();
    }
}
