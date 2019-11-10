package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.SMenuDto;
import com.lky.entity.SMenu;
import com.lky.enums.dict.SMenuDict;
import com.lky.service.SMenuService;
import com.lky.utils.BeanUtils;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.SetResCode.MENU_PERMS_EXIST;

/**
 * 权限管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/4/8
 */
@RestController
@RequestMapping("/sys/menu")
@Api(value = "sys/menu", description = "权限管理")
public class SMenuController extends BaseController {

    @Inject
    private SMenuService sMenuService;

    @ApiOperation(value = "添加权限", response = SMenu.class, notes = "返回新增权限")
    @PostMapping(value = "")
    public ResponseInfo create(@RequestBody SMenu sMenu) {
        //效验参数
        String[] checkFiled = {"sMenu", "name", "type", "perms", "sortIndex"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, sMenu, sMenu.getName(), sMenu.getType(), sMenu.getPerms(), sMenu.getSortIndex());
        //授权标识不能重复
        AssertUtils.isNull(MENU_PERMS_EXIST, sMenuService.findByPerms(sMenu.getPerms()));
        AssertUtils.isContain(PARAMS_EXCEPTION, sMenu.getType(), SMenuDict.TYPE_DIR, SMenuDict.TYPE_MENU, SMenuDict.TYPE_BUTTON);
        //上级菜单效验
        if (!String.valueOf(SMenuDict.TYPE_DIR).equals(sMenu.getType())) {
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"parentId"}, sMenu.getParentId());
            SMenu parentSMenu = sMenuService.findById(sMenu.getParentId());
            AssertUtils.notNull(PARAMS_EXCEPTION, parentSMenu);
            if (String.valueOf(SMenuDict.TYPE_DIR).equals(parentSMenu.getType())) {
                AssertUtils.isTrue(PARAMS_EXCEPTION, String.valueOf(SMenuDict.TYPE_MENU).equals(sMenu.getType()));
            } else {
                AssertUtils.isTrue(PARAMS_EXCEPTION, String.valueOf(SMenuDict.TYPE_BUTTON).equals(sMenu.getType()));
            }
        } else {
            AssertUtils.isNull(PARAMS_EXCEPTION, sMenu.getParentId());
        }
        sMenuService.save(sMenu);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("sMenu", sMenu);
        return responseInfo;
    }

    @ApiOperation(value = "修改权限信息", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "sMenu", value = "权限信息") @RequestBody SMenu sMenu) {
        //效验参数
        String[] checkFiled = {"id", "sMenu", "name", "type", "perms", "sortIndex"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id, sMenu, sMenu.getName(), sMenu.getType(),
                sMenu.getPerms(), sMenu.getSortIndex());
        SMenu originSMenu = sMenuService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, originSMenu);
        if (!sMenu.getPerms().equals(originSMenu.getPerms())) {
            //修改授权标识后，效验不能重复
            AssertUtils.isNull(MENU_PERMS_EXIST, sMenuService.findByPerms(sMenu.getPerms()));
        }
        AssertUtils.isContain(PARAMS_EXCEPTION, sMenu.getType(), SMenuDict.TYPE_DIR, SMenuDict.TYPE_MENU, SMenuDict.TYPE_BUTTON);
        //上级菜单效验
        if (!String.valueOf(SMenuDict.TYPE_DIR).equals(sMenu.getType())) {
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"parentId"}, sMenu.getParentId());
            AssertUtils.isTrue(PARAMS_EXCEPTION, !Objects.equals(sMenu.getParentId(), id));
            SMenu parentSMenu = sMenuService.findById(sMenu.getParentId());
            AssertUtils.notNull(PARAMS_EXCEPTION, parentSMenu);
            if (String.valueOf(SMenuDict.TYPE_DIR).equals(parentSMenu.getType())) {
                AssertUtils.isTrue(PARAMS_EXCEPTION, String.valueOf(SMenuDict.TYPE_MENU).equals(sMenu.getType()));
            } else {
                AssertUtils.isTrue(PARAMS_EXCEPTION, String.valueOf(SMenuDict.TYPE_BUTTON).equals(sMenu.getType()));
            }
        } else {
            AssertUtils.isNull(PARAMS_EXCEPTION, sMenu.getParentId());
        }
        BeanUtils.copyPropertiesIgnoreNull(sMenu, originSMenu, "id");
        sMenuService.update(originSMenu);
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "权限列表", response = SMenuDto.class, responseContainer = "list", notes = "sMenuList")
    @GetMapping(value = "list")
    @RequiresPermissions("permission:edit:list")
    public ResponseInfo list() {
        List<SMenuDto> sMenuDtoList = sMenuService.findWholeAll();
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("sMenuList", sMenuDtoList);
        return responseInfo;
    }

    @ApiOperation(value = "权限组的权限列表", response = SMenuDto.class, responseContainer = "list", notes = "sMenuList")
    @GetMapping(value = "get/list")
    @RequiresPermissions("permission:group:manager:list")
    public ResponseInfo getList() {
        List<SMenuDto> sMenuDtoList = sMenuService.findWholeAll();
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("sMenuList", sMenuDtoList);
        return responseInfo;
    }
}
