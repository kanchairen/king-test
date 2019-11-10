package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 店铺统计数据
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_shop_statistics")
@ApiModel(value = "ShopStatistics", description = "店铺统计数据")
public class ShopStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "店铺id")
    @Column(name = "shop_id", nullable = false)
    private int shopId;

    @ApiModelProperty(notes = "店铺访问量")
    @Column(nullable = false)
    private int pv;

    @ApiModelProperty(notes = "店铺转换率")
    @Column(nullable = false)
    private int uv;

    @ApiModelProperty(notes = "日期")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;
}
