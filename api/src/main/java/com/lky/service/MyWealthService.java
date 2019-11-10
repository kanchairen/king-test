package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.*;
import com.lky.dao.UserAssetDao;
import com.lky.dto.DetailRecordDto;
import com.lky.dto.SurplusGrainDto;
import com.lky.dto.UserAssetDto;
import com.lky.entity.*;
import com.lky.enums.dict.BalanceRecordDict;
import com.lky.enums.dict.RPointRecordDict;
import com.lky.enums.dict.SurplusGrainRecordDict;
import com.lky.enums.dict.WPointRecordDict;
import com.lky.mapper.AssetMapper;
import com.lky.utils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.dict.BalanceRecordDict.TYPE_CONSUMER_WPOINT_CONVERT_BALANCE;
import static com.lky.enums.dict.BalanceRecordDict.TYPE_MERCHANT_WPOINT_CONVERT_BALANCE;
import static com.lky.enums.dict.RPointRecordDict.*;
import static com.lky.enums.dict.SurplusGrainRecordDict.TYPE_FROM_BALANCE;
import static com.lky.enums.dict.SurplusGrainRecordDict.TYPE_TO_BALANCE;
import static java.util.Calendar.DAY_OF_YEAR;

/**
 * 个人中心 - 我的财富
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/2
 */
@Service
public class MyWealthService extends BaseService<UserAsset, Integer> {

    @Inject
    private UserAssetDao userAssetDao;

    @Inject
    private AssetMapper assetMapper;

    @Inject
    private PointService pointService;

    @Inject
    private ShopService shopService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private RPointRecordService rPointRecordService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private ComputeService computeService;

    @Inject
    private UserService userService;

    @Inject
    private SurplusGrainRecordService surplusGrainRecordService;

    @Override
    public BaseDao<UserAsset, Integer> getBaseDao() {
        return this.userAssetDao;
    }

    public UserAssetDto getAssetInfo(User user) throws ParseException {
        UserAsset userAsset = user.getUserAsset();
        UserAssetDto userAssetDto = assetMapper.toDto(userAsset);
        userAssetDto.setUserTransWPoint(userAsset.getTransWPoint());
        Shop shop = shopService.findByUser(user);
        userAssetDto.setMerchant(shop != null);
        if (shop != null) {
            userAssetDto.setMerchantTransWPoint(userAsset.getMerchantTransWPoint());
        }
        //获取今天开始的时间，即去掉时分秒。
        SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd");
        String s = now.format(new Date());
        Date today = now.parse(s);

        SimpleSpecificationBuilder<WPointRecord> wBuilder = new SimpleSpecificationBuilder<>();
        wBuilder.add("user", SpecificationOperator.Operator.eq, user.getId());
        List<WPointRecord> wPointRecordList = wPointRecordService.findAll(wBuilder.generateSpecification());

        SimpleSpecificationBuilder<RPointRecord> rBuilder = new SimpleSpecificationBuilder<>();
        rBuilder.add("user", SpecificationOperator.Operator.eq, user.getId());
        List<RPointRecord> rPointRecordList = rPointRecordService.findAll(rBuilder.generateSpecification());

        Specification<BalanceRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user.getId()));
            predicates.add(cb.greaterThan(root.get("createTime"), today));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<BalanceRecord> balanceRecordList = balanceRecordService.findAll(spec);

        double wPointToBalance = 0;  //用户今日激励金额-G米转化成的金额
        double merchantWPointToBalance = 0;  //商家今日激励金额-G米转化成的金额
        if (!CollectionUtils.isEmpty(balanceRecordList)) {
            for (BalanceRecord balanceRecord : balanceRecordList) {
                if (balanceRecord.getCreateTime().after(today)) {
                    if (TYPE_CONSUMER_WPOINT_CONVERT_BALANCE.compare(balanceRecord.getType())) {
                        wPointToBalance += balanceRecord.getChangeBalance();
                    }
                    if (TYPE_MERCHANT_WPOINT_CONVERT_BALANCE.compare(balanceRecord.getType())) {
                        merchantWPointToBalance += balanceRecord.getChangeBalance();
                    }
                }
            }
        }

        double wPointToday = 0;   // 用户今天获得G米个数
        double mWPointToday = 0;  // 商家今天获取小米个数

        if (!CollectionUtils.isEmpty(wPointRecordList)) {
            for (WPointRecord wPointRecord : wPointRecordList) {
                double changeWPoint = wPointRecord.getChangeWPoint();
                String userType = wPointRecord.getUserType();
                if (changeWPoint > 0) {
                    if (WPointRecordDict.USER_TYPE_CONSUMER.compare(userType)) {
                        if (wPointRecord.getCreateTime().after(today)) {
                            //用户今天累计获得G米个数
                            wPointToday += changeWPoint;
                        }
                    } else if (WPointRecordDict.USER_TYPE_MERCHANT.compare(userType)) {
                        if (wPointRecord.getCreateTime().after(today)) {
                            //商家今天累计获得G米个数
                            mWPointToday += changeWPoint;
                        }
                    }
                }
            }
        }

        double rPointToday = 0;   // 用户今天获得小米个数
        double mRPointToday = 0;  // 商家今天获取小米个数
        double sumRPointGet = 0;   //用户累计获得小米个数
        double mSumRPointGet = 0;  //商家累计获得小米个数
        double rPointConvertToday = 0;  //今天通过G米转化成小米的个数
        double merchantConvertRPointToday = 0; //商家今日激励小米-G米转化成的小米
        double convertRPointToday = 0;  //今日激励小米-G米转化成的小米

        if (!CollectionUtils.isEmpty(rPointRecordList)) {
            for (RPointRecord rPointRecord : rPointRecordList) {
                double changeRPoint = rPointRecord.getChangeRPoint();
                String type = rPointRecord.getType();
                String userType = rPointRecord.getUserType();
                if (changeRPoint > 0) {
                    if (RPointRecordDict.USER_TYPE_CONSUMER.compare(userType)) {
                        if (rPointRecord.getCreateTime().after(today)) {
                            //用户今天累计获得小米个数
                            rPointToday += changeRPoint;
                            if (RPointRecordDict.TYPE_CONSUMER_CONVERT.compare(type)) {
                                convertRPointToday += changeRPoint;
                            }
                            if (TYPE_MERCHANT_CONVERT.compare(type)) {
                                merchantConvertRPointToday += changeRPoint;
                            }
                        }
                        //用户累计获得小米个数
                        sumRPointGet += changeRPoint;
                    } else if (RPointRecordDict.USER_TYPE_MERCHANT.compare(userType)) {
                        if (rPointRecord.getCreateTime().after(today)) {
                            //商家今天累计获得小米个数
                            mRPointToday += changeRPoint;
                        }
                        //商家累计获得小米个数
                        mSumRPointGet += changeRPoint;
                    }
                }
            }
        }
        userAssetDto.setConvertBalanceToday(ArithUtils.round(wPointToBalance, 2));
        //今日新增G米
        userAssetDto.setWpointToday(ArithUtils.round(wPointToday, 2));
        userAssetDto.setMerchantWPointToday(ArithUtils.round(mWPointToday, 2));
        userAssetDto.setMerchantConvertBalanceToday(ArithUtils.round(merchantWPointToBalance, 2));

        userAssetDto.setConvertRPointToday(ArithUtils.round(convertRPointToday, 2));
        userAssetDto.setMerchantConvertRPointToday(ArithUtils.round(merchantConvertRPointToday, 2));
        userAssetDto.setRpointToday(ArithUtils.round(rPointToday, 2));
        userAssetDto.setMerchantRPointToday(ArithUtils.round(mRPointToday, 2));
        userAssetDto.setMerchantRPointAddSum(ArithUtils.round(mSumRPointGet, 2));
        userAssetDto.setRpointAddSum(ArithUtils.round(sumRPointGet, 2));
        userAssetDto.setWpointConvertRPoint(ArithUtils.round(rPointConvertToday, 2));
        userAssetDto.setOpenSurplusGrain(user.getOpenSurplusGrain());
        return userAssetDto;
    }

    /**
     * 大米明细记录列表
     *
     * @param user      用户、商户
     * @param level     0全部，1收入，2支出
     * @param pageable  分页
     * @param type      类型
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 大米明细记录列表
     */
    public Page<DetailRecordDto> findBalanceRecordList(User user, Integer level, Pageable pageable,
                                                       String type, Long beginTime, Long endTime) {

        Specification<BalanceRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user.getId()));
            if (beginTime != null && endTime != null && beginTime != 0 && endTime != 0) {
                predicates.add(cb.between(root.get("createTime"), new Date(beginTime), new Date(endTime)));
            }
            if (StringUtils.isNotEmpty(type)) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (level != null && level != 0) {
                if (level == 1) {
                    predicates.add(cb.greaterThan(root.get("changeBalance"), 0));
                } else {
                    predicates.add(cb.lessThan(root.get("changeBalance"), 0));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<BalanceRecord> balanceRecordPage = balanceRecordService.findAll(spec, pageable);
        List<BalanceRecord> balanceRecordList = balanceRecordPage.getContent();
        List<DetailRecordDto> detailRecordDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(balanceRecordList)) {
            for (BalanceRecord balanceRecord : balanceRecordList) {
                DetailRecordDto detailRecordDto = new DetailRecordDto();
                detailRecordDto.setId(balanceRecord.getId());
                detailRecordDto.setType(balanceRecord.getType());
                detailRecordDto.setChange(balanceRecord.getChangeBalance());
                detailRecordDto.setCurrent(balanceRecord.getCurrentBalance());
                detailRecordDto.setCreateTime(balanceRecord.getCreateTime());
                detailRecordDto.setRemark(balanceRecord.getRemark());
                //类型转中文
                detailRecordDto.setShowType(BalanceRecordDict.getValue(balanceRecord.getType()));
                detailRecordDtoList.add(detailRecordDto);
            }
        }
        return new PageImpl<>(detailRecordDtoList, pageable, balanceRecordPage.getTotalElements());
    }


    /**
     * 用户G米明细记录列表
     *
     * @param user     用户
     * @param level    0全部，1收入，2支出
     * @param pageable 分页
     * @return 用户G米明细记录列表
     */
    public Page<DetailRecordDto> findWPointRecordList(User user, Integer level, Pageable pageable, String userType, String type) {
        SimpleSpecificationBuilder<WPointRecord> bBuilder = new SimpleSpecificationBuilder<>();
        bBuilder.add("user", SpecificationOperator.Operator.eq, user.getId());
        bBuilder.add("userType", SpecificationOperator.Operator.eq, userType);
        if (StringUtils.isNotEmpty(type)) {
            bBuilder.add("type", SpecificationOperator.Operator.eq, type);
        }
        if (level != null) {
            switch (level) {
                case 1:
                    bBuilder.add("changeWPoint", SpecificationOperator.Operator.gt, 0);
                    break;
                case 2:
                    bBuilder.add("changeWPoint", SpecificationOperator.Operator.lt, 0);
                    break;
                default:
            }
        }
        Page<WPointRecord> wPointRecordPage = wPointRecordService.findAll(bBuilder.generateSpecification(), pageable);
        List<WPointRecord> wPointRecordList = wPointRecordPage.getContent();
        List<DetailRecordDto> detailRecordDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(wPointRecordList)) {
            for (WPointRecord wPointRecord : wPointRecordList) {
                DetailRecordDto detailRecordDto = new DetailRecordDto();
                detailRecordDto.setId(wPointRecord.getId());
                detailRecordDto.setType(wPointRecord.getType());
                detailRecordDto.setChange(wPointRecord.getChangeWPoint());
                detailRecordDto.setCurrent(wPointRecord.getCurrentWPoint());
                detailRecordDto.setCreateTime(wPointRecord.getCreateTime());
                detailRecordDto.setRemark(wPointRecord.getRemark());
                //类型转中文
                if (WPointRecordDict.TYPE_CONSUMER_DIVIDE.getKey().equals(wPointRecord.getType())) {
                    detailRecordDto.setShowType(wPointRecord.getRemark());
                } else {
                    detailRecordDto.setShowType(WPointRecordDict.getValue(wPointRecord.getType()));
                }
                detailRecordDtoList.add(detailRecordDto);
            }
        }
        return new PageImpl<>(detailRecordDtoList, pageable, wPointRecordPage.getTotalElements());
    }


    /**
     * 小米明细记录列表
     *
     * @param user      用户、商户
     * @param level     0全部，1收入，2支出
     * @param pageable  分页
     * @param type      类型
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 小米明细记录列表
     */
    public Page<DetailRecordDto> findRPointRecordList(User user, Integer level, Pageable pageable, String userType,
                                                      String type, Long beginTime, Long endTime) {

        Specification<RPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("user"), user.getId()));
            if (StringUtils.isNotEmpty(userType)) {
                predicates.add(cb.equal(root.get("userType"), userType));
            }
            if (beginTime != null && endTime != null && beginTime != 0 && endTime != 0) {
                predicates.add(cb.between(root.get("createTime"), new Date(beginTime), new Date(endTime)));
            }
            if (StringUtils.isNotEmpty(type)) {
                AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_MERCHANT_CONVERT_BALANCE, TYPE_MERCHANT_OFFLINE_ORDERS,
                        TYPE_MERCHANT_ONLINE_ORDERS, TYPE_MERCHANT_CONVERT);
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (level != null && level != 0) {
                if (level == 1) {
                    predicates.add(cb.greaterThan(root.get("changeRPoint"), 0));
                } else {
                    predicates.add(cb.lessThan(root.get("changeRPoint"), 0));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Page<RPointRecord> rPointRecordPage = rPointRecordService.findAll(spec, pageable);
        List<RPointRecord> rPointRecordList = rPointRecordPage.getContent();
        List<DetailRecordDto> detailRecordDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rPointRecordList)) {
            for (RPointRecord rPointRecord : rPointRecordList) {
                DetailRecordDto detailRecordDto = new DetailRecordDto();
                detailRecordDto.setId(rPointRecord.getId());
                detailRecordDto.setType(rPointRecord.getType());
                detailRecordDto.setChange(rPointRecord.getChangeRPoint());
                detailRecordDto.setCurrent(rPointRecord.getCurrentRPoint());
                detailRecordDto.setCreateTime(rPointRecord.getCreateTime());
                detailRecordDto.setRemark(rPointRecord.getRemark());
                //类型转中文
                detailRecordDto.setShowType(RPointRecordDict.getValue(rPointRecord.getType()));
                detailRecordDtoList.add(detailRecordDto);
            }
        }
        return new PageImpl<>(detailRecordDtoList, pageable, rPointRecordPage.getTotalElements());
    }

    public void rPointConvertBalance(Shop shop, User user) {
        UserAsset merchantUserAsset = user.getUserAsset();

        //商家小米
        double merchantRPoint = merchantUserAsset.getMerchantRPoint();
        if (merchantRPoint == 0) {
            return;
        }
        //商家得到的大米
        double merchantGiveCash = computeService.merchantRPointConvertBalance(merchantRPoint);
        pointService.cashDeposit(user, merchantUserAsset, merchantGiveCash,
                String.valueOf(BalanceRecordDict.TYPE_MERCHANT_RPOINT_CONVERT_BALANCE));

        //商家小米记录
        RPointRecord merchantRPointRecord = new RPointRecord();
        merchantRPointRecord.setUser(user);
        merchantRPointRecord.setChangeRPoint(-merchantRPoint);
        merchantRPointRecord.setUserType(String.valueOf(RPointRecordDict.USER_TYPE_MERCHANT));
        merchantRPointRecord.setType(String.valueOf(RPointRecordDict.TYPE_MERCHANT_CONVERT_BALANCE));
        merchantRPointRecord.setCurrentRPoint(0);
        merchantRPointRecord.setRemark("商家手动转换全部小米到大米");
        rPointRecordService.save(merchantRPointRecord);

        merchantUserAsset.setMerchantRPoint(0);
        user.setUserAsset(merchantUserAsset);
        userService.update(user);
    }

    /**
     * 获取用户余粮公社页面数据
     *
     * @param user 用户
     * @return余粮公社Dto
     */
    public SurplusGrainDto getSurplusGrainByUser(User user) {
        SurplusGrainDto surplusGrainDto = new SurplusGrainDto();
        BeanUtils.copyPropertiesIgnoreNull(user, surplusGrainDto);
        surplusGrainDto.setBalance(user.getUserAsset().getBalance());
        //用户余粮公社数量
        double surplusGrain = user.getUserAsset().getSurplusGrain();
        surplusGrainDto.setSurplusGrain(surplusGrain);
        //获取用户最近的余粮公社收益G米
        surplusGrainDto.setYesterdayIncomeWPoint(wPointRecordService.findSurplusGrainIncome(user));
        //获取确认中的余粮公社
        double confirmSurplusGrain = 0;
        if (surplusGrain > 0) {
            confirmSurplusGrain = ArithUtils.round(surplusGrainRecordService.confirmSurplusGrain(user), 2);
            //如果确认中的余粮公社金额大于用户的余粮公社总金额，则确认中的余粮公社为用户的总余粮公社
            confirmSurplusGrain = (surplusGrain > confirmSurplusGrain ? confirmSurplusGrain : surplusGrain);
        }
        surplusGrainDto.setConfirmSurplusGrain(confirmSurplusGrain);
        surplusGrainDto.setCalculateSurplusGrain(ArithUtils.round(surplusGrain - confirmSurplusGrain, 2));
        return surplusGrainDto;
    }

    public Page<DetailRecordDto> findSurplusGrainRecordList(User user, Integer level, Pageable pageable) {
        if (level != null && level != 0) {
            String type = String.valueOf(level == 1 ? TYPE_FROM_BALANCE : TYPE_TO_BALANCE);
            return surplusGrainRecordService.findListByCondition(user, null, null, pageable, type);
        } else {
            String type = String.valueOf(WPointRecordDict.TYPE_CONSUMER_SURPLUS_GRAIN_INCOME);
            return this.findWPointRecordList(user, null, pageable, String.valueOf(WPointRecordDict.USER_TYPE_CONSUMER), type);
        }
    }

    /**
     * 大米转入到余粮公社
     *
     * @param user      用户
     * @param userAsset 用户资产
     * @param amount    转到余粮公社金额
     * @param startDay  开始计算收益时间
     */
    public void addSurplusGrain(User user, UserAsset userAsset, double amount, Integer startDay) {
        userAsset.setBalance(userAsset.getBalance() - amount);
        userAsset.setSurplusGrain(userAsset.getSurplusGrain() + amount);
        userAssetDao.saveAndFlush(userAsset);
        //增加大米明细记录
        BalanceRecord balanceRecordSource = new BalanceRecord();
        balanceRecordSource.setChangeBalance(-amount);
        balanceRecordSource.setCurrentBalance(userAsset.getBalance());
        balanceRecordSource.setUser(user);
        balanceRecordSource.setType(String.valueOf(BalanceRecordDict.TYPE_TO_SURPLUS_GRAIN));
        balanceRecordSource.setRemark("大米转到余粮公社大米减少：" + amount);
        balanceRecordService.save(balanceRecordSource);

        //增加余粮公社明细记录
        SurplusGrainRecord surplusGrainRecord = new SurplusGrainRecord();
        surplusGrainRecord.setChangeSurplusGrain(amount);
        surplusGrainRecord.setCurrentSurplusGrain(userAsset.getSurplusGrain());
        surplusGrainRecord.setUser(user);
        surplusGrainRecord.setType(String.valueOf(SurplusGrainRecordDict.TYPE_FROM_BALANCE));
        Date beginDate = DateUtils.getBeginDate(new Date(), DAY_OF_YEAR);
        surplusGrainRecord.setIncomeTime(DateUtils.add(beginDate, Calendar.DATE, startDay));
        surplusGrainRecord.setRemark("大米转入余粮公社：" + amount);
        surplusGrainRecordService.save(surplusGrainRecord);
    }

    /**
     * 余粮公社转出到大米
     *
     * @param user      用户
     * @param userAsset 用户账户
     * @param amount    转到大米金额
     */
    public void reduceSurplusGrain(User user, UserAsset userAsset, double amount) {
        userAsset.setBalance(userAsset.getBalance() + amount);
        userAsset.setSurplusGrain(userAsset.getSurplusGrain() - amount);
        userAssetDao.saveAndFlush(userAsset);
        //增加大米明细记录
        BalanceRecord balanceRecordSource = new BalanceRecord();
        balanceRecordSource.setChangeBalance(amount);
        balanceRecordSource.setCurrentBalance(userAsset.getBalance());
        balanceRecordSource.setUser(user);
        balanceRecordSource.setType(String.valueOf(BalanceRecordDict.TYPE_FROM_SURPLUS_GRAIN));
        balanceRecordSource.setRemark("余粮公社转入获得的大米：" + amount);
        balanceRecordService.save(balanceRecordSource);

        //增加余粮公社明细记录
        SurplusGrainRecord surplusGrainRecord = new SurplusGrainRecord();
        surplusGrainRecord.setChangeSurplusGrain(-amount);
        surplusGrainRecord.setCurrentSurplusGrain(userAsset.getSurplusGrain());
        surplusGrainRecord.setUser(user);
        surplusGrainRecord.setType(String.valueOf(SurplusGrainRecordDict.TYPE_TO_BALANCE));
        surplusGrainRecord.setRemark("余粮公社转到大米：" + amount);
        surplusGrainRecordService.save(surplusGrainRecord);
    }
}
