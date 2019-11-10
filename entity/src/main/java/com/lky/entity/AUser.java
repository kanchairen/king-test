package com.lky.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lky.commons.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * 代理商
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "a_user")
@ApiModel(value = "AUser", description = "代理商")
public class AUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ApiModelProperty(notes = "父id")
    private Integer parentId;

    @ApiModelProperty(notes = "登录用户名")
    @Column(nullable = false, length = 32)
    private String username;

    @ApiModelProperty(notes = "登录密码，初始化密码12345678")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ApiModelProperty(notes = "登录手机号码")
    @Column(length = 20)
    private String mobile;

    @ApiModelProperty(notes = "董事长姓名，冗余字段")
    @Column(length = 32)
    private String chairmanName;

    @ApiModelProperty(notes = "董事长手机号码，冗余字段")
    @Column(length = 20)
    private String chairmanMobile;

    @ApiModelProperty(notes = "状态", allowableValues = "lock,active")
    @Column(length = 20, nullable = false)
    private String state;

    @ApiModelProperty(notes = "支付密码")
    @JsonIgnore
    private String payPwd;

    @ApiModelProperty(notes = "创建时间")
    @Column(nullable = false)
    private Date createTime = new Date();

    @ApiModelProperty(notes = "代理商资产")
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "a_user_asset_id", referencedColumnName = "id")
    private AUserAsset aUserAsset;

    @ApiModelProperty(notes = "代理商信息")
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "a_user_info_id", referencedColumnName = "id")
    private AUserInfo aUserInfo;
}
