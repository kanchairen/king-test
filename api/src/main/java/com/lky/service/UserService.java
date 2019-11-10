package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.RegexUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.UserDao;
import com.lky.entity.User;
import com.lky.entity.UserAsset;
import com.lky.entity.UserRole;
import com.lky.enums.dict.RoleDict;
import com.lky.enums.dict.UserDict;
import com.lky.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.lky.enums.code.MerchantResCode.NO_MERCHANT;
import static com.lky.enums.code.UserResCode.*;

/**
 * app用户
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/20
 */
@Service
public class UserService extends BaseService<User, Integer> {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserDao userDao;

    @Inject
    private RoleService roleService;

    @Inject
    private PointService pointService;

    @Override
    public BaseDao<User, Integer> getBaseDao() {
        return this.userDao;
    }

    public User findByMobile(String mobile) {
        return userDao.findByMobile(mobile);
    }

    public List<Integer> listUser() {
        return userDao.listUser();
    }

    public List<Integer> listMerchant() {
        return userDao.listMerchant();
    }

    public User findByRecommendCode(String recommendCode) {
        return userDao.findByRecommendCode(recommendCode);
    }

    public User findByUserAssetId(Integer userAssetId) {
        return userDao.findByUserAssetId(userAssetId);
    }

    /**
     * 用户注册
     *
     * @param mobile         登录手机号
     * @param password       登录密码
     * @param area           用户所属区域
     * @param recommendCode  推荐码
     * @param registerSource 注册来源
     */
    public Integer register(String mobile, String password, String area,
                            String recommendCode, String registerSource,
                            String registerIp, String registerAgent) {
        User user = this.findByMobile(mobile);
        AssertUtils.isNull(MOBILE_EXIST, user);

        user = new User();
        user.setUsername(mobile);
        user.setMobile(mobile);
        user.setPassword(PasswordUtils.createHash(password));
        user.setArea(area);
        if (!StringUtils.isEmpty(recommendCode)) {
            User parentUser;
            //推荐码可以是用户手机号或是其推荐码
            if (RegexUtils.isMobileNumber(recommendCode)) {
                parentUser = this.findByMobile(recommendCode);
            } else {
                parentUser = this.findByRecommendCode(recommendCode);
            }
            AssertUtils.notNull(RECOMMEND_CODE_ERROR, parentUser);

            if (!isIncludeRole(parentUser, RoleDict.CODE_AGENT)) {
                SimpleSpecificationBuilder<User> builder = new SimpleSpecificationBuilder<>();
                builder.add("parentId", SpecificationOperator.Operator.eq, parentUser.getId());
                long sumChild = super.count(builder.generateSpecification());
                if (sumChild >= 9) {
                    UserRole agentRole = new UserRole();
                    agentRole.setRole(roleService.findByCode(RoleDict.CODE_AGENT));
                    parentUser.getUserRoleSet().add(agentRole);
                    this.save(parentUser);
                }
            }
            user.setParentId(parentUser.getId());
        }
        user.setRegisterIp(registerIp);
        user.setRegisterAgent(registerAgent);
        user.setRegisterSource(registerSource);
        user.setRecommendCode(StringUtils.getUUID(8).toLowerCase());

        //设置角色
        UserRole consumerRole = new UserRole();
        consumerRole.setRole(roleService.findByCode(RoleDict.CODE_CONSUMER));

        HashSet<UserRole> userRoleSet = new HashSet<>();
        userRoleSet.add(consumerRole);
        user.setUserRoleSet(userRoleSet);
        user.setRoleType(String.valueOf(UserDict.ROLE_TYPE_CONSUMER));
        user.setUserAsset(new UserAsset());
        this.save(user);

        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param mobile   登录手机号
     * @param password 登录密码
     * @return 登录用户id
     */
    public Integer login(String mobile, String password) {
        User user = this.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, user);
        AssertUtils.isTrue(SYS_USER_LOCK, !user.getLocked());
        AssertUtils.isTrue(PASSWORD_ERROR, PasswordUtils.validatePassword(password, user.getPassword()));
        return user.getId();
    }

    /**
     * 商家登录
     *
     * @param mobile   登录手机号
     * @param password 登录密码
     * @return 登录用户id
     */
    public Integer merchantLogin(String mobile, String password) {
        User user = this.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, user);
        AssertUtils.isTrue(PASSWORD_ERROR, PasswordUtils.validatePassword(password, user.getPassword()));
        AssertUtils.isTrue(NO_MERCHANT, isIncludeRole(user, RoleDict.CODE_MERCHANT));
        return user.getId();
    }

    /**
     * 检查是否是商户
     *
     * @param userId 用户id
     */
    public void checkIsMerchant(Integer userId) {
        User user = super.findById(userId);
        AssertUtils.isTrue(NO_MERCHANT, isIncludeRole(user, RoleDict.CODE_MERCHANT));
    }

    /**
     * 判断用户是否是对应角色
     *
     * @param user 用户
     * @return boolean
     */
    public Boolean isIncludeRole(User user, RoleDict roleCode) {
        Set<UserRole> userRoleSet = user.getUserRoleSet();
        long count = userRoleSet.stream()
                .filter(userRole -> roleCode.compare(userRole.getRole().getCode()))
                .count();
        return count > 0;
    }

    /**
     * 找出用户是否推广员或者高级推广员
     *
     * @param user 用户
     * @return 用户角色
     */
    public UserRole findAgent(User user) {
        Set<UserRole> userRoleSet = user.getUserRoleSet();
        UserRole userRole = null;
        for (UserRole role : userRoleSet) {
            if (RoleDict.CODE_UP_AGENT.compare(role.getRole().getCode())) {
                userRole = role;
                break;
            } else if (RoleDict.CODE_AGENT.compare(role.getRole().getCode())) {
                userRole = role;
                break;
            }
        }
        return userRole;
    }

    /**
     * 修改登录密码
     *
     * @param mobile   登录手机号
     * @param password 新密码
     * @return 登录用户id
     */
    public Integer forgetPwd(String mobile, String password) {
        User user = this.findByMobile(mobile);
        AssertUtils.notNull(MOBILE_NOT_EXIST, user);
        user.setPassword(PasswordUtils.createHash(password));
        user.setUpdateTime(new Date());
        super.update(user);
        return user.getId();
    }

    /**
     * 修改用户地址
     *
     * @param id      用户id
     * @param address 用户地址
     */
    public void editAddress(Integer id, String address) {
        User user = super.findById(id);
        if (user != null) {
            user.setArea(address);
            super.update(user);
        }
    }

    /**
     * 查找当前用户的下级用户
     *
     * @param parentId 用户id
     * @param pageable 分页信息
     * @return 下级用户
     */
    public Page<User> listByParentId(int parentId, Pageable pageable) {
        SimpleSpecificationBuilder<User> builder = new SimpleSpecificationBuilder<>();
        builder.add("parentId", SpecificationOperator.Operator.eq, parentId);
        return super.findAll(builder.generateSpecification(), pageable);
    }
}
