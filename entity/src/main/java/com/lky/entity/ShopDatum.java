package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 店铺资料
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_shop_datum")
@ApiModel(value = "ShopDatum", description = "店铺资料")
public class ShopDatum extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "开通店铺费用")
    @Column(name = "open_shop_fee", columnDefinition = "decimal(14,2)")
    private double openShopFee;

    @ApiModelProperty(notes = "开通店铺有效期")
    @Column(name = "open_shop_expire")
    private Date openShopExpire;

    @ApiModelProperty(notes = "营业执照（可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "license_img_ids")
    private String licenseImgIds;

}
