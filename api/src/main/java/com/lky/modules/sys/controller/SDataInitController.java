package com.lky.modules.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.FileUtils;
import com.lky.commons.utils.JsonUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dao.AreaDao;
import com.lky.dao.ExpressDao;
import com.lky.dao.SMenuDao;
import com.lky.entity.Area;
import com.lky.entity.Express;
import com.lky.entity.Role;
import com.lky.entity.SMenu;
import com.lky.enums.dict.AreaDict;
import com.lky.enums.dict.SMenuDict;
import com.lky.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.lky.enums.dict.RoleDict.*;

/**
 * 数据初始化
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@RestController
@RequestMapping("sys/data/init")
@Api(value = "sys/data/init", description = "系统数据初始化")
public class SDataInitController extends BaseController {

    @Inject
    private RoleService roleService;

    @Inject
    private AreaDao areaDao;

    @Inject
    private ExpressDao expressDao;

    @Inject
    private SMenuDao sMenuDao;

    @ApiOperation(value = "初始化app角色", response = ResponseInfo.class)
    @PostMapping("role")
    public ResponseInfo initRole() {

        Role consumerRole = new Role();
        consumerRole.setName(CODE_CONSUMER.getValue());
        consumerRole.setCode(String.valueOf(CODE_CONSUMER));
        consumerRole.setDescription("app用户注册时，默认分配该角色");

        Role merchantRole = new Role();
        merchantRole.setName(CODE_MERCHANT.getValue());
        merchantRole.setCode(String.valueOf(CODE_MERCHANT));
        merchantRole.setDescription("申请成为商家之后，赋予该角色");

        Role agentRole = new Role();
        agentRole.setName(CODE_AGENT.getValue());
        agentRole.setCode(String.valueOf(CODE_AGENT));
        agentRole.setDescription("消费者达到条件可以直接升级为推广员，推广10个以上的用户或者在平台消费满100元");

        Role upAgentRole = new Role();
        upAgentRole.setName(CODE_UP_AGENT.getValue());
        upAgentRole.setCode(String.valueOf(CODE_UP_AGENT));
        upAgentRole.setDescription("任何角色达到条件可以直接升级为高级推广员, 缴纳365元年费");

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(consumerRole);
        roleSet.add(merchantRole);
        roleSet.add(agentRole);
        roleSet.add(upAgentRole);
        roleService.save(roleSet);

        return ResponseUtils.buildResponseInfo();
    }


    @ApiOperation(value = "初始化地址", response = ResponseInfo.class)
    @PostMapping("area")
    public ResponseInfo initArea() throws Exception {

        File file = ResourceUtils.getFile("classpath:db_init/area.json");
        String jsonString = FileUtils.load(file, "UTF-8");

        JSONObject wholeChinaJson = JSONObject.parseObject(jsonString);
        Set<String> bigAreasKeySet = wholeChinaJson.keySet();
        //添加大区
        for (String regionKey : bigAreasKeySet) {
            Area region = new Area();
            region.setName(regionKey);
            region.setType(String.valueOf(AreaDict.TYPE_REGION));
            region.setParent(null);
            areaDao.save(region);

            JSONObject bigAreaJson = wholeChinaJson.getJSONObject(regionKey);
            Set<String> provinceKeySet = bigAreaJson.keySet();

            //添加省份
            for (String provinceKey : provinceKeySet) {
                Area province = new Area();
                province.setName(provinceKey);
                province.setParent(region.getId());
                province.setType(String.valueOf(AreaDict.TYPE_PROVINCE));
                areaDao.save(province);

                JSONObject provinceJson = bigAreaJson.getJSONObject(provinceKey);
                Set<String> cityKeySet = provinceJson.keySet();

                //添加城市
                for (String cityKey : cityKeySet) {
                    Area city = new Area();
                    city.setName(cityKey);
                    city.setParent(province.getId());
                    city.setType(String.valueOf(AreaDict.TYPE_CITY));
                    areaDao.save(city);

                    JSONArray cityJsonArray = provinceJson.getJSONArray(cityKey);

                    //添加县区
                    for (Object areaName : cityJsonArray) {
                        Area area = new Area();
                        area.setName((String) areaName);
                        area.setParent(city.getId());
                        area.setType(String.valueOf(AreaDict.TYPE_DISTRICT));
                        areaDao.save(area);
                    }
                }
            }
        }
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "初始化物流公司", response = ResponseInfo.class)
    @PostMapping("express")
    public ResponseInfo initExpress() throws Exception {

        File file = ResourceUtils.getFile("classpath:db_init/shipper.json");
        String jsonString = FileUtils.load(file, "UTF-8");

        Map<String, String> jsonToMap = JsonUtils.jsonToMap(jsonString, String.class, String.class);

        assert jsonToMap != null;
        List<Express> expressList = new ArrayList<>();
        for (Map.Entry<String, String> entry : jsonToMap.entrySet()) {
            Express express = new Express();
            express.setCode(entry.getKey());
            express.setName(entry.getValue());
            expressList.add(express);
        }
        expressDao.save(expressList);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "初始化菜单", response = ResponseInfo.class)
    @PostMapping(value = "menu")
    public ResponseInfo initMenu() throws FileNotFoundException {

        File file = ResourceUtils.getFile("classpath:db_init/menu.json");
        String jsonString = FileUtils.load(file, "UTF-8");

        Map<String, String> jsonToMap = JsonUtils.jsonToMap(jsonString, String.class, String.class);

        assert jsonToMap != null;
        List<SMenu> menuList = new ArrayList<>();
        int i = jsonToMap.entrySet().size();
        for (Map.Entry<String, String> entry : jsonToMap.entrySet()) {
            SMenu menu = new SMenu();
            menu.setName(entry.getKey());
            menu.setPerms(entry.getValue());
            menu.setType(String.valueOf(SMenuDict.TYPE_MENU));
            menu.setSortIndex(i);
            menuList.add(menu);
            i--;
        }
        sMenuDao.save(menuList);

        return ResponseUtils.buildResponseInfo();
    }
}
