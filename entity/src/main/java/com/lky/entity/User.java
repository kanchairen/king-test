package com.lky.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lky.commons.base.BaseEntity;
import com.lky.enums.dict.AuthRecordDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * 商城用户表
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "m_user")
@ApiModel(value = "User", description = "商城用户表")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ApiModelProperty(notes = "父id")
    private Integer parentId;

    @ApiModelProperty(notes = "登录用户名")
    @Column(nullable = false, length = 32)
    private String username;

    @ApiModelProperty(notes = "手机号")
    @Column(nullable = false, length = 20)
    private String mobile;

    @ApiModelProperty(notes = "登录密码")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ApiModelProperty(notes = "用户角色（可多个 consumer：消费者，agent:推广员，up_agent:高级推广员，merchant：商家）")
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Set<UserRole> userRoleSet;

    @ApiModelProperty(notes = "真实姓名")
    @Column(length = 32)
    private String realName;

    @ApiModelProperty(notes = "身份证号")
    @Column(length = 18)
    private String cardNumber;

    @ApiModelProperty(notes = "用户实名认证状态(unauthorized：未认证，apply：申请，agree：认证通过，refuse：认证被拒绝)")
    private String authState = AuthRecordDict.STATE_UNAUTHORIZED.getKey();

    @ApiModelProperty(notes = "昵称")
    @Column(length = 64)
    private String nickname;

    @ApiModelProperty(notes = "邮箱")
    @Column(length = 64)
    private String email;

    @ApiModelProperty(notes = "头像id")
    @OneToOne
    @JoinColumn(name = "avatar_img_id")
    private Image avatarImage;

    @ApiModelProperty(notes = "系统生成8位的唯一推荐码，可用于分享")
    @Column(length = 8)
    private String recommendCode;

    @ApiModelProperty(notes = "注册来源方式（android,ios,pc,h5页面）", allowableValues = "android,ios,ps,h5")
    @Column(length = 32)
    private String registerSource;

    @ApiModelProperty(notes = "注册ip地址")
    @Column(length = 20)
    private String registerIp;

    @ApiModelProperty(notes = "游览器头部信息")
    @Column
    private String registerAgent;

    @ApiModelProperty(notes = "是否冻结（默认0：不冻结，1：冻结）")
    @Column()
    private Boolean locked = Boolean.FALSE;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "更新时间")
    private Date updateTime;

    @ApiModelProperty(notes = "用户资产")
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_asset_id", referencedColumnName = "id")
    private UserAsset userAsset;

    @ApiModelProperty(notes = "用户性别")
    @Column(length = 4)
    private String sex;

    @ApiModelProperty(notes = "用户地区")
    @Column(length = 128)
    private String area;

    @ApiModelProperty(notes = "支付密码")
    @JsonIgnore
    private String payPwd;

    @ApiModelProperty(notes = "角色类型(consumer消费者，merchant 商家)")
    @Column(length = 32)
    private String roleType;

    @ApiModelProperty(notes = "是否开通余粮公社")
    private Boolean openSurplusGrain = Boolean.FALSE;

    @ApiModelProperty(notes = "大米自动转余粮公社")
    private Boolean automaticSurplusGrain = Boolean.FALSE;
}
