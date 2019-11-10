package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统用户角色中间表
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_user_role")
@ApiModel(value = "SUserRole", description = "系统用户角色中间表")
public class SUserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "用户id")
    @Column(name = "s_user_id")
    private Integer userId;

    @ApiModelProperty(notes = "角色id")
    @Column(name = "s_role_id")
    private Integer roleId;
}
