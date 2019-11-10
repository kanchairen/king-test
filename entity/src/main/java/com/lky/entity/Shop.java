package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 商城店铺
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_shop")
@ApiModel(value = "Shop", description = "商城店铺")
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "所属行业")
    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;

    @ApiModelProperty(notes = "店铺名称")
    @Column(nullable = false, length = 128)
    private String name;

    @ApiModelProperty(notes = "店铺logo")
    @OneToOne
    @JoinColumn(name = "logo_img_id")
    private Image logoImg;

    @ApiModelProperty(notes = "店铺banner,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "banner_img_ids")
    private String bannerImgIds;

    @ApiModelProperty(notes = "联系QQ")
    @Column(length = 32)
    private String contactQq;

    @ApiModelProperty(notes = "联系电话")
    @Column(length = 32)
    private String contactPhone;

    @ApiModelProperty(notes = "接收通知手机号码")
    @Column(length = 32)
    private String notifyPhone;

    @ApiModelProperty(notes = "经度")
    @Column(length = 20)
    private String lng;

    @ApiModelProperty(notes = "维度")
    @Column(length = 20)
    private String lat;

    @ApiModelProperty(notes = "店铺地址")
    @Column(length = 1024)
    private String address;

    @ApiModelProperty(notes = "店铺描述")
    @Column(name = "description")
    private String description;

    @ApiModelProperty(notes = "分享文字")
    @Column(name = "share_text")
    private String shareText;

    @ApiModelProperty(notes = "分享标题")
    @Column(name = "share_title")
    private String shareTitle;

    @ApiModelProperty(notes = "分享图片")
    @OneToOne
    @JoinColumn(name = "share_img_id")
    private Image shareImg;

    @ApiModelProperty(notes = "店铺状态（默认0：关闭 1：开启）")
    @Column(nullable = false)
    private Boolean state = Boolean.FALSE;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

    @ApiModelProperty(notes = "店铺配置")
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "shop_config_id")
    private ShopConfig shopConfig;

    @ApiModelProperty(notes = "店铺开通资料")
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "shop_datum_id")
    private ShopDatum shopDatum;

    @ApiModelProperty(notes = "近期订单数量")
    @Column(nullable = false)
    private int recentSumOrder;
}
