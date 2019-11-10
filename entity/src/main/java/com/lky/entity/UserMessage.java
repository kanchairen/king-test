package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 商城用户消息通知
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_user_message")
@ApiModel(value = "UserMessage", description = "商城用户消息通知")
public class UserMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @Column(name = "m_user_id", nullable = false)
    private int userId;

    @ApiModelProperty(notes = "消息通知标题")
    @Column(nullable = false, length = 32)
    private String title;

    @ApiModelProperty(notes = "消息通知类型（系统类型）", allowableValues = "system")
    @Column(nullable = false, length = 32)
    private String type;

    @ApiModelProperty(notes = "消息通知目标id")
    @Column(name = "target_id")
    private String targetId;

    @ApiModelProperty(notes = "消息通知目标类型(成交、发货、提现、到账)",
            allowableValues = "conclude,send,applySuccess,applyFail,withdraw,toAccount")
    @Column(name = "target_type", length = 32)
    private String targetType;

    @ApiModelProperty(notes = "消息通知内容")
    @Column(name = "content", length = 1024)
    private String content;

    @ApiModelProperty(notes = "未读")
    private Boolean unread = Boolean.TRUE;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;
}
