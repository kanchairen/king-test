package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.AuthRecordDao;
import com.lky.dto.AuthRecordDto;
import com.lky.entity.AuthRecord;
import com.lky.entity.User;
import com.lky.enums.dict.AuthRecordDict;
import com.lky.mapper.AuthRecordMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.lky.enums.code.UserResCode.AUTH_CARD_NUMBER_EXIST;
import static com.lky.enums.code.UserResCode.AUTH_HAS_AGREE;
import static com.lky.enums.dict.AuthRecordDict.STATE_AGREE;
import static com.lky.enums.dict.AuthRecordDict.STATE_APPLY;

/**
 * 实名认证申请记录Service
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-8
 */
@Service
public class AuthRecordService extends BaseService<AuthRecord, Integer> {

    @Inject
    private AuthRecordDao authRecordDao;

    @Inject
    private AuthRecordMapper authRecordMapper;

    @Inject
    private UserService userService;

    @Override
    public BaseDao<AuthRecord, Integer> getBaseDao() {
        return this.authRecordDao;
    }

    public AuthRecord findByUser(User user) {
        return authRecordDao.findByUser(user);
    }

    /**
     * 实名认证申请
     *
     * @param authRecordDto 实名认证信息
     * @return 申请记录
     */
    public AuthRecord authApply(User user, AuthRecordDto authRecordDto) {
        AuthRecord authRecord = findByUser(user);
        String state = STATE_APPLY.getKey();
        String cardNumber = authRecordDto.getCardNumber();
        //效验一张身份证只能绑定一个GNC帐号
        AssertUtils.isTrue(AUTH_CARD_NUMBER_EXIST, CollectionUtils.isEmpty(this.findByCardNumberApply(cardNumber, user)));
        if (authRecord == null) {
            authRecord = authRecordMapper.fromDto(authRecordDto);
            authRecord.setState(state);
            super.save(authRecord);
        } else {
            AssertUtils.isTrue(AUTH_HAS_AGREE, !STATE_AGREE.getKey().equals(authRecord.getState()));
            authRecord.setState(state);
            authRecord.setRealName(authRecordDto.getRealName());
            authRecord.setCardNumber(cardNumber);
            authRecord.setAuthImgIds(authRecordMapper.imgListToStr(authRecordDto.getAuthImgList()));
            authRecord.setRemark(authRecordDto.getRemark());
            authRecord.setUpdateTime(new Date());
            super.update(authRecord);
        }

        //更新用户实名认证状态
        user.setAuthState(state);
        user.setUpdateTime(new Date());
        userService.update(user);

        return authRecord;
    }

    public List<AuthRecord> findByCardNumberApply(String cardNumber, User user) {
        SimpleSpecificationBuilder<AuthRecord> builder = new SimpleSpecificationBuilder<>();
        builder.add("cardNumber", SpecificationOperator.Operator.eq, cardNumber);
        builder.add("user", SpecificationOperator.Operator.ne, user);
        builder.add("state", SpecificationOperator.Operator.ne, String.valueOf(AuthRecordDict.STATE_REFUSE));
        Pageable pageable = new PageRequest(0, 1);
        return authRecordDao.findAll(builder.generateSpecification(), pageable).getContent();
    }

    public List<AuthRecord> findByCardNumberAudit(String cardNumber) {
        SimpleSpecificationBuilder<AuthRecord> builder = new SimpleSpecificationBuilder<>();
        builder.add("cardNumber", SpecificationOperator.Operator.eq, cardNumber);
        builder.add("state", SpecificationOperator.Operator.eq, String.valueOf(AuthRecordDict.STATE_AGREE));
        Pageable pageable = new PageRequest(0, 1);
        return authRecordDao.findAll(builder.generateSpecification(), pageable).getContent();
    }
}
