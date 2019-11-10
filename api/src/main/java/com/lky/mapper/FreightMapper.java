package com.lky.mapper;

import com.lky.commons.utils.JsonUtils;
import com.lky.dto.AddressDto;
import com.lky.dto.FreightTemplateDto;
import com.lky.entity.FreightTemplate;
import com.lky.service.AreaService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.inject.Inject;

/**
 * 运费模板Dto转换
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/23
 */
@Mapper(componentModel = "jsr330")
public abstract class FreightMapper {

    @Inject
    private AreaService areaService;

    @Mappings({
            @Mapping(source = "sendAddress", target = "addressDto"),
    })
    public abstract FreightTemplateDto toDto(FreightTemplate freightTemplate);

    @Mappings({
            @Mapping(source = "addressDto", target = "sendAddress"),
    })
    public abstract FreightTemplate fromDto(FreightTemplateDto freightTemplateDto);


    public AddressDto toAddressDto(String sendAddress) {
        return JsonUtils.jsonToObject(sendAddress, AddressDto.class);
    }

    public String toSendAddress(AddressDto addressDto) {
        addressDto.setProvince(areaService.findById(addressDto.getProvince().getId()));
        addressDto.setCity(areaService.findById(addressDto.getCity().getId()));
        addressDto.setDistrict(areaService.findById(addressDto.getDistrict().getId()));
        return JsonUtils.objectToJson(addressDto);
    }

}
