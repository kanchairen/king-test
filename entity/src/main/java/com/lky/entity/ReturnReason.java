package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 退货理由
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_return_reason")
@ApiModel(value = "ReturnReason", description = "退货理由")
public class ReturnReason extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "退款类型（仅退款，换货，退款退货，商家回复）", allowableValues = "refund,return,allReturn,reply")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "退款理由")
    @Column(name = "content")
    private String content;
}
