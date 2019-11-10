package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 基础配置
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_base_config")
@ApiModel(value = "BaseConfig", description = "基础配置")
public class BaseConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "客服联系电话")
    @Column(name = "service_tellphone")
    private String serviceTellphone;

    @ApiModelProperty(notes = "客服联系QQ")
    @Column(name = "service_qq")
    private String serviceQq;

    @ApiModelProperty(notes = "久天下设置的店铺")
    private Integer worldShopId;

    @ApiModelProperty(notes = "首页会员数量开关")
    private Boolean memberShowHome;
}
