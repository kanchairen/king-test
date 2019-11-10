package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 物流模板
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_freight_template")
@ApiModel(value = "FreightTemplate", description = "物流模板")
public class FreightTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "模板名称")
    @Column(nullable = false, length = 120)
    private String name;

    @ApiModelProperty(notes = "发货地址")
    @Column(nullable = false, length = 512)
    private String sendAddress;

    @ApiModelProperty(notes = "配送时间")
    @Column(nullable = false)
    private int deliveryTime;

    @ApiModelProperty(notes = "价格类型（计件、重量、体积）", allowableValues = "num,weight,volume")
    @Column(nullable = false)
    private String priceType;

    @ApiModelProperty(notes = "店铺id")
    @Column(nullable = false)
    private int shopId;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

    @ApiModelProperty(notes = "运费规则列表，单向一对多")
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "freight_template_id")
    private List<FreightRule> freightRuleList;
}
