package com.lky.entity;

import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 商品评论
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_comment")
@ApiModel(value = "Comment", description = "商品评论")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "app用户")
    @ManyToOne
    @JoinColumn(nullable = false, name = "m_user_id")
    private User user;

    @ApiModelProperty(notes = "订单号")
    @Column(length = 32)
    private String ordersId;

    @ApiModelProperty(notes = "子订单项")
    private Integer ordersItemId;

    @ApiModelProperty(notes = "评分")
    @Column(nullable = false)
    private int score;

    @ApiModelProperty(notes = "评论内容")
    @Column(length = 1024)
    private String content;

    @ApiModelProperty(notes = "追加内容")
    @Column(length = 1024)
    private String appendContent;

    @ApiModelProperty(notes = "回复内容")
    @Column(length = 1024)
    private String reply;

    @ApiModelProperty(notes = "追加回复内容")
    @Column(length = 1024)
    private String appendReply;

    @ApiModelProperty(notes = "商品id")
    @Column(nullable = false)
    private int productId;

    @ApiModelProperty(notes = "商品组id")
    @Column(nullable = false)
    private int productGroupId;

    @ApiModelProperty(notes = "评论图片，可多张,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "comment_img_ids")
    private String commentImgIds;

    @ApiModelProperty(notes = "追加评论图片，可多张,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    @Column(name = "append_comment_img_ids")
    private String appendCommentImgIds;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime = new Date();

    @ApiModelProperty(notes = "追加时间")
    private Date appendTime;

    @ApiModelProperty(notes = "回复时间")
    private Date replyTime;

    @ApiModelProperty(notes = "追加回复时间")
    private Date appendReplyTime;

    @ApiModelProperty(notes = "购买时间")
    private Date buyTime;
}
