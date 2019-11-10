package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 代理商成员
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/12/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_user_member")
@ApiModel(value = "AUserMember", description = "代理商成员")
public class AUserMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(value = "代理商id")
    @Column(nullable = false, name = "a_user_id")
    private Integer aUserId;

    @ApiModelProperty(value = "代理职位(董事长,总经理,总监,经理,运营团队,GP)",
            allowableValues = "chairman,general_manager,director,manager,operation,gp")
    @Column(name = "position", length = 32)
    private String position;

    @ApiModelProperty(value = "姓名")
    @Column(length = 32)
    private String name;

    @ApiModelProperty(value = "手机号码")
    @Column(length = 20)
    private String mobile;

    @ApiModelProperty(value = "股份")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double unit;

    @ApiModelProperty(value = "管理股份")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double manageUnit;

    @ApiModelProperty(value = "出资金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double payAmount;

    @ApiModelProperty(value = "管理金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double manageAmount;
}
