package com.lky.dto;

import com.lky.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * 关联用户dto
 *
 * @author luckyhua
 * @version 1.0
 * @since 2018/3/6
 */
@Setter
@Getter
public class RelateUserDto {

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    private Page<User> childUserList;

}
