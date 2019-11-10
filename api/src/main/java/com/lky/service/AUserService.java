package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.*;
import com.lky.dao.AUserDao;
import com.lky.dao.AUserInfoDao;
import com.lky.dao.AUserMemberDao;
import com.lky.dto.AUserOverView;
import com.lky.dto.AgentArea;
import com.lky.dto.AgentDto;
import com.lky.entity.*;
import com.lky.enums.dict.AUserDict;
import com.lky.enums.dict.AUserInfoDict;
import com.lky.mapper.AgentMapper;
import com.lky.utils.BeanUtils;
import com.lky.utils.PasswordUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.*;
import static com.lky.enums.code.UserResCode.*;
import static com.lky.enums.dict.AUserMemberDict.POSITION_CHAIRMAN;
import static com.lky.enums.dict.AreaDict.*;

/**
 * 代理商用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/21
 */
@Service
public class AUserService extends BaseService<AUser, Integer> {

    @Inject
    private AUserDao aUserDao;

    @Inject
    private AUserMemberDao aUserMemberDao;

    @Inject
    private AgentMapper agentMapper;

    @Inject
    private AreaService areaService;

    @Inject
    private AUserInfoDao aUserInfoDao;

    @Inject
    private OrdersService ordersService;

    @Inject
    private OfflineOrdersService offlineOrdersService;

    @Inject
    private AIncomeRecordService aIncomeRecordService;

    @Inject
    private AWPointRecordService awPointRecordService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Override
    public BaseDao<AUser, Integer> getBaseDao() {
        return this.aUserDao;
    }

    public AUser findByMobile(String mobile) {
        return aUserDao.findByMobile(mobile);
    }

    /**
     * 修改登录密码
     *
     * @param mobile   登录手机号
     * @param password 新密码
     * @return 登录用户id
     */
    public Integer forgetPwd(String mobile, String password) {
        AUser aUser = this.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, aUser);
        aUser.setPassword(PasswordUtils.createHash(password));
        super.update(aUser);
        return aUser.getId();
    }

    /**
     * 添加代理商
     *
     * @param agentDto 代理商Dto
     * @return 新增代理商id
     */
    public Integer add(AgentDto agentDto) {
        AUser aUser = new AUser();
        AUserInfo aUserInfo = new AUserInfo();
        AUserAsset aUserAsset = new AUserAsset();

        aUser.setPassword(PasswordUtils.createHash(AUserDict.AGENT_INIT_PASSWORD.getKey()));
        aUser.setState(agentDto.getState());
        aUser.setMobile(agentDto.getMobile().trim());
        aUser.setUsername(agentDto.getMobile().trim());
        //添加代理商信息表
        BeanUtils.copyPropertiesIgnoreNull(agentDto, aUserInfo);
        aUser.setAUserInfo(aUserInfo);

        aUserInfo.setArea(JsonUtils.objectToJson(agentDto.getArea()));
        //添加代理商资产表
        aUser.setAUserAsset(aUserAsset);

        List<AUserMember> memberList = agentDto.getMemberList();
        AssertUtils.isTrue(PARAMS_IS_NULL, !CollectionUtils.isEmpty(memberList));
        //校验是否有董事长且只有一个
        List<AUserMember> chairmanList = memberList.stream()
                .filter(aum -> POSITION_CHAIRMAN.compare(aum.getPosition()))
                .collect(Collectors.toList());

        AssertUtils.isTrue(PARAMS_IS_NULL, !CollectionUtils.isEmpty(chairmanList) && chairmanList.size() == 1);
        AUserMember chairman = chairmanList.get(0);
        //添加董事长信息
        aUser.setChairmanMobile(chairman.getMobile());
        aUser.setChairmanName(chairman.getName());
        super.save(aUser);

        //添加新代理商成员
        this.addAUserMember(memberList, aUser.getId());

        return aUser.getId();
    }

    /**
     * 根据条件搜索代理商
     *
     * @param pageNumber 页码索引
     * @param pageSize   每页显示数量
     * @param level      代理等级省/市/区
     * @param condition  代理地区，姓名，手机号
     * @return 封装代理商列表
     */
    public Page<AgentDto> findListByCondition(Integer pageNumber, Integer pageSize, String level, String condition) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Specification<AUser> spec = (root, query, cb) -> {
            Join<AUser, AUserInfo> infoJoin = root.join("aUserInfo", JoinType.INNER);
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(level)) {
                AssertUtils.isContain(PARAMS_EXCEPTION, level, AUserInfoDict.LEVEL_PROVINCE, AUserInfoDict.LEVEL_CITY, AUserInfoDict.LEVEL_DISTRICT);
                predicates.add(cb.equal(infoJoin.get("level"), level));
            }
            if (StringUtils.isNotEmpty(condition)) {
                Predicate p1 = cb.like(root.get("chairmanName"), "%" + condition.trim() + "%");
                Predicate p2 = cb.like(root.get("chairmanMobile"), "%" + condition.trim() + "%");
                Predicate p3 = cb.like(infoJoin.get("area"), "%" + condition.trim() + "%");
                predicates.add(cb.or(p1, p2, p3));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<AUser> aUserPage = super.findAll(spec, pageable);
        List<AgentDto> agentDtoList = agentMapper.toDtoList(aUserPage.getContent());
        return new PageImpl<>(agentDtoList, pageable, aUserPage.getTotalElements());
    }


    public AUser findByCellphone(String mobile) {
        return aUserDao.findByMobile(mobile);
    }

    /**
     * 编辑代理商信息
     *
     * @param sourceAUser 原代理商信息
     * @param agentDto    现代理商信息
     */
    public void modify(AUser sourceAUser, AgentDto agentDto) {
        sourceAUser.setState(agentDto.getState());
        sourceAUser.setMobile(agentDto.getMobile().trim());
        sourceAUser.setUsername(agentDto.getMobile().trim());

        AUserInfo aUserInfo = sourceAUser.getAUserInfo();
        BeanUtils.copyPropertiesIgnoreNull(agentDto, aUserInfo, "id");

        aUserInfo.setArea(JsonUtils.objectToJson(agentDto.getArea()));

        List<AUserMember> memberList = agentDto.getMemberList();
        AssertUtils.isTrue(PARAMS_IS_NULL, !CollectionUtils.isEmpty(memberList));
        //校验是否有董事长有且只有一个
        List<AUserMember> chairmanList = memberList.stream()
                .filter(aum -> POSITION_CHAIRMAN.compare(aum.getPosition()))
                .collect(Collectors.toList());
        AssertUtils.isTrue(PARAMS_IS_NULL, !CollectionUtils.isEmpty(chairmanList) && chairmanList.size() == 1);
        AUserMember chairman = chairmanList.get(0);
        //添加董事长信息
        sourceAUser.setChairmanMobile(chairman.getMobile());
        sourceAUser.setChairmanName(chairman.getName());
        super.update(sourceAUser);
        //删除原代理商成员
        List<AUserMember> sourceMemberList = aUserMemberDao.findByAUserId(sourceAUser.getId());
        AssertUtils.isTrue(SERVER_EXCEPTION, !CollectionUtils.isEmpty(sourceMemberList));
        aUserMemberDao.delete(sourceMemberList);
        //添加新代理商成员
        this.addAUserMember(memberList, sourceAUser.getId());


    }

    //添加代理商成员
    private void addAUserMember(List<AUserMember> memberList, Integer aUserId) {
        for (AUserMember memberDto : memberList) {
            String[] checkMemberFields = {"position", "unit", "manageUnit", "payAmount", "manageAmount"};
            AssertUtils.notNull(PARAMS_IS_NULL, checkMemberFields, memberDto.getPosition(), memberDto.getUnit(),
                    memberDto.getManageUnit(), memberDto.getPayAmount(), memberDto.getManageAmount());
            AUserMember aUserMember = new AUserMember();
            BeanUtils.copyPropertiesIgnoreNull(memberDto, aUserMember);
            aUserMember.setAUserId(aUserId);
            aUserMemberDao.save(aUserMember);
        }
    }

    /**
     * 校验地址
     *
     * @param level 代理级别
     * @param aUser 原代理商信息
     */
    public void verifyAddress(String level, AgentArea agentArea, AUser aUser) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"address"}, agentArea);

        Area province = agentArea.getProvince();
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"provinceId"}, province.getId());
        Area provinceDb = areaService.findById(province.getId());
        AssertUtils.isTrue(PARAMS_EXCEPTION, provinceDb != null && TYPE_PROVINCE.compare(provinceDb.getType()));
        agentArea.setProvince(provinceDb);

        Area city = agentArea.getCity();
        if (city != null) {
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"cityId"}, city.getId());
            Area cityDb = areaService.findById(city.getId());
            AssertUtils.isTrue(PARAMS_EXCEPTION, cityDb != null && TYPE_CITY.compare(cityDb.getType()));
            agentArea.setCity(cityDb);
        }
        Area district = agentArea.getDistrict();
        if (district != null) {
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"districtId"}, district.getId());
            Area districtDb = areaService.findById(district.getId());
            AssertUtils.isTrue(PARAMS_EXCEPTION, districtDb != null && TYPE_DISTRICT.compare(districtDb.getType()));
            agentArea.setDistrict(districtDb);
        }
        //代理级别和地区格式对应效验
        switch (AUserInfoDict.getEnum(level)) {
            case LEVEL_PROVINCE:
                AssertUtils.isTrue(AGENT_LEVEL_MAPPING, agentArea.getCity() == null && agentArea.getDistrict() == null);
                break;
            case LEVEL_CITY:
                AssertUtils.isTrue(AGENT_LEVEL_MAPPING, agentArea.getCity() != null && agentArea.getDistrict() == null);
                break;
            case LEVEL_DISTRICT:
                AssertUtils.isTrue(AGENT_LEVEL_MAPPING, agentArea.getCity() != null && agentArea.getDistrict() != null);
                break;
        }
        //检查该地区是否已有代理商
        AUserInfo aUserInfo = aUserInfoDao.findByArea(JsonUtils.objectToJson(agentArea));
        if (aUser != null && aUser.getAUserInfo() != null && aUserInfo != null) {
            AssertUtils.isTrue(AGENT_AREA_EXIST, aUserInfo.getArea().equals(aUser.getAUserInfo().getArea()));
        } else {
            AssertUtils.isNull(AGENT_AREA_EXIST, aUserInfo);
        }
    }

    /**
     * 查找所有开启的代理商，
     * 当前时间大于等于开始代理时间的代理商
     *
     * @return 代理商列表
     */
    public List<AUser> findActiveAgent() {
        Specification<AUser> spec = (root, query, cb) -> {
            Join<AUser, AUserInfo> infoJoin = root.join("aUserInfo", JoinType.INNER);
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("state"), AUserDict.AGENT_STATE_ACTIVE.getKey()));
            predicates.add(cb.lessThanOrEqualTo(infoJoin.get("beginAgentDate"), new Date()));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return super.findAll(spec);
    }

    /**
     * 代理商总览详情
     *
     * @param aUser 代理商
     * @return 代理商总览
     */
    public AUserOverView findOverView(AUser aUser) {
        if (aUser == null) {
            return null;
        }
        AUserOverView aUserOverView = new AUserOverView();
        AUserInfo aUserInfo = aUser.getAUserInfo();
        AUserAsset aUserAsset = aUser.getAUserAsset();
        AssertUtils.notNull(SERVER_EXCEPTION, aUserInfo);
        AssertUtils.notNull(SERVER_EXCEPTION, aUserAsset);

        BeanUtils.copyPropertiesIgnoreNull(aUser, aUserOverView);
        BeanUtils.copyPropertiesIgnoreNull(aUserInfo, aUserOverView, "id");
        BeanUtils.copyPropertiesIgnoreNull(aUserAsset, aUserOverView, "id");
        //计算待倒扣金额   待倒扣金额大于等于0
        double waitBackAmount = ArithUtils.round(aUserInfo.getAmount() - aUserInfo.getPayAmount() - aUserAsset.getSumBackAmount(), 2);
        aUserOverView.setWaitBackAmount(waitBackAmount > 0 ? waitBackAmount : 0);
        //代理区域
        AgentArea agentArea = JsonUtils.jsonToObject(aUserInfo.getArea(), AgentArea.class);
        String area = agentArea.getProvince() != null ? agentArea.getProvince().getName() : "";
        area = agentArea.getCity() != null ? area + agentArea.getCity().getName() : area;
        area = agentArea.getDistrict() != null ? area + agentArea.getDistrict().getName() : area;
        aUserOverView.setArea(area);
        //计算昨天，近7天，近30天营业额
        Date nowTime = new Date();
        Date week = DateUtils.add(nowTime, Calendar.DATE, -7);
        Date month = DateUtils.add(nowTime, Calendar.DATE, -30);
        double yesterdayConsumerAmount = wPointRecordService.sumAgentIncomeWPoint(area, null, null);
        double weekConsumerAmount = wPointRecordService.sumAgentIncomeWPoint(area, week, nowTime);
        double monthConsumerAmount = wPointRecordService.sumAgentIncomeWPoint(area, month, nowTime);
        aUserOverView.setYesterdayConsumerAmount(yesterdayConsumerAmount);
        aUserOverView.setWeekConsumerAmount(weekConsumerAmount);
        aUserOverView.setMonthConsumerAmount(monthConsumerAmount);
        //计算昨日收益金额
        double yesterdayIncomeAmount = aIncomeRecordService.findYesterdayIncome(aUser.getId());
        aUserOverView.setYesterdayIncome(yesterdayIncomeAmount);
        //计算可转化的白积
        double transWPoint = awPointRecordService.findAUserTransWPoint(aUser.getId());
        aUserOverView.setTransWPoint(transWPoint);
        return aUserOverView;
    }
}
