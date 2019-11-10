package com.lky.modules.api;

import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.DateUtils;
import com.lky.dao.SUserDao;
import com.lky.entity.SUser;
import com.lky.entity.User;
import com.lky.entity.UserAsset;
import com.lky.enums.dict.SUserDict;
import com.lky.service.SUserService;
import com.lky.service.UserAssetService;
import com.lky.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.util.*;

import static java.util.Calendar.DAY_OF_YEAR;

/**
 * 分页和排序测试
 * 条件查询测试
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/19
 */
public class JpaTest extends BaseTest {

    @Inject
    private SUserService sUserService;

    @Inject
    private SUserDao sUserDao;

    @Inject
    private UserService userService;

    @Inject
    private EntityManager entityManager;

    @Inject
    private UserAssetService userAssetService;

    @Test
    @Transactional
    public void testAddSUser() {
        List<SUser> userList = new ArrayList<>();
        //添加操作
        for (int i = 0; i < 10; i++) {
            SUser sUser = new SUser();
            sUser.setUsername("admin" + i);
            sUser.setPassword("111111");
            sUser.setMobile("13011112222");
            sUser.setEmail("kk@qq.com");
            sUser.setState(String.valueOf(SUserDict.STATE_ACTIVE));
            sUser.setCreateTime(new Date());
            userList.add(sUser);
        }
        sUserService.save(userList);
        Assert.assertEquals(11, sUserService.count());
    }

    @Test
    public void testFind() {
        List<SUser> userList = sUserService.findAll();
        Assert.assertEquals(11, userList.size());
    }

    @Test
    public void testPage() {
        //显示第1页每页显示3条
        PageRequest pr = new PageRequest(1, 3);
        Page<SUser> sUserList = sUserService.findAll(pr);
        Assert.assertEquals(4, sUserList.getTotalPages());
        Assert.assertEquals(11, sUserList.getTotalElements());
        Assert.assertEquals(1, sUserList.getNumber());
    }

    @Test
    public void testSort() {
        //设置排序方式为username降序
        List<SUser> sUserList = sUserService.findAll(new Sort(Sort.Direction.DESC, "username"));
        Assert.assertEquals(Integer.valueOf(10), sUserList.get(0).getId());

        //设置排序以username和id进行升序
        sUserList = sUserService.findAll(new Sort(Sort.Direction.ASC, "username", "id"));
        Assert.assertEquals(Integer.valueOf(2), sUserList.get(0).getId());

        //设置排序方式以username升序，以id降序
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.ASC, "username"),
                new Sort.Order(Sort.Direction.DESC, "id"));

        sUserList = sUserService.findAll(sort);
        Assert.assertEquals(Integer.valueOf(3), sUserList.get(0).getId());
    }

    @Test
    public void testPageAndSort() {
        //显示第1页每页显示3条
        PageRequest pr = new PageRequest(1, 3, new Sort(Sort.Direction.DESC, "username"));
        Page<SUser> sUserList = sUserService.findAll(pr);
        Assert.assertEquals(2, sUserList.getTotalPages());
        Assert.assertEquals(6, sUserList.getTotalElements());
        Assert.assertEquals(1, sUserList.getNumber());
    }

    @Test
    public void testSpecification() {
        //select * from s_user where username like '%zt%' and id > 3
        List<SUser> sUserList = sUserDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            //root.get("username")表示获取username这个字段名称,like表示执行like查询,%zt%表示值
            Predicate p1 = criteriaBuilder.like(root.get("username"), "%zt%");
            Predicate p2 = criteriaBuilder.greaterThan(root.get("id"), 3);
            //将两个查询条件联合起来之后返回Predicate对象,username模糊查询，id>3
            return criteriaBuilder.and(p1, p2);
        });
        Assert.assertEquals(2, sUserList.size());
        Assert.assertEquals("admin", sUserList.get(0).getUsername());
    }

    @Test
    public void testSpecification2() {
        //select * from s_user where (id = 2 or id = 3) and (email like 'zt%' or username like 'foo%')
        //第一个Specification定义了两个or的组合
        Specification<SUser> s1 = (root, criteriaQuery, criteriaBuilder) -> {
            Predicate p1 = criteriaBuilder.equal(root.get("id"), 2);
            Predicate p2 = criteriaBuilder.equal(root.get("id"), 3);
            return criteriaBuilder.or(p1, p2);
        };
        //第二个Specification定义了两个or的组合
        Specification<SUser> s2 = (root, criteriaQuery, criteriaBuilder) -> {
            Predicate p1 = criteriaBuilder.like(root.get("email"), "kk%");
            Predicate p2 = criteriaBuilder.like(root.get("username"), "foo%");
            return criteriaBuilder.or(p1, p2);
        };
        //通过Specifications将两个Specification连接起来，第一个条件加where，第二个是and
        List<SUser> sUserList = sUserDao.findAll(Specifications.where(s1).and(s2));

        Assert.assertEquals(2, sUserList.size());
        Assert.assertEquals(Integer.valueOf(2), sUserList.get(0).getId());
    }

    @Test
    public void testSpecification3() {
        //测试like
        SimpleSpecificationBuilder<SUser> builder = new SimpleSpecificationBuilder<>();
        builder.add("username", SpecificationOperator.Operator.likeAll, "l");
        List<SUser> sUserList = sUserDao.findAll(builder.generateSpecification());
        sUserList.forEach(sUser -> System.out.println(sUser.getUsername()));
    }

    @Test
    public void testSpecification4() {
        //测试between
        Date date = new Date();
        Date beginDate = DateUtils.getBeginDate(date, DAY_OF_YEAR);
        SimpleSpecificationBuilder<User> builder = new SimpleSpecificationBuilder<>();
        builder.add("createTime", SpecificationOperator.Operator.between, new Date[]{beginDate, date});
        List<User> userList = userService.findAll(builder.generateSpecification());
        System.out.println("0------------------" + userList.size());

        //测试in
        Set<Integer> idSet = new HashSet<>();
        idSet.add(1);
        idSet.add(2);
        idSet.add(1999);
        SimpleSpecificationBuilder<User> builder1 = new SimpleSpecificationBuilder<>();
        builder1.add("id", SpecificationOperator.Operator.in, idSet);
        List<User> userList1 = userService.findAll(builder1.generateSpecification());
        System.out.println("1------------------" + userList1.size());

        //测试not in
        SimpleSpecificationBuilder<User> builder2 = new SimpleSpecificationBuilder<>();
        builder2.add("id", SpecificationOperator.Operator.notIn, 1);
        List<User> userList2 = userService.findAll(builder2.generateSpecification());
        System.out.println("2------------------" + userList2.size());

        //测试lessThanEqual
        SimpleSpecificationBuilder<User> builder3 = new SimpleSpecificationBuilder<>();
        builder3.add("createTime", SpecificationOperator.Operator.lessThanEqual, beginDate);
        List<User> userList3 = userService.findAll(builder3.generateSpecification());
        System.out.println("3------------------" + userList3.size());
    }

    @Test
    public void testSpecification5() {
        //测试in
        Specification<User> spec = (root, query, cb) -> {
            Set<Integer> idSet = new HashSet<>();
            idSet.add(1);
            idSet.add(2);
            idSet.add(1999);
            SpecificationOperator so = new SpecificationOperator();
            so.setKey("id");
            so.setValue(idSet);
            if (so.getValue() instanceof Collection) {
                Collection idS = (Collection) so.getValue();
                return root.get(so.getKey()).in(idS);
            }
            return null;
        };
        List<User> userList1 = userService.findAll(spec);
        System.out.println("1------------------" + userList1.size());
    }

    @Test
    public void testQuery() {
        Query query = entityManager.createQuery("select ua from UserAsset ua where ua.id = 1", UserAsset.class);
        UserAsset userAsset = (UserAsset) query.getSingleResult();
        System.out.println(userAsset);
        userAsset.setBalance(1000);
        userAssetService.update(userAsset);
    }

}
