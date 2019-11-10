package com.lky.modules.api;

import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.UserSqlDao;
import com.lky.entity.Area;
import com.lky.entity.User;
import com.lky.entity.UserAsset;
import com.lky.enums.dict.UserDict;
import com.lky.scheduling.SurplusGrainScheduling;
import com.lky.service.AreaService;
import com.lky.service.UserService;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户测试
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/10
 */
public class UserTest extends BaseTest {

    @Inject
    private UserService userService;

    @Inject
    private AreaService areaService;

    @Inject
    private Environment environment;

    @Inject
    private UserSqlDao userSqlDao;

    @Inject
    private SurplusGrainScheduling surplusGrainScheduling;

    @Test
    public void save() {
        //用户资产
        UserAsset userAsset = new UserAsset();
        userAsset.setBalance(10000);
        userAsset.setCashDeposit(1000);
        userAsset.setMerchantWPoint(0);

        //用户
        User user = new User();
        user.setUsername("haha");
        user.setPassword("123321");
        user.setMobile("18221352257");
        user.setNickname("haha");
        user.setRealName("周zhi华");
        user.setRegisterSource(String.valueOf(UserDict.REGISTER_SOURCE_ANDROID));
        user.setRecommendCode("88888888");
        user.setUserAsset(userAsset);

        userService.save(user);
    }

    @Test
    public void register() throws InterruptedException {
        try {
            String noRegisterAward = environment.getProperty("no-register-award-time");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date timePoint = dateFormat.parse(noRegisterAward);
            System.out.println(timePoint);
            if (new Date().before(timePoint)) {
                System.out.println(true);
            } else {
                System.out.println(false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void update() {
        User user = userService.findById(3);
        user.setNickname("hua");
        userService.update(user);
    }

    @Test
    public void ssss() {
        Set<Integer> idSet = new HashSet<>();
        idSet.add(1);
        idSet.add(2);
        idSet.add(5);
        Specification<User> shopSpecification = (root, query, cb) -> root.in(idSet);
        System.out.println(userService.count(shopSpecification));
    }

    @Test
    public void batchUpdate() {
        List<User> userList = userService.findAll();
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                if (user.getId() % 2 == 0) {
                    user.setSex("男");
                }
            }
            userService.update(userList);
        }
    }

    @Test
    public void batchUpdateTwo() {
        Long begin = new Date().getTime();
        List<Area> areaList = areaService.findAll();
        if (!CollectionUtils.isEmpty(areaList)) {
            for (Area area : areaList) {
                if (area.getParent() != null) {
                    area.setParent(area.getParent() - 1);
                }
            }
            areaService.update(areaList);
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(new Date().getTime() - begin);

        Long begin1 = new Date().getTime();
        List<Area> areaList1 = areaService.findAll();
        if (!CollectionUtils.isEmpty(areaList1)) {
            for (Area area : areaList1) {
                if (area.getParent() != null) {
                    area.setParent(area.getParent() + 1);
                }
                areaService.update(area);
            }
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println(new Date().getTime() - begin1);
    }

    @Test
    public void autoSql() {
        List<Map<String, Object>> mapList = userSqlDao.findAutomaticSurplusGrain(5000000);
        System.out.println("----------------------------");
        System.out.println(mapList.toString());
    }

    @Test
    public void autoSurplusGrain() {
        surplusGrainScheduling.automaticSurplusGrain();
    }

    @Test
    public void find() {
        User user = userService.findByUserAssetId(1);
        System.out.println(user);
    }
}
