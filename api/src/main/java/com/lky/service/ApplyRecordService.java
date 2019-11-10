package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.ApplyRecordDao;
import com.lky.dto.ApplyRecordDto;
import com.lky.entity.*;
import com.lky.enums.dict.ApplyRecordDict;
import com.lky.enums.dict.RoleDict;
import com.lky.enums.dict.UserDict;
import com.lky.mapper.ApplyRecordMapper;
import com.lky.service.async.AsyncMessageService;
import com.lky.utils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static com.lky.enums.dict.ApplyRecordDict.STATE_AGREE;

/**
 * 商家申请记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/16
 */
@Service
public class ApplyRecordService extends BaseService<ApplyRecord, Integer> {

    @Inject
    private ApplyRecordDao applyRecordDao;

    @Inject
    private ApplyRecordMapper applyRecordMapper;

    @Inject
    private BaseConfigService baseConfigService;

    @Inject
    private ShopService shopService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private AsyncMessageService asyncMessageService;

    @Inject
    private PointService pointService;

    @Override
    public BaseDao<ApplyRecord, Integer> getBaseDao() {
        return this.applyRecordDao;
    }

    public ApplyRecord findByUser(User user) {
        return applyRecordDao.findByUser(user);
    }

    public ApplyRecord apply(ApplyRecordDto applyRecordDto, double amount) {
        ApplyRecord applyRecord;
        if (applyRecordDto.getId() != null) {
            applyRecord = super.findById(applyRecordDto.getId());
            BeanUtils.copyPropertiesIgnoreNull(applyRecordDto, applyRecord,
                    "id", "createTime", "amount", "sumPaidAmount", "shopId", "state", "auditRemark", "auditTime",
                    "industry", "shopLogoImg");
            applyRecord.setState(String.valueOf(ApplyRecordDict.STATE_UNPAID));
            applyRecord.setAmount(amount - applyRecord.getSumPaidAmount());
            applyRecord.setIndustry(applyRecordMapper.fromIndustryDto(applyRecordDto.getIndustryParentDto()));
            applyRecord.setShopBannerImgIds(applyRecordMapper.imgListToStr(applyRecordDto.getShopBannerImgList()));
            applyRecord.setShopLicenseImgIds(applyRecordMapper.imgListToStr(applyRecordDto.getShopLicenseImgList()));
            applyRecord.setShopLogoImg(applyRecordDto.getShopLogoImg());
            super.update(applyRecord);
        } else {
            applyRecord = applyRecordMapper.fromDto(applyRecordDto);
            applyRecord.setState(String.valueOf(ApplyRecordDict.STATE_UNPAID));
            applyRecord.setAmount(amount);
            super.save(applyRecord);
        }
        return applyRecord;
    }

    public ApplyRecord againApply(ApplyRecord applyRecord, ApplyRecordDto applyRecordDto, double amount) {
        BeanUtils.copyPropertiesIgnoreNull(applyRecordDto, applyRecord,
                "sumPaidAmount", "state", "amount", "createTime", "shopId", "auditRemark", "auditTime",
                "industry", "shopLogoImg");
        if (applyRecord.getIndustry().getId() != applyRecordDto.getIndustryParentDto().getId()) {
            applyRecord.setIndustry(applyRecordMapper.fromIndustryDto(applyRecordDto.getIndustryParentDto()));
        }
        if (amount > 0) { //需补差价支付
            applyRecord.setState(String.valueOf(ApplyRecordDict.STATE_UNPAID));
        } else { //无需补差价申请中
            amount = 0; //有可能关闭了健康店铺
            applyRecord.setState(String.valueOf(ApplyRecordDict.STATE_APPLY));
        }
        applyRecord.setShopBannerImgIds(applyRecordMapper.imgListToStr(applyRecordDto.getShopBannerImgList()));
        applyRecord.setShopLicenseImgIds(applyRecordMapper.imgListToStr(applyRecordDto.getShopLicenseImgList()));
        applyRecord.setShopLogoImg(applyRecordDto.getShopLogoImg());
        applyRecord.setAmount(amount);
        super.update(applyRecord);
        return applyRecord;
    }

    public void audit(ApplyRecord applyRecord) {

        //审核通过，同意后插入店铺
        if (STATE_AGREE.compare(applyRecord.getState())) {

            HighConfig highConfig = baseConfigService.findH();

            //第一次审核通过，店铺还未开
            if (applyRecord.getShopId() == null) {
                //店铺资料
                ShopDatum shopDatum = new ShopDatum();
                shopDatum.setLicenseImgIds(applyRecord.getShopLicenseImgIds());
                shopDatum.setOpenShopFee(highConfig.getOpenShopFee());
                shopDatum.setOpenShopExpire(DateUtils.add(new Date(), Calendar.YEAR, 1));

                //店铺配置
                ShopConfig shopConfig = new ShopConfig();
                shopConfig.setOpenShop(Boolean.TRUE);
                shopConfig.setBenefitRate(highConfig.getBenefitRateMin());

                //店铺信息
                Shop shop = new Shop();
                shop.setIndustry(applyRecord.getIndustry());
                shop.setAddress(applyRecord.getShopAddress());
                shop.setBannerImgIds(applyRecord.getShopBannerImgIds());
                shop.setContactPhone(applyRecord.getShopContactPhone());
                shop.setNotifyPhone(applyRecord.getUser().getMobile());
                shop.setLat(applyRecord.getLat());
                shop.setLng(applyRecord.getLng());
                shop.setName(applyRecord.getShopName());
                shop.setState(Boolean.TRUE);
                shop.setLogoImg(applyRecord.getShopLogoImg());
                shop.setUser(applyRecord.getUser());
                shop.setShopDatum(shopDatum);
                shop.setShopConfig(shopConfig);
                shopService.save(shop);

                applyRecord.setShopId(shop.getId());

                //更新用户角色，判断是否达到条件，设置用户角色有效期
                User user = applyRecord.getUser();

                UserRole merchantRole = new UserRole();
                merchantRole.setRole(roleService.findByCode(RoleDict.CODE_MERCHANT));
                merchantRole.setUserId(user.getId());
                merchantRole.setExpireTime(DateUtils.add(new Date(), Calendar.YEAR, 1));

                Set<UserRole> userRoleSet = user.getUserRoleSet();
                userRoleSet.add(merchantRole);
                user.setUserRoleSet(userRoleSet);
                user.setRoleType(String.valueOf(UserDict.ROLE_TYPE_MERCHANT));
                userService.update(user);
                pointService.merchantSettledAward(user, applyRecord.getSumPaidAmount(), highConfig);
            }
        }
        asyncMessageService.sendShopApplyRecord(applyRecord);
        applyRecord.setAuditTime(new Date());
        super.update(applyRecord);
    }

}
