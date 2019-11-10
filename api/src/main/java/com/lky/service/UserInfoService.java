package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.RegexUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.UserDao;
import com.lky.dao.UserSqlDao;
import com.lky.dto.DetailRecordDto;
import com.lky.dto.PlatformCountDto;
import com.lky.dto.UserEpitomeDto;
import com.lky.dto.UserInfoDto;
import com.lky.entity.User;
import com.lky.enums.dict.*;
import com.lky.scheduling.ShopScheduling;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsxExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.code.UserResCode.PARENT_NOT_SELF;
import static com.lky.enums.code.UserResCode.RECOMMEND_CODE_ERROR;
import static com.lky.enums.dict.ChangeWPointRecordDict.*;
import static com.lky.enums.dict.ShopStatisticsDict.REPORTS_DIR;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 * 用户个人资料
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/16
 */
@Service
public class UserInfoService extends BaseService<User, Integer> {

    private static final Logger log = LoggerFactory.getLogger(ShopScheduling.class);

    @Inject
    private UserDao userDao;

    @Inject
    private ImageService imageService;

    @Inject
    private UserService userService;

    @Inject
    private BalanceRecordService balanceRecordService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private RPointRecordService rPointRecordService;

    @Inject
    private LockWPointRecordService lockWPointRecordService;

    @Inject
    private UserSqlDao userSqlDao;

    @Inject
    private SurplusGrainRecordService surplusGrainRecordService;

    @Override
    public BaseDao<User, Integer> getBaseDao() {
        return this.userDao;
    }

    /**
     * 修改用户信息
     *
     * @param user        原用户信息
     * @param userInfoDto 修改用户信息dto
     * @return 修改后的用户信息
     */
    public User editUserInfo(User user, UserInfoDto userInfoDto) {
        user.setArea(userInfoDto.getAddress());
        if (userInfoDto.getAvatarImage() != null) {
            Integer imageId = userInfoDto.getAvatarImage().getId();
            AssertUtils.notNull(PARAMS_EXCEPTION, imageId);
            user.setAvatarImage(imageService.findById(imageId));
        } else {
            user.setAvatarImage(null);
        }
        String recommendCode = userInfoDto.getRecommendCode();
        if (user.getParentId() == null && StringUtils.isNotEmpty(recommendCode)) {
            //推荐码可以是用户手机号或是其推荐码
            User parentUser;
            if (RegexUtils.isMobileNumber(recommendCode)) {
                parentUser = userService.findByMobile(recommendCode);
            } else {
                parentUser = userService.findByRecommendCode(recommendCode);
            }
            AssertUtils.notNull(RECOMMEND_CODE_ERROR, parentUser);
            AssertUtils.isTrue(PARENT_NOT_SELF, parentUser.getId() != user.getId());
            user.setParentId(parentUser.getId());
        }
        user.setEmail(userInfoDto.getEmail());
        user.setSex(userInfoDto.getSex());
        user.setNickname(userInfoDto.getNickname());
        user.setUpdateTime(new Date());
        return super.update(user);
    }

    /**
     * 获取用户财务列表
     *
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @param condition 姓名/手机号码
     * @param role      用户类型
     * @param type      排序类型
     * @param desc      是否降序
     * @return 用户财务信息列表
     */
    private List<UserEpitomeDto> getUserEpitomeList(String condition, String role, String type, Boolean desc,
                                                    int pageSize, int pageNum) {
        String sort = getSort(type, desc);
        List<UserEpitomeDto> userEpitomeList = userSqlDao.findUserEpitomeList(condition, role, sort, pageSize, pageNum);
        if (!CollectionUtils.isEmpty(userEpitomeList)) {
            for (UserEpitomeDto userEpitomeDto : userEpitomeList) {
                userEpitomeDto.setRoleType(UserDict.getEnum(userEpitomeDto.getRoleType()).getValue());
            }
        }
        return userEpitomeList;
    }

    /**
     * 获取用户财务列表
     *
     * @param pageNumber 页码
     * @param pageSize   每页条数
     * @param condition  姓名/手机号码
     * @param role       用户类型
     * @param type       排序类型
     * @param desc       是否降序
     * @return 用户财务信息列表
     */
    public Page<UserEpitomeDto> findEpitomeList(int pageNumber, int pageSize, String condition,
                                                String role, String type, Boolean desc) {
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        int total = userSqlDao.findUserEpitomeNumber(condition, role);
        return new PageImpl<>(this.getUserEpitomeList(condition, role, type, desc, pageSize, pageNumber), pageable, total);
    }

    /**
     * 生成用户财务列表统计报表
     *
     * @param condition 姓名/手机号码
     * @param role      用户类型
     * @param state     排序类型
     * @param desc      是否降序
     * @return 统计报表路径名称信息
     */
    public String buildReport(String condition, String role, String state, Boolean desc) {
        File file = new File(REPORTS_DIR.getKey());
        if (!file.exists() && !file.mkdirs()) {
            log.error("mkdir 失败");
        }
        String reportFileName = REPORTS_DIR.getKey() + "userEpitomeList_TempFile" + ".xlsx";
        File oldFile = new File(reportFileName);
        if (oldFile.exists()) {
            oldFile.delete();
        }
        List<UserEpitomeDto> userEpitomeList = this.getUserEpitomeList(condition, role, state, desc, 10000000, 0);

        List<Map<String, Object>> copyMap = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userEpitomeList)) {
            for (UserEpitomeDto userEpitomeDto : userEpitomeList) {
                Map<String, Object> map = new HashMap<>(10);
                map.put("realName", userEpitomeDto.getRealName());
                map.put("mobile", userEpitomeDto.getMobile());
                map.put("role", userEpitomeDto.getRoleType());
                map.put("wpoint", BigDecimal.valueOf(userEpitomeDto.getWpoint()));
                map.put("lockWPoint", BigDecimal.valueOf(userEpitomeDto.getLockWpoint()));
                map.put("merchantWPoint", BigDecimal.valueOf(userEpitomeDto.getMerchantWpoint()));
                map.put("merchantLockWPoint", BigDecimal.valueOf(userEpitomeDto.getMerchantLockWpoint()));
                map.put("rpoint", BigDecimal.valueOf(userEpitomeDto.getRpoint()));
                map.put("merchantRPoint", BigDecimal.valueOf(userEpitomeDto.getMerchantRpoint()));
                map.put("balance", BigDecimal.valueOf(userEpitomeDto.getBalance()));
                map.put("surplusGrain", BigDecimal.valueOf(userEpitomeDto.getSurplusGrain()));
                copyMap.add(map);
            }
        }
        createExcel(reportFileName, copyMap,
                col.column("真实姓名", "realName", type.stringType()),
                col.column("手机号码", "mobile", type.stringType()),
                col.column("角色", "role", type.stringType()),
                col.column("用户G米", "wpoint", type.bigDecimalType()),
                col.column("用户存量G米", "lockWPoint", type.bigDecimalType()),
                col.column("商家G米", "merchantWPoint", type.bigDecimalType()),
                col.column("商家存量G米", "merchantLockWPoint", type.bigDecimalType()),
                col.column("用户小米", "rpoint", type.bigDecimalType()),
                col.column("商家小米", "merchantRPoint", type.bigDecimalType()),
                col.column("大米", "balance", type.bigDecimalType()),
                col.column("余粮公社", "surplusGrain", type.bigDecimalType()));
        return reportFileName;
    }

    private void createExcel(String fileName, Collection collection, ColumnBuilder... columns) {
        try {
            report()
                    .addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "2")
                    .ignorePageWidth()
                    .ignorePagination()
                    .columns(columns)
                    .setDataSource(collection)
                    .toXlsx(getJasperXlsxExporterBuilder(fileName));
        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    private JasperXlsxExporterBuilder getJasperXlsxExporterBuilder(String fileName) {
        return export
                .xlsxExporter(fileName)
                .setDetectCellType(true)
                .setIgnorePageMargins(true)
                .setWhitePageBackground(false)
                .setRemoveEmptySpaceBetweenRows(true);
    }

    /**
     * 生成排序规则
     *
     * @param type 资产账户的资金类型
     * @param desc 是否降序
     * @return 排序规则 默认是按最近上线排序
     */
    private String getSort(String type, Boolean desc) {
        String sort;
        if (StringUtils.isNotEmpty(type) && desc != null) {
            AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_BALANCE, TYPE_WPOINT, TYPE_RPOINT, TYPE_LOCK_WPOINT,
                    TYPE_MERCHANT_WPOINT, TYPE_MERCHANT_RPOINT, TYPE_MERCHANT_LOCK_WPOINT, TYPE_SURPLUS_GRAIN);
            ChangeWPointRecordDict targetType = ChangeWPointRecordDict.getEnum(type);
            switch (targetType) {
                case TYPE_LOCK_WPOINT:
                    type = "lock_Wpoint";
                    break;
                case TYPE_MERCHANT_WPOINT:
                    type = "merchant_Wpoint";
                    break;
                case TYPE_MERCHANT_RPOINT:
                    type = "merchant_rpoint";
                    break;
                case TYPE_MERCHANT_LOCK_WPOINT:
                    type = "merchant_lock_wpoint";
                    break;
                case TYPE_SURPLUS_GRAIN:
                    type = "surplus_grain";
                    break;
            }
            sort = " ua." + type + " ";
            if (desc) {
                sort = sort + " DESC ";
            } else {
                sort = sort + " ASC ";
            }
        } else {
            sort = " u.create_time DESC ";
        }
        return sort;
    }

    /**
     * 查询用户账户某种资产的资金流水
     *
     * @param user      app用户
     * @param type      资金类型
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param pageable  分页信息
     * @return 资金流水
     */
    public Page<DetailRecordDto> findUserFundFlow(User user, String type, Long beginTime, Long endTime, Pageable pageable) {
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, user);
        Page<DetailRecordDto> detailRecordPage = null;
        AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_BALANCE, TYPE_WPOINT, TYPE_RPOINT, TYPE_LOCK_WPOINT,
                TYPE_MERCHANT_WPOINT, TYPE_MERCHANT_RPOINT, TYPE_MERCHANT_LOCK_WPOINT, TYPE_SURPLUS_GRAIN);
        ChangeWPointRecordDict targetType = ChangeWPointRecordDict.getEnum(type);
        switch (targetType) {
            case TYPE_BALANCE:
                detailRecordPage = balanceRecordService.findListByCondition(user, beginTime, endTime, pageable);
                break;

            case TYPE_WPOINT:
                detailRecordPage = wPointRecordService.findListByCondition(user,
                        WPointRecordDict.USER_TYPE_CONSUMER.getKey(), beginTime, endTime, pageable);
                break;

            case TYPE_MERCHANT_WPOINT:
                detailRecordPage = wPointRecordService.findListByCondition(user,
                        WPointRecordDict.USER_TYPE_MERCHANT.getKey(), beginTime, endTime, pageable);
                break;

            case TYPE_LOCK_WPOINT:
                detailRecordPage = lockWPointRecordService.findListByCondition(user,
                        LockWPointRecordDict.USER_TYPE_CONSUMER.getKey(), beginTime, endTime, pageable);
                break;

            case TYPE_MERCHANT_LOCK_WPOINT:
                detailRecordPage = lockWPointRecordService.findListByCondition(user,
                        LockWPointRecordDict.USER_TYPE_MERCHANT.getKey(), beginTime, endTime, pageable);
                break;

            case TYPE_RPOINT:
                detailRecordPage = rPointRecordService.findListByCondition(user,
                        RPointRecordDict.USER_TYPE_CONSUMER.getKey(), beginTime, endTime, pageable);
                break;

            case TYPE_MERCHANT_RPOINT:
                detailRecordPage = rPointRecordService.findListByCondition(user,
                        RPointRecordDict.USER_TYPE_MERCHANT.getKey(), beginTime, endTime, pageable);
                break;
            case TYPE_SURPLUS_GRAIN:
                detailRecordPage = surplusGrainRecordService.findListByCondition(user, beginTime, endTime, pageable, null);
                break;
            default:
                break;
        }
        return detailRecordPage;
    }

    /**
     * 查看平台财务
     *
     * @return 平台财务Dto
     */
    public PlatformCountDto platformCount() {
        return userSqlDao.getPlatformCount();
    }
}
