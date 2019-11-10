package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

/**
 * 运费规则,指定sort_index为索引
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_freight_rule", indexes = {@Index(name = "freight_rule_sort_index", columnList = "sort_index")})
@ApiModel(value = "FreightRule", description = "运费规则")
public class FreightRule extends BaseEntity implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "基本件数（重量、体积")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double base = 0;

    @ApiModelProperty(notes = "基本运费")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double basePrice = 0;

    @ApiModelProperty(notes = "追加件数（重量、体积，即超出多少件（kg、m3）为追加运费）")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double extra = 0;

    @ApiModelProperty(notes = "追加运费")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double extraPrice = 0;

    @ApiModelProperty(notes = "运费模板id")
    @Column(name = "freight_template_id")
    private Integer freightTemplateId;

    @ApiModelProperty(notes = "排序")
    @Column(name = "sort_index")
    private int sortIndex;

    @ApiModelProperty(notes = "城市运费规则,单向多对多")
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "m_freight_rule_city",
            joinColumns = {@JoinColumn(name = "freight_rule_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "city_id", referencedColumnName = "id")}
    )
    private Set<Area> citySet;

    //用于创建新的对象
    @Override
    public Object clone() {

        Object object = null;

        try {
            object = super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }
}
