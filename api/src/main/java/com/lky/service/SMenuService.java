package com.lky.service;

import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.SMenuDao;
import com.lky.dto.SMenuDto;
import com.lky.entity.SMenu;
import com.lky.enums.dict.SMenuDict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;

/**
 * 系统菜单Service层
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@Service
public class SMenuService extends BaseService<SMenu, Integer> {

    @Inject
    private SMenuDao sMenuDao;

    @Override
    public BaseDao<SMenu, Integer> getBaseDao() {
        return sMenuDao;
    }

    /**
     * 查找所有系统菜单
     *
     * @return 系统菜单列表
     */
    public List<SMenu> findAll() {
        return super.findAll(new Sort(Sort.Direction.ASC, "sortIndex"));
    }

    /**
     * 查找所有系统菜单的id列表
     *
     * @return 系统菜单列表
     */
    public List<Integer> findAllIdList() {
        List<SMenu> sMenuList = findAll();
        return sMenuList.stream().map(SMenu::getId).collect(Collectors.toList());
    }

    /**
     * 根据系统角色id查找对应菜单列表
     *
     * @param sRoleId 系统角色id
     * @return 菜单列表
     */
    public List<SMenu> findBySRoleId(Integer sRoleId) {
        return sMenuDao.selectBySRoleId(sRoleId);
    }


    public SMenu findByPerms(String perms) {
        return sMenuDao.findByPerms(perms);
    }

    /**
     * 获取所有权限列表Dto 包含上下级关系
     *
     * @return 所有限列表Dto
     */
    public List<SMenuDto> findWholeAll() {
        List<SMenu> listAll = this.findAll();
        return this.findSMenuTree(listAll);
    }

    /**
     * 权限菜单结构整理o
     *
     * @param sMenuList 权限菜单
     * @return 权限列表dto
     */
    public List<SMenuDto> findSMenuTree(List<SMenu> sMenuList) {
        List<SMenuDto> sMenuDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sMenuList)) {
            for (SMenu sMenu : sMenuList) {
                //获取一级权限
                if (String.valueOf(SMenuDict.TYPE_DIR).equals(sMenu.getType())) {
                    SMenuDto dirDto = new SMenuDto();
                    List<SMenuDto> menuChild = new ArrayList<>();
                    dirDto.setChildList(menuChild);
                    dirDto.setsMenu(sMenu);
                    for (SMenu menu : sMenuList) {
                        //获取子权限
                        if (menu.getParentId() != null && menu.getParentId() == sMenu.getId()) {
                            SMenuDto menuDto = new SMenuDto();
                            List<SMenuDto> buttonChild = new ArrayList<>();
                            menuDto.setsMenu(menu);
                            menuDto.setChildList(buttonChild);
                            menuDto.setParent(sMenu);
                            for (SMenu buttonMenu : sMenuList) {
                                //获取按钮权限
                                if (buttonMenu.getParentId() != null && buttonMenu.getParentId() == menu.getId()) {
                                    SMenuDto buttonDto = new SMenuDto();
                                    buttonDto.setsMenu(buttonMenu);
                                    buttonDto.setParent(menu);
                                    buttonDto.setGrandpa(sMenu);
                                    buttonChild.add(buttonDto);
                                }
                            }
                            menuChild.add(menuDto);
                        }
                    }
                    sMenuDtoList.add(dirDto);
                }
            }
        }
        return sMenuDtoList;
    }

    /**
     * 根据sUserId查找其权限菜单
     *
     * @param sUserId 管理员id
     * @return 权限菜单列表
     */
    public List<SMenu> findBySUserId(Integer sUserId) {
        return sMenuDao.selectBySUserId(sUserId);
    }

    /**
     * 校验菜单列表是否存在 且结构完整（子菜单必须要有父级菜单权限）
     *
     * @param sMenuList 菜单列表
     */
    public void checkExistAndLink(List<SMenu> sMenuList) {
        List<SMenu> sMenuAll = super.findAll();
        AssertUtils.isTrue(PARAMS_EXCEPTION, !CollectionUtils.isEmpty(sMenuList));
        for (SMenu sMenu : sMenuList) {
            //校验菜单列表是否存在
            SMenu menu = sMenuAll
                    .stream()
                    .filter(t -> t.getId() == sMenu.getId())
                    .findFirst()
                    .orElse(null);
            AssertUtils.notNull(PARAMS_EXCEPTION, menu);
            //如果是子菜单，则需要包含其父菜单，形成权限链
            if (menu.getParentId() != null) {
                AssertUtils.isTrue(PARAMS_EXCEPTION, sMenuList
                        .stream()
                        .filter(t -> t.getId() == menu.getParentId())
                        .count() > 0);
            }
        }
    }
}
