package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 天时卡
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_climate_card_link")
@ApiModel(value = "ClimateCardLink", description = "天时卡")
public class ClimateCardLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "类型，购买、市场行情", allowableValues = "buy, quotations")
    @Column(nullable = false, length = 20)
    private String type;

    @ApiModelProperty(notes = "Link链接")
    @Column(nullable = false)
    private String Link;

}
