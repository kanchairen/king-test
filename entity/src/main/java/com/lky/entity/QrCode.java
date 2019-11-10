package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 活动二维码，设置现金对应G米比例
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_qr_code")
@ApiModel(value = "QrCode", description = "活动二维码")
public class QrCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "开启/关闭")
    @Column(nullable = false)
    private Boolean state;

    @ApiModelProperty(notes = "活动比例")
    @Column(columnDefinition = "decimal(14,2)", nullable = false)
    private double rate;

    @ApiModelProperty(notes = "活动开始时间")
    @Column(nullable = false)
    private Date beginTime;

    @ApiModelProperty(notes = "活动结束时间")
    @Column(nullable = false)
    private Date endTime;

    @ApiModelProperty(notes = "二维码对应唯一code")
    @Column(nullable = false)
    private String code;

    @ApiModelProperty(notes = "是否加入到代理商收益中计算")
    @Column(nullable = false)
    private Boolean calculated;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "是否跳转URL连接")
    private Boolean redirect;

    @ApiModelProperty(notes = "跳转URL门槛（元）")
    @Column(columnDefinition = "decimal(14,2)")
    private Double threshold;

    @ApiModelProperty(notes = "跳转URL")
    private String url;
}
