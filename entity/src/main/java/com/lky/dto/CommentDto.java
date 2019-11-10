package com.lky.dto;

import com.lky.entity.Image;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 商品评论展示Dto
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/30
 */
public class CommentDto {

    @ApiModelProperty(notes = "数据库主键")
    private int id;

    @ApiModelProperty(notes = "用户头像")
    private Image userHead;

    @ApiModelProperty(notes = "用户昵称")
    private String nickname;

    @ApiModelProperty(notes = "订单号")
    private String ordersId;

    @ApiModelProperty(notes = "子订单项")
    private Integer ordersItemId;

    @ApiModelProperty(notes = "评分")
    private int score;

    @ApiModelProperty(notes = "评论内容")
    private String content;

    @ApiModelProperty(notes = "追加内容")
    private String appendContent;

    @ApiModelProperty(notes = "回复内容")
    private String reply;

    @ApiModelProperty(notes = "追加回复内容")
    private String appendReply;

    @ApiModelProperty(notes = "商品id")
    private int productId;

    @ApiModelProperty(notes = "商品组id")
    private int productGroupId;

    @ApiModelProperty(notes = "商品图片")
    private Image productImg;

    @ApiModelProperty(notes = "商品名称")
    private String productName;

    @ApiModelProperty(notes = "评论图片，可多张,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> commentImgList;

    @ApiModelProperty(notes = "追加评论图片，可多张,使用逗号隔开，OneToMany采用dto转，防止img表中出现多余字段")
    private List<Image> appendCommentImgList;

    @ApiModelProperty(notes = "购买时间")
    private Date buyTime;

    @ApiModelProperty(notes = "购买规格")
    private String spec;

    @ApiModelProperty(notes = "创建时间")
    private Date createTime = new Date();

    @ApiModelProperty(notes = "追加时间")
    private Date appendTime;

    @ApiModelProperty(notes = "回复时间")
    private Date replyTime;

    @ApiModelProperty(notes = "追加回复时间")
    private Date appendReplyTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Image getUserHead() {
        return userHead;
    }

    public void setUserHead(Image userHead) {
        this.userHead = userHead;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(String ordersId) {
        this.ordersId = ordersId;
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

    public String getAppendContent() {
        return appendContent;
    }

    public void setAppendContent(String appendContent) {
        this.appendContent = appendContent;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductGroupId() {
        return productGroupId;
    }

    public void setProductGroupId(int productGroupId) {
        this.productGroupId = productGroupId;
    }

    public Image getProductImg() {
        return productImg;
    }

    public void setProductImg(Image productImg) {
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<Image> getCommentImgList() {
        return commentImgList;
    }

    public void setCommentImgList(List<Image> commentImgList) {
        this.commentImgList = commentImgList;
    }

    public List<Image> getAppendCommentImgList() {
        return appendCommentImgList;
    }

    public void setAppendCommentImgList(List<Image> appendCommentImgList) {
        this.appendCommentImgList = appendCommentImgList;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getAppendTime() {
        return appendTime;
    }

    public void setAppendTime(Date appendTime) {
        this.appendTime = appendTime;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public Date getAppendReplyTime() {
        return appendReplyTime;
    }

    public void setAppendReplyTime(Date appendReplyTime) {
        this.appendReplyTime = appendReplyTime;
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", userHead=" + userHead +
                ", nickname='" + nickname + '\'' +
                ", ordersId='" + ordersId + '\'' +
                ", ordersItemId=" + ordersItemId +
                ", score=" + score +
                ", content='" + content + '\'' +
                ", appendContent='" + appendContent + '\'' +
                ", reply='" + reply + '\'' +
                ", appendReply='" + appendReply + '\'' +
                ", productId=" + productId +
                ", productGroupId=" + productGroupId +
                ", productImg=" + productImg +
                ", productName='" + productName + '\'' +
                ", commentImgList=" + commentImgList +
                ", appendCommentImgList=" + appendCommentImgList +
                ", buyTime=" + buyTime +
                ", spec='" + spec + '\'' +
                ", createTime=" + createTime +
                ", appendTime=" + appendTime +
                ", replyTime=" + replyTime +
                ", appendReplyTime=" + appendReplyTime +
                '}';
    }
}
