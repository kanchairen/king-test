package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dto.SMenuDto;
import com.lky.dto.SRoleDto;
import com.lky.dto.SRoleTreeDto;
import com.lky.entity.SMenu;
import com.lky.entity.SRole;
import com.lky.enums.code.RoleResCode;
import com.lky.mapper.SRoleMapper;
import com.lky.service.SMenuService;
import com.lky.service.SRoleService;
import com.lky.utils.BeanUtils;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;

/**
 * 系统角色Controller层
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@RestController
@RequestMapping("sys/role")
@Api(value = "sys/role", description = "系统角色（权限组管理）")
public class SRoleController extends BaseController {

    @Inject
    private SRoleService sRoleService;

    @Inject
    private SMenuService sMenuService;

    @Inject
    private SRoleMapper sRoleMapper;

    @ApiOperation(value = "添加新的系统角色", response = SRole.class)
    @PostMapping("add")
    public ResponseInfo create(@ApiParam(name = "sRoleDto", value = "系统角色信息")
                               @RequestBody SRoleDto sRoleDto) {
        AssertUtils.notNull(PARAMS_IS_NULL, sRoleDto, sRoleDto.getName());
        //校验系统角色名是否存在
        AssertUtils.isTrue(RoleResCode.NAME_IS_EXIST, !sRoleService.isExit(sRoleDto.getName(), null));
        AssertUtils.isTrue(PARAMS_IS_NULL, !CollectionUtils.isEmpty(sRoleDto.getsMenuList()));
        //校验菜单列表是否存在且结构完整
        sMenuService.checkExistAndLink(sRoleDto.getsMenuList());
        SRole sRole = sRoleService.create(sRoleDto);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sRole", sRole);
        return responseInfo;
    }

    @ApiOperation(value = "删除系统角色", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "系统角色id", paramType = "path", dataType = "int"),
    })
    @DeleteMapping("{id}")
    public ResponseInfo remove(@PathVariable Integer id) {
        SRole sRole = sRoleService.findById(id);
        AssertUtils.notNull(RoleResCode.NO_EXIST, sRole);
        sRoleService.remove(sRole);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "修改系统角色信息(修改什么内容就传什么内容，id必传)", response = SRole.class)
    @PutMapping("edit")
    public ResponseInfo edit(@ApiParam(name = "sRoleDto", value = "系统角色信息")
                             @RequestBody SRoleDto sRoleDto) {
        AssertUtils.notNull(PARAMS_IS_NULL, sRoleDto, sRoleDto.getId());
        //校验该系统角色是否存在
        SRole sRole = sRoleService.findById(sRoleDto.getId());
        AssertUtils.notNull(RoleResCode.NO_EXIST, sRole);

        if (sRoleDto.getName() != null) {
            //校验系统角色名是否存在
            AssertUtils.isTrue(RoleResCode.NAME_IS_EXIST, !sRoleService.isExit(sRoleDto.getName(), sRole.getId()));
        }
        //校验菜单列表是否存在且结构完整
        if (!CollectionUtils.isEmpty(sRoleDto.getsMenuList())) {
            sMenuService.checkExistAndLink(sRoleDto.getsMenuList());
        }
        sRoleService.edit(sRoleDto, sRole);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sRole", sRole);
        return responseInfo;
    }

    @ApiOperation(value = "系统角色详情，包含权限目录", response = SRoleTreeDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "系统角色id", paramType = "path", dataType = "int"),
    })
    @GetMapping("{id}")
    public ResponseInfo get(@PathVariable Integer id) {
        SRole sRole = sRoleService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, sRole);
        SRoleDto sRoleDto = sRoleMapper.toDto(sRole);
        SRoleTreeDto sRoleTreeDto = new SRoleTreeDto();
        BeanUtils.copyPropertiesIgnoreNull(sRoleDto, sRoleTreeDto);
        sRoleTreeDto.setsMenuList(sMenuService.findSMenuTree(sRoleDto.getsMenuList()));
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sRole", sRoleTreeDto);
        return responseInfo;
    }

    @ApiOperation(value = "所有的权限菜单列表", response = SMenu.class, notes = "sMenuList", responseContainer = "List")
    @GetMapping("menu/list")
    public ResponseInfo menuList() {
        List<SMenuDto> sMenuList = sMenuService.findWholeAll();
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sMenuList", sMenuList);
        return responseInfo;
    }

    @ApiOperation(value = "系统角色列表", response = SRole.class, notes = "sRoleList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping("list")
    @RequiresPermissions("permission:group:manager:list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize) {
        return this.roleList(pageNumber, pageSize);
    }

    @ApiOperation(value = "系统角色列表—（添加子账号用）", response = SRole.class, notes = "sRoleList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
    })
    @GetMapping("sub/list")
    public ResponseInfo subList(@RequestParam(defaultValue = "0") int pageNumber,
                                @RequestParam(defaultValue = "10") int pageSize) {
        return this.roleList(pageNumber, pageSize);
    }

    private ResponseInfo roleList(int pageNumber, int pageSize) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        Page<SRole> page = sRoleService.findAll(pageable);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("sRoleList", page);
        return responseInfo;
    }
}
