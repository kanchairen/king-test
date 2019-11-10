package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户收货地址
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_receive_address")
@ApiModel(value = "ReceiveAddress", description = "用户收货地址")
public class ReceiveAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @Column(name = "m_user_id", nullable = false)
    private int userId;

    @ApiModelProperty(notes = "收件人姓名")
    @Column(nullable = false, length = 64)
    private String name;

    @ApiModelProperty(notes = "收件人手机号")
    @Column(nullable = false, length = 20)
    private String mobile;

    @ApiModelProperty(notes = "收件人地址", example = "{\"province\":{\"id\":2},\"city\":{\"id\":4},\"district\":{\"id\":393},\"detail\":\"九新公路\"}")
    @Column(nullable = false, length = 512)
    private String addressDetail;

    @ApiModelProperty(notes = "是否默认（默认0：否 1：是）")
    @Column(nullable = false)
    private Boolean first = Boolean.FALSE;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
