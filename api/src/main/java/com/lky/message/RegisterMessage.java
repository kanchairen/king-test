package com.lky.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户注册消息
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/4/2
 */
@Getter
@Setter
@ToString
public class RegisterMessage extends AbstractMessageObject implements Serializable {

    private static final long serialVersionUID = -3216338803403983522L;

    /**
     * 注册用户id
     */
    private Integer userId;

    /**
     * 注册用户获得的存量G米
     */
    private double lockWPoint;

    /**
     * 注册用户获得的G米
     */
    private double wpoint;

    /**
     * 注册用户推荐人获得的G米
     */
    private double recommendWPoint;
}
