package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.SRoleDao;
import com.lky.dto.SRoleDto;
import com.lky.entity.SRole;
import com.lky.entity.SRoleMenu;
import com.lky.entity.SUserRole;
import com.lky.mapper.SRoleMapper;
import com.lky.utils.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统角色Service层
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-13
 */
@Service
public class SRoleService extends BaseService<SRole, Integer> {

    @Inject
    private SRoleDao sRoleDao;

    @Inject
    private SRoleMapper sRoleMapper;

    @Inject
    private SRoleMenuService sRoleMenuService;

    @Inject
    private SUserRoleService sUserRoleService;

    @Override
    public BaseDao<SRole, Integer> getBaseDao() {
        return sRoleDao;
    }

    /**
     * 创建新的系统角色
     *
     * @param sRoleDto 新的角色信息
     * @return 创建好的系统角色
     */
    public SRole create(SRoleDto sRoleDto) {
        SRole sRole = sRoleMapper.fromDto(sRoleDto);
        sRole.setCode(StringUtils.getUUID());
        super.save(sRole);

        List<SRoleMenu> sRoleMenuList = new ArrayList<>();
        sRoleDto.getsMenuList().forEach(sMenu -> {
            SRoleMenu sRoleMenu = new SRoleMenu();
            sRoleMenu.setMenuId(sMenu.getId());
            sRoleMenu.setRoleId(sRole.getId());
            sRoleMenuList.add(sRoleMenu);
        });
        sRoleMenuService.save(sRoleMenuList);
        return sRole;
    }

    /**
     * 删除系统角色，删除时，上下对应的中间表也同步删除
     *
     * @param sRole 需要删除的系统角色
     */
    public void remove(SRole sRole) {
        //删除系统角色与权限菜单中间表
        List<SRoleMenu> sRoleMenuList = sRoleMenuService.findByRoleId(sRole.getId());
        if (!CollectionUtils.isEmpty(sRoleMenuList)) {
            sRoleMenuService.delete(sRoleMenuList);
        }

        //删除用户与系统角色中间表
        List<SUserRole> sUserRoleList = sUserRoleService.findByRoleId(sRole.getId());
        if (!CollectionUtils.isEmpty(sUserRoleList)) {
            sUserRoleService.delete(sUserRoleList);
        }

        //删除系统角色
        super.delete(sRole);
    }

    /**
     * 编辑系统角色
     *
     * @param sRoleDto 新的系统角色信息
     * @param sRole    原来的系统角色信息
     */
    public void edit(SRoleDto sRoleDto, SRole sRole) {
        BeanUtils.copyPropertiesIgnoreNull(sRoleDto, sRole, "id", "code", "sMenuList");
        super.update(sRole);

        //更新系统角色与权限菜单中间表
        if (!CollectionUtils.isEmpty(sRoleDto.getsMenuList())) {
            //删除久的系统角色与权限菜单中间表
            List<SRoleMenu> oldSRoleMenuList = sRoleMenuService.findByRoleId(sRole.getId());
            if (!CollectionUtils.isEmpty(oldSRoleMenuList)) {
                sRoleMenuService.delete(oldSRoleMenuList);
            }

            //添加的系统角色与权限菜单中间表
            List<SRoleMenu> newSRoleMenuList = new ArrayList<>();
            sRoleDto.getsMenuList().forEach(sMenu -> {
                SRoleMenu sRoleMenu = new SRoleMenu();
                sRoleMenu.setRoleId(sRole.getId());
                sRoleMenu.setMenuId(sMenu.getId());
                newSRoleMenuList.add(sRoleMenu);
            });
            sRoleMenuService.save(newSRoleMenuList);
        }
    }

    /**
     * 判断系统角色名是否已经存在
     *
     * @param name 名称
     * @param id   需要去除的系统角色的id
     * @return boolean
     */
    public Boolean isExit(String name, Integer id) {
        Specification<SRole> spec = ((root, query, cb) -> {
            List<Predicate> p = Lists.newArrayList();
            p.add(cb.equal(root.get("name"), name));
            if (id != null) {
                p.add(cb.notEqual(root.get("id"), id));
            }
            return cb.and(p.toArray(new Predicate[p.size()]));
        });
        return count(spec) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 查找系统角色列表
     *
     * @param pageable 分页信息
     * @return 系统角色列表
     */
    public Page<SRole> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    /**
     * 根据用户id查找其权限列表
     *
     * @param userId 用户id
     * @return 权限列表
     */
    public List<SRole> findByUserId(Integer userId) {
        return sRoleDao.selectByUserId(userId);
    }
}
