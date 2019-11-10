package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 创建商品评论dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/30
 */
public class CreateComment {

    @ApiModelProperty(notes = "数据库主键")
    private Integer id;

    @ApiModelProperty(notes = "子订单号")
    private Integer ordersItemId;

    @ApiModelProperty(notes = "评分")
    private int score;

    @ApiModelProperty(notes = "评论内容")
    private String content;

    @ApiModelProperty(notes = "评论图片，可多张,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> commentImgList;

    @ApiModelProperty(notes = "商家回复内容")
    private String reply;

    @ApiModelProperty(notes = "商家追加回复内容")
    private String appendReply;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrdersItemId() {
        return ordersItemId;
    }

    public void setOrdersItemId(Integer ordersItemId) {
        this.ordersItemId = ordersItemId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Image> getCommentImgList() {
        return commentImgList;
    }

    public void setCommentImgList(List<Image> commentImgList) {
        this.commentImgList = commentImgList;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getAppendReply() {
        return appendReply;
    }

    public void setAppendReply(String appendReply) {
        this.appendReply = appendReply;
    }

    @Override
    public String toString() {
        return "CreateComment{" +
                "id=" + id +
                ", ordersItemId=" + ordersItemId +
                ", score=" + score +
                ", content='" + content + '\'' +
                ", commentImgList=" + commentImgList +
                ", reply='" + reply + '\'' +
                ", appendReply='" + appendReply + '\'' +
                '}';
    }
}
