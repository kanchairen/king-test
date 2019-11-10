package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户退款记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_orders_return")
@ApiModel(value = "MOrdersReturn", description = "用户退款记录")
public class OrdersReturn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "退款状态（申请、同意、拒绝）", allowableValues = "apply,agree,refuse")
    @Column(nullable = false, length = 32)
    private String state;

    @ApiModelProperty(notes = "退款金额")
    @Column(nullable = false, columnDefinition = "decimal(14,2)")
    private double price;

    @ApiModelProperty(notes = "退款小米")
    @Column(name = "rpoint_price", columnDefinition = "decimal(14,2)")
    private double rpointPrice;

    @ApiModelProperty(notes = "退款G米")
    @Column(name = "wpoint_price", columnDefinition = "decimal(14,2)")
    private double wpointPrice;

    @ApiModelProperty(notes = "订单号")
    @Column(nullable = false, length = 32)
    private String ordersId;

    @ApiModelProperty(notes = "子订单项")
    @Column(name = "orders_item_id")
    private Integer ordersItemId;

    @ApiModelProperty(notes = "店铺id")
    @Column(name = "shop_id", nullable = false)
    private Integer shopId;

    @ApiModelProperty(notes = "app用户")
    @Column(name = "m_user_id")
    private Integer userId;

    @ApiModelProperty(notes = "退款类型")
    private String returnType;

    @ApiModelProperty(notes = "退款理由")
    private String returnReason;

    @ApiModelProperty(notes = "退款追加理由")
    @Column(name = "return_reason_append")
    private String returnReasonAppend;

    @ApiModelProperty(notes = "商家回复理由")
    private String replyReason;

    @ApiModelProperty(notes = "商家回复追加理由")
    @Column(name = "reply_reason_append")
    private String replyReasonAppend;

    @ApiModelProperty(notes = "证据图片")
    @Column(name = "proof_img_ids")
    private String proofImgIds;

    @ApiModelProperty(notes = "退换货说明")
    private String remark;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
