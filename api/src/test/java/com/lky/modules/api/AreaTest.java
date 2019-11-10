package com.lky.modules.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lky.commons.utils.FileUtils;
import com.lky.dao.AreaDao;
import com.lky.entity.Area;
import com.lky.enums.dict.AreaDict;
import org.junit.Test;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 地区测试
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/17
 */
public class AreaTest {

    @Inject
    private AreaDao areaDao;

    @Test
    public  void testAdd() throws Exception
    {

//        File file = new File("E:\\project\\lky-server\\api\\src\\main\\resources\\db_init\\area.json");
        System.out.println(System.getProperty("user.dir"));
        File file = new File("src\\main\\resources\\db_init\\area.json");
        String jsonString = FileUtils.load(file, "UTF-8");
        JSONObject wholeChinaJson = JSONObject.parseObject(jsonString);
        List<Area> areaList = new ArrayList<>();
        Set<String> bigAreasKeySet = wholeChinaJson.keySet();
        System.out.println(wholeChinaJson);
        //添加大区
        for (String regionKey : bigAreasKeySet) {
            Area region = new Area();
            region.setName(regionKey);
            region.setType(String.valueOf(AreaDict.TYPE_REGION));

            JSONObject bigAreaJson = wholeChinaJson.getJSONObject(regionKey);
            Set<String> provinceKeySet = bigAreaJson.keySet();

            //添加省份
            for (String provinceKey : provinceKeySet) {
                Area province = new Area();
                province.setName(provinceKey);
                province.setParent(region.getId());
                province.setType(String.valueOf(AreaDict.TYPE_PROVINCE));

                JSONObject provinceJson = bigAreaJson.getJSONObject(provinceKey);
                Set<String> cityKeySet = provinceJson.keySet();
                //添加城市
                for (String cityKey : cityKeySet) {
                    Area city = new Area();
                    city.setName(cityKey);
                    city.setParent(province.getId());
                    city.setType(String.valueOf(AreaDict.TYPE_CITY));

                    JSONArray cityJsonArray = provinceJson.getJSONArray(cityKey);

                    //添加县区
                    for (Object areaName : cityJsonArray) {
                        Area area = new Area();
                        area.setName((String) areaName);
                        area.setParent(city.getId());
                        area.setType(String.valueOf(AreaDict.TYPE_DISTRICT));
                        areaList.add(area);
                    }
                }
            }
        }




    }


}
