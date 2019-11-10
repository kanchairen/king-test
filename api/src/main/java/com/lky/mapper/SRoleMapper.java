package com.lky.mapper;

import com.lky.dto.SRoleDto;
import com.lky.entity.SMenu;
import com.lky.entity.SRole;
import com.lky.service.SMenuService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.inject.Inject;
import java.util.List;

/**
 * 系统角色Dto转换
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@Mapper(componentModel = "jsr330")
public abstract class SRoleMapper {

    @Inject
    private SMenuService sMenuService;

    @Mapping(source = "id", target = "id", ignore = true)
    public abstract SRole fromDto(SRoleDto sRoleDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "id", target = "sMenuList")
    public abstract SRoleDto toDto(SRole sRole);

    public abstract List<SRoleDto> toDtoList(List<SRole> sRoleList);

    public List<SMenu> toMenuList(Integer id) {
        return sMenuService.findBySRoleId(id);
    }
}
