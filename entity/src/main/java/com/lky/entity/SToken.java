package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户token
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "s_token")
@ApiModel(value = "SToken", description = "用户token")
public class SToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "用户id")
    @Column(name="user_id", nullable = false)
    private Integer userId;

    @ApiModelProperty(notes = "token类型", allowableValues = "app,system")
    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "token")
    @Column(name = "token", nullable = false, length = 122)
    private String token;

    @ApiModelProperty(notes = "有效期")
    private Date expireTime;

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
