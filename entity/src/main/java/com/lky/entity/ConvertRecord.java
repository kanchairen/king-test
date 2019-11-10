package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * G米转换记录
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_convert_record")
@ApiModel(value = "ConvertRecord", description = "G米转换记录")
public class ConvertRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "类型，系统自动、手动转换", allowableValues = "auto,manual")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "转换时的指数")
    @Column(name = "lhealth_index", nullable = false, columnDefinition = "decimal(14,4)")
    private double lhealthIndex;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();
}
