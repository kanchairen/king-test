package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 申请开店记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_apply_record")
@ApiModel(value = "ApplyRecord", description = "申请开店记录")
public class ApplyRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "所属行业")
    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;

    @ApiModelProperty(notes = "店铺id,开通店铺后回传")
    private Integer shopId;

    @ApiModelProperty(notes = "店铺名称")
    @Column(name = "shop_name")
    private String shopName;

    @ApiModelProperty(notes = "店铺地址详情")
    @Column(name = "shop_address")
    private String shopAddress;

    @ApiModelProperty(notes = "店铺联系电话")
    @Column(name = "shop_contact_phone")
    private String shopContactPhone;

    @ApiModelProperty(notes = "店铺头像")
    @ManyToOne
    @JoinColumn(name = "shop_logo_img_id")
    private Image shopLogoImg;

    @ApiModelProperty(notes = "店铺头图 (可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "shop_banner_img_ids")
    private String shopBannerImgIds;

    @ApiModelProperty(notes = "营业执照（可多张）,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "shop_license_img_ids")
    private String shopLicenseImgIds;

    @ApiModelProperty(notes = "申请状态，未支付，申请中，同意，拒绝", allowableValues = "unpaid,apply,agree,refuse")
    @Column(nullable = false)
    private String state;

    @ApiModelProperty(notes = "经度")
    private String lat;

    @ApiModelProperty(notes = "维度")
    private String lng;

    @ApiModelProperty(notes = "开通申请店铺应付费用")
    @Column(nullable = false, name = "amount", columnDefinition = "decimal(14,2)")
    private double amount;

    @ApiModelProperty(notes = "申请开店总共的支付费用")
    @Column(nullable = false, name = "sum_paid_amount", columnDefinition = "decimal(14,2)")
    private double sumPaidAmount;

    @ApiModelProperty(notes = "申请备注")
    private String remark;

    @ApiModelProperty(notes = "审核备注（拒绝理由）")
    private String auditRemark;

    @ApiModelProperty(notes = "审核时间")
    private Date auditTime;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
