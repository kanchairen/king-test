package com.lky.modules.api;

import com.lky.LkyApplication;
import com.lky.dto.SRoleDto;
import com.lky.entity.SMenu;
import com.lky.entity.SRole;
import com.lky.mapper.SRoleMapper;
import com.lky.service.SMenuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 系统角色相关测试类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LkyApplication.class)
public class SRoleTest {

    @Inject
    private SMenuService sMenuService;

    @Inject
    private SRoleMapper sRoleMapper;

    @Test
    public void findMenuList() {
        List<SMenu> sMenuList = sMenuService.findAll();
        System.out.println(Arrays.toString(sMenuList.toArray()));

        Set<Integer> idSet = new HashSet<>();
        idSet.add(100);
        Specification<SMenu> shopSpecification = (root, query, cb) -> root.in(idSet);
        List<SMenu> all = sMenuService.findAll(shopSpecification);
    }

    @Test
    public void toDto() {
        SRole sRole = new SRole();
        sRole.setId(1);
        SRoleDto sRoleDto = sRoleMapper.toDto(sRole);
        System.out.println("%%%%%%%%%%%%%%%%%%");
    }
}
