package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.AreaDao;
import com.lky.dto.AddressDto;
import com.lky.dto.AreaChildDto;
import com.lky.entity.Area;
import com.lky.utils.BaiduMapUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.dict.AreaDict.*;

/**
 * 地址操作
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/16
 */
@Service
public class AreaService extends BaseService<Area, Integer> {

    @Inject
    private AreaDao areaDao;

    @Override
    public BaseDao<Area, Integer> getBaseDao() {
        return this.areaDao;
    }

    /**
     * 根据名称和类型校验
     *
     * @param name 名称
     * @param type 类型
     * @return 个数
     */
    public int countByNameAndType(String name, String type) {
        return areaDao.countByNameAndType(name, type);
    }

    /**
     * 地址效验
     *
     * @param addressDto 地址dto
     * @param needDetail 是否需要详细地址
     */
    public void verifyAddress(AddressDto addressDto, boolean needDetail) {

        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"address"}, addressDto);

        Area province = addressDto.getProvince();
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"provinceId"}, province.getId());
        Area provinceDb = super.findById(province.getId());
        AssertUtils.isTrue(PARAMS_EXCEPTION, provinceDb != null && TYPE_PROVINCE.compare(provinceDb.getType()));
        addressDto.setProvince(provinceDb);

        Area city = addressDto.getCity();
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"cityId"}, city.getId());
        Area cityDb = super.findById(city.getId());
        AssertUtils.isTrue(PARAMS_EXCEPTION, cityDb != null && TYPE_CITY.compare(cityDb.getType()));
        addressDto.setCity(cityDb);

        Area district = addressDto.getDistrict();
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"districtId"}, district.getId());
        Area districtDb = super.findById(district.getId());
        AssertUtils.isTrue(PARAMS_EXCEPTION, districtDb != null && TYPE_DISTRICT.compare(districtDb.getType()));
        addressDto.setDistrict(districtDb);

        if (needDetail) {
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"detail"}, addressDto.getDetail());
        }
    }

    /**
     * 通过省份信息找到其所以的市、区县
     *
     * @param areaListAll  所有省市区列表
     * @param areaProvince 省
     * @return 省份对应的市区县
     */
    public AreaChildDto findByProvince(List<Area> areaListAll, Area areaProvince) {
        AreaChildDto areaProvinceDto = new AreaChildDto();
        areaProvinceDto.setArea(areaProvince);
        List<AreaChildDto> areaCityList = new ArrayList<>();
//                        找到市
        areaListAll.forEach(areaCity -> {
            if (areaCity.getParent() != null && areaCity.getParent() == areaProvince.getId()) {
                List<AreaChildDto> areaDistrictList = new ArrayList<>();
                AreaChildDto areaCityDto = new AreaChildDto();
                areaCityDto.setArea(areaProvince);
                //找到区县
                areaListAll.forEach(areaDistrict -> {
                    if (areaDistrict.getParent() != null && areaDistrict.getParent() == areaCity.getId()) {
                        AreaChildDto areaDistrictDto = new AreaChildDto();
                        areaDistrictDto.setArea(areaDistrict);
                        areaDistrictList.add(areaDistrictDto);
                    }
                });
                areaCityDto.setAreaChild(areaDistrictList);
                areaCityDto.setArea(areaCity);
                areaCityList.add(areaCityDto);
            }
        });
        areaProvinceDto.setAreaChild(areaCityList);
        areaProvinceDto.setArea(areaProvince);
        return areaProvinceDto;

    }

    /**
     * 根据经纬度查找数据库中城市对象
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 系统中城市对象
     */
    public Area findCityByLngAndLat(String lng, String lat) {
        String[] areaArr = BaiduMapUtil.findAreaByLngAndLat(lng, lat);
        if (!CollectionUtils.isEmpty(areaArr) && areaArr.length == 3) {
            SimpleSpecificationBuilder<Area> builder = new SimpleSpecificationBuilder<>();
            builder.add("name", SpecificationOperator.Operator.eq, areaArr[0]);
            builder.add("type", SpecificationOperator.Operator.eq, "province");
            List<Area> provinceList = super.findAll(builder.generateSpecification());
            SimpleSpecificationBuilder<Area> builderCity = new SimpleSpecificationBuilder<>();
            builderCity.add("name", SpecificationOperator.Operator.eq, areaArr[1]);
            builderCity.add("type", SpecificationOperator.Operator.eq, "city");
            List<Area> cityList = super.findAll(builderCity.generateSpecification());
            if (!CollectionUtils.isEmpty(provinceList) && !CollectionUtils.isEmpty(cityList)) {
                if (cityList.get(0).getParent() == provinceList.get(0).getId()) {
                    return cityList.get(0);
                }
            }
        }
        return null;
    }

    public List<AreaChildDto> findListAll() {
        List<AreaChildDto> areaChildDtoList = new ArrayList<>();
        List<Area> areaListAll = super.findAll();
        areaListAll.forEach(area -> {
            //找到大的区域
            if (area.getParent() == null) {
                AreaChildDto areaRegionDto = new AreaChildDto();
                List<AreaChildDto> areaProvinceList = new ArrayList<>();

                areaListAll.forEach(areaProvince -> {
                    //找到省份
                    if (areaProvince.getParent() != null && areaProvince.getParent() == area.getId()) {
                        areaProvinceList.add(this.findByProvince(areaListAll, areaProvince));
                    }
                });
                areaRegionDto.setAreaChild(areaProvinceList);
                areaRegionDto.setArea(area);
                areaChildDtoList.add(areaRegionDto);
            }
        });
        return areaChildDtoList;
    }

    public void test() {
        Area area = areaDao.findOne(1);
        area.setParent(0);
        areaDao.saveAndFlush(area);
        int i = 1 / 0;
        Area area1 = areaDao.findOne(2);
        area1.setParent(11111);
        areaDao.saveAndFlush(area1);
    }
}
