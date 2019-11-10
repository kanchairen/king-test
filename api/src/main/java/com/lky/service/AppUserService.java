package com.lky.service;

import com.lky.commons.base.BaseDict;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dto.AuthRecordDto;
import com.lky.dto.CapitalChangeDto;
import com.lky.entity.*;
import com.lky.enums.code.MerchantResCode;
import com.lky.enums.dict.*;
import com.lky.global.constant.Constant;
import com.lky.utils.ShiroUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.OPERATE_FAIL;
import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.AssetResCode.*;
import static com.lky.enums.code.UserResCode.MOBILE_FORMAT_ERROR;
import static com.lky.enums.dict.AuthRecordDict.STATE_AGREE;
import static com.lky.enums.dict.ChangeWPointRecordDict.*;
import static com.lky.global.constant.Constant.TYPE_WPOINT_CALCULATED;
import static com.lky.global.constant.Constant.TYPE_WPOINT_NOT_CALCULATED;

/**
 * app用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/1/17
 */
@Service
@Transactional
public class AppUserService {

    @Inject
    private UserService userService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private AuthRecordService authRecordService;

    @Inject
    private ChangeWPointRecordService changeWPointRecordService;

    @Inject
    private LockWPointRecordService lockWPointRecordService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private RPointRecordService rPointRecordService;

    @Inject
    private PointService pointService;

    @Inject
    private EnvironmentService environmentService;

    public void authAudit(AuthRecord authRecord, AuthRecordDto authRecordDto, User user) {
        //更新用户认证申请记录
        authRecord.setState(authRecordDto.getState());
        authRecord.setAuditRemark(authRecordDto.getAuditRemark() == null
                ? AuthRecordDict.AUDIT_REMARK.getValue() : authRecordDto.getAuditRemark());
        authRecord.setAuditTime(new Date());
        authRecordService.update(authRecord);

        //更新用户认证状态
        user.setAuthState(authRecordDto.getState());
        user.setUpdateTime(new Date());
        if (STATE_AGREE.getKey().equals(authRecordDto.getState())) {
            user.setRealName(authRecord.getRealName());
            user.setCardNumber(authRecord.getCardNumber());
            //实名认证成功送奖励
            pointService.userRegisterAward(user);
        }
        //发送短信通知用户
        if (environmentService.executeEnv()) {
            smsLogService.sendMobileStateSms(user.getMobile(), SmsLogDict.TYPE_AUTHENTICATION, authRecordDto.getState());
        }
        userService.update(user);
    }

    public void addRecord(User user, Double number, Boolean calculated) {
        SUser sUser = ShiroUtils.getSUser();
        ChangeWPointRecord changeWPointRecord = new ChangeWPointRecord();
        changeWPointRecord.setAuditState(String.valueOf(AUDIT_STATE_APPLY));
        changeWPointRecord.setNumber(number);
        changeWPointRecord.setUser(user);
        changeWPointRecord.setType(String.valueOf(TYPE_WPOINT));
        changeWPointRecord.setOperateName(sUser.getUsername());
        changeWPointRecord.setOperateMobile(sUser.getMobile());
        changeWPointRecord.setCalculated(calculated);
        changeWPointRecordService.save(changeWPointRecord);
    }

    public void audit(User user, ChangeWPointRecord record, String auditState) {
        String type = record.getType();
        AssertUtils.isContain(OPERATE_FAIL, type, TYPE_WPOINT, TYPE_RPOINT, TYPE_LOCK_WPOINT,
                TYPE_MERCHANT_WPOINT, TYPE_MERCHANT_RPOINT, TYPE_MERCHANT_LOCK_WPOINT);
        double number = record.getNumber();
        AssertUtils.isTrue(RECHARGE_NUMBER_WRONG,
                number <= 100000000 && number >= -100000000 && number % 1 == 0 && number != 0);
        UserAsset userAsset = user.getUserAsset();
        Date date = new Date();
        ChangeWPointRecordDict targetType = ChangeWPointRecordDict.getEnum(type);
        BaseDict recordDict;
        switch (targetType) {
            case TYPE_WPOINT:
                if (number < 0) {
                    if (AUDIT_STATE_AGREE.compare(auditState)) {
                        AssertUtils.isTrue(WPOINT_NOT_ENOUGH, userAsset.getWpoint() + number >= 0);
                    }
                    recordDict = WPointRecordDict.TYPE_CONSUMER_SYS_REDUCE;
                } else {
                    recordDict = WPointRecordDict.TYPE_CONSUMER_SYS_GIVE;
                }
                if (AUDIT_STATE_AGREE.compare(auditState)) {
                    userAsset.setWpoint(userAsset.getWpoint() + number);
                    userService.update(user);
                    //添加系统赠送用户G米记录
                    WPointRecord wPointRecord = new WPointRecord();
                    wPointRecord.setUser(user);
                    wPointRecord.setType(String.valueOf(recordDict));
                    wPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER));
                    wPointRecord.setCalculated(record.getCalculated());
                    wPointRecord.setChangeWPoint(number);
                    wPointRecord.setCurrentWPoint(userAsset.getWpoint());
                    wPointRecord.setRemark(recordDict.getValue() + "：" + number);
                    wPointRecordService.save(wPointRecord);
                    //消费者两级上线得到G米分成
                    if (number > 0) {
                        wPointRecordService.userSharingWPoint(user, number);
                    }
                }
                break;

            case TYPE_MERCHANT_WPOINT:
                if (number < 0) {
                    if (AUDIT_STATE_AGREE.compare(auditState)) {
                        AssertUtils.isTrue(MERCHANT_WPOINT_NOT_ENOUGH,
                                userAsset.getMerchantWPoint() + number >= 0);
                    }
                    recordDict = WPointRecordDict.TYPE_MERCHANT_SYS_REDUCE;
                } else {
                    recordDict = WPointRecordDict.TYPE_MERCHANT_SYS_GIVE;
                }
                if (AUDIT_STATE_AGREE.compare(auditState)) {
                    userAsset.setMerchantWPoint(userAsset.getMerchantWPoint() + number);
                    userService.update(user);
                    //添加系统赠送商家G米记录
                    WPointRecord wPointRecord = new WPointRecord();
                    wPointRecord.setUser(user);
                    wPointRecord.setType(String.valueOf(recordDict));
                    wPointRecord.setRemark(recordDict.getValue() + "：" + number);
                    wPointRecord.setUserType(String.valueOf(WPointRecordDict.USER_TYPE_MERCHANT));
                    wPointRecord.setCalculated(record.getCalculated());
                    wPointRecord.setChangeWPoint(number);
                    wPointRecord.setCurrentWPoint(userAsset.getMerchantWPoint());
                    wPointRecordService.save(wPointRecord);
                }
                break;

            case TYPE_LOCK_WPOINT:
                if (number < 0) {
                    if (AUDIT_STATE_AGREE.compare(auditState)) {
                        AssertUtils.isTrue(LOCK_WPOINT_NOT_ENOUGH,
                                userAsset.getLockWPoint() + number >= 0);
                    }
                    recordDict = LockWPointRecordDict.TYPE_CONSUMER_SYS_REDUCE;
                } else {
                    recordDict = LockWPointRecordDict.TYPE_CONSUMER_SYS_GIVE;
                }
                if (AUDIT_STATE_AGREE.compare(auditState)) {
                    userAsset.setLockWPoint(userAsset.getLockWPoint() + number);
                    userService.update(user);
                    //添加系统赠送用户存量G米记录
                    LockWPointRecord lockWPointRecord = new LockWPointRecord();
                    lockWPointRecord.setUser(user);
                    lockWPointRecord.setType(String.valueOf(recordDict));
                    lockWPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_CONSUMER));
                    lockWPointRecord.setCalculated(record.getCalculated());
                    lockWPointRecord.setChangeLockWPoint(number);
                    lockWPointRecord.setCurrentLockWPoint(userAsset.getLockWPoint());
                    lockWPointRecord.setRemark(recordDict.getValue() + "：" + number);
                    lockWPointRecordService.save(lockWPointRecord);
                }
                break;

            case TYPE_MERCHANT_LOCK_WPOINT:
                if (number < 0) {
                    if (AUDIT_STATE_AGREE.compare(auditState)) {
                        AssertUtils.isTrue(MERCHANT_LOCK_WPOINT_NOT_ENOUGH,
                                userAsset.getMerchantLockWPoint() + number >= 0);
                    }
                    recordDict = LockWPointRecordDict.TYPE_MERCHANT_SYS_REDUCE;
                } else {
                    recordDict = LockWPointRecordDict.TYPE_MERCHANT_SYS_GIVE;
                }
                if (AUDIT_STATE_AGREE.compare(auditState)) {
                    userAsset.setMerchantLockWPoint(userAsset.getMerchantLockWPoint() + number);
                    userService.update(user);
                    //添加系统赠送商家存量G米记录
                    LockWPointRecord lockWPointRecord = new LockWPointRecord();
                    lockWPointRecord.setUser(user);
                    lockWPointRecord.setType(String.valueOf(recordDict));
                    lockWPointRecord.setUserType(String.valueOf(LockWPointRecordDict.USER_TYPE_MERCHANT));
                    lockWPointRecord.setCalculated(record.getCalculated());
                    lockWPointRecord.setChangeLockWPoint(number);
                    lockWPointRecord.setCurrentLockWPoint(userAsset.getMerchantLockWPoint());
                    lockWPointRecord.setRemark(recordDict.getValue() + "：" + number);
                    lockWPointRecordService.save(lockWPointRecord);
                }
                break;

            case TYPE_RPOINT:
                if (number < 0) {
                    if (AUDIT_STATE_AGREE.compare(auditState)) {
                        AssertUtils.isTrue(RED_POINT_NOT_ENOUGH, userAsset.getRpoint() + number >= 0);
                    }
                    recordDict = RPointRecordDict.TYPE_CONSUMER_SYS_REDUCE;
                } else {
                    recordDict = RPointRecordDict.TYPE_CONSUMER_SYS_GIVE;
                }
                if (AUDIT_STATE_AGREE.compare(auditState)) {
                    userAsset.setRpoint(userAsset.getRpoint() + number);
                    userService.update(user);
                    //添加系统赠送用户小米记录
                    RPointRecord rPointRecord = new RPointRecord();
                    rPointRecord.setUser(user);
                    rPointRecord.setType(String.valueOf(recordDict));
                    rPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_CONSUMER));
                    rPointRecord.setChangeRPoint(number);
                    rPointRecord.setCurrentRPoint(userAsset.getRpoint());
                    rPointRecord.setRemark(recordDict.getValue() + "：" + number);
                    rPointRecordService.save(rPointRecord);
                }
                break;

            case TYPE_MERCHANT_RPOINT:
                if (number < 0) {
                    if (AUDIT_STATE_AGREE.compare(auditState)) {
                        AssertUtils.isTrue(MERCHANT_RPOINT_NOT_ENOUGH, userAsset.getMerchantRPoint() + number >= 0);
                    }
                    recordDict = RPointRecordDict.TYPE_MERCHANT_SYS_REDUCE;
                } else {
                    recordDict = RPointRecordDict.TYPE_MERCHANT_SYS_GIVE;
                }
                if (AUDIT_STATE_AGREE.compare(auditState)) {
                    userAsset.setMerchantRPoint(userAsset.getMerchantRPoint() + number);
                    userService.update(user);
                    //添加系统赠送商家小米记录
                    RPointRecord rPointRecord = new RPointRecord();
                    rPointRecord.setUser(user);
                    rPointRecord.setType(String.valueOf(recordDict));
                    rPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_MERCHANT));
                    rPointRecord.setChangeRPoint(number);
                    rPointRecord.setCurrentRPoint(userAsset.getMerchantRPoint());
                    rPointRecord.setRemark(recordDict.getValue() + "：" + number);
                    rPointRecordService.save(rPointRecord);
                }
                break;
        }
        record.setAuditState(auditState);
        record.setAuditTime(date);
        changeWPointRecordService.update(record);

    }


    public ChangeWPointRecord addChangeRecord(SUser sUser, String mobile, String type, double number) {
        //参数效验
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, mobile);
        AssertUtils.isInclude(PARAMS_EXCEPTION, type, TYPE_WPOINT_CALCULATED, TYPE_WPOINT_NOT_CALCULATED,
                Constant.TYPE_RPOINT, Constant.TYPE_LOCK_WPOINT, Constant.TYPE_MERCHANT_WPOINT,
                Constant.TYPE_MERCHANT_RPOINT, Constant.TYPE_MERCHANT_LOCK_WPOINT);
        User user = userService.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, new String[]{mobile}, user);
        //如果修改数值为负数，则账户的剩余数量要大于该数值，保证不出现负数。
        UserAsset userAsset = user.getUserAsset();
        switch (type) {
            case Constant.TYPE_WPOINT_CALCULATED:
            case Constant.TYPE_WPOINT_NOT_CALCULATED:
                AssertUtils.isTrue(WPOINT_NOT_ENOUGH, mobile, userAsset.getWpoint() + number >= 0);
                break;

            case Constant.TYPE_RPOINT:
                AssertUtils.isTrue(RED_POINT_NOT_ENOUGH, mobile, userAsset.getRpoint() + number >= 0);
                break;

            case Constant.TYPE_LOCK_WPOINT:
                AssertUtils.isTrue(LOCK_WPOINT_NOT_ENOUGH, mobile, userAsset.getLockWPoint() + number >= 0);
                break;

            case Constant.TYPE_MERCHANT_WPOINT:
                AssertUtils.isTrue(MerchantResCode.NO_MERCHANT, mobile,
                        String.valueOf(UserDict.ROLE_TYPE_MERCHANT).equals(user.getRoleType()));
                AssertUtils.isTrue(MERCHANT_WPOINT_NOT_ENOUGH, mobile, userAsset.getMerchantWPoint() + number >= 0);
                break;

            case Constant.TYPE_MERCHANT_RPOINT:
                AssertUtils.isTrue(MerchantResCode.NO_MERCHANT, mobile,
                        String.valueOf(UserDict.ROLE_TYPE_MERCHANT).equals(user.getRoleType()));
                AssertUtils.isTrue(MERCHANT_RPOINT_NOT_ENOUGH, mobile, userAsset.getMerchantRPoint() + number >= 0);
                break;

            case Constant.TYPE_MERCHANT_LOCK_WPOINT:
                AssertUtils.isTrue(MerchantResCode.NO_MERCHANT, mobile,
                        String.valueOf(UserDict.ROLE_TYPE_MERCHANT).equals(user.getRoleType()));
                AssertUtils.isTrue(MERCHANT_LOCK_WPOINT_NOT_ENOUGH, mobile, userAsset.getMerchantLockWPoint() + number >= 0);
                break;
        }
        ChangeWPointRecord changeWPointRecord = new ChangeWPointRecord();
        changeWPointRecord.setAuditState(String.valueOf(AUDIT_STATE_APPLY));
        changeWPointRecord.setNumber(number);
        changeWPointRecord.setUser(user);
        if (Constant.TYPE_WPOINT_CALCULATED.equals(type) || Constant.TYPE_WPOINT_NOT_CALCULATED.equals(type)) {
            changeWPointRecord.setType(String.valueOf(TYPE_WPOINT));
        } else {
            changeWPointRecord.setType(ChangeWPointRecordDict.getKey(type));
        }
        changeWPointRecord.setOperateName(sUser.getUsername());
        changeWPointRecord.setOperateMobile(sUser.getMobile());
        changeWPointRecord.setCalculated(Constant.TYPE_WPOINT_CALCULATED.equals(type));
        return changeWPointRecord;
    }

    /**
     * 批量增减用户账户G米，小米，存量
     *
     * @param changeDtoList 资金修改list
     */
    public void addBatchRecord(List<CapitalChangeDto> changeDtoList) {
        if (!CollectionUtils.isEmpty(changeDtoList)) {
            List<ChangeWPointRecord> recordList = new ArrayList<>(changeDtoList.size());
            SUser sUser = ShiroUtils.getSUser();
            for (CapitalChangeDto capitalChangeDto : changeDtoList) {
                recordList.add(this.addChangeRecord(sUser, capitalChangeDto.getMobile(),
                        capitalChangeDto.getType(), capitalChangeDto.getNumber()));
            }
            changeWPointRecordService.save(recordList);
        }
    }

    public void webAddChangeRecord(SUser sUser, User user, String type, Double number, Boolean calculated) {
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, user);
        AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_WPOINT, TYPE_RPOINT, TYPE_LOCK_WPOINT, TYPE_MERCHANT_WPOINT,
                TYPE_MERCHANT_RPOINT, TYPE_MERCHANT_LOCK_WPOINT);
        String mobile = user.getMobile();
        ChangeWPointRecordDict targetType = ChangeWPointRecordDict.getEnum(type);
        if (TYPE_WPOINT.getKey().equals(type)) {
            AssertUtils.isTrue(PARAMS_EXCEPTION, "calculated", calculated != null);
        } else {
            calculated = Boolean.FALSE;
        }
        //如果修改数值为负数，则账户的剩余数量要大于该数值，保证不出现负数。
        if (number < 0) {
            UserAsset userAsset = user.getUserAsset();
            switch (targetType) {
                case TYPE_WPOINT:
                    AssertUtils.isTrue(WPOINT_NOT_ENOUGH, mobile, userAsset.getWpoint() + number >= 0);
                    break;
                case TYPE_RPOINT:
                    AssertUtils.isTrue(RED_POINT_NOT_ENOUGH, mobile, userAsset.getRpoint() + number >= 0);
                    break;
                case TYPE_LOCK_WPOINT:
                    AssertUtils.isTrue(LOCK_WPOINT_NOT_ENOUGH, mobile, userAsset.getLockWPoint() + number >= 0);
                    break;
                case TYPE_MERCHANT_WPOINT:
                    AssertUtils.isTrue(MERCHANT_WPOINT_NOT_ENOUGH, mobile, userAsset.getMerchantWPoint() + number >= 0);
                    break;
                case TYPE_MERCHANT_RPOINT:
                    AssertUtils.isTrue(MERCHANT_RPOINT_NOT_ENOUGH, mobile, userAsset.getMerchantRPoint() + number >= 0);
                    break;
                case TYPE_MERCHANT_LOCK_WPOINT:
                    AssertUtils.isTrue(MERCHANT_LOCK_WPOINT_NOT_ENOUGH, mobile, userAsset.getMerchantLockWPoint() + number >= 0);
                    break;
            }
        }
        ChangeWPointRecord changeWPointRecord = new ChangeWPointRecord();
        changeWPointRecord.setAuditState(String.valueOf(AUDIT_STATE_APPLY));
        changeWPointRecord.setNumber(number);
        changeWPointRecord.setUser(user);
        changeWPointRecord.setType(type);
        changeWPointRecord.setOperateName(sUser.getUsername());
        changeWPointRecord.setOperateMobile(sUser.getMobile());
        changeWPointRecord.setCalculated(calculated);
        changeWPointRecordService.save(changeWPointRecord);
    }
}
