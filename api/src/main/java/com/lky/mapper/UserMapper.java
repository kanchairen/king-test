package com.lky.mapper;

import com.lky.dto.UserInfoDto;
import com.lky.dto.UserRecommendDto;
import com.lky.entity.User;
import com.lky.enums.dict.RoleDict;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用户dto转换
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/6/6
 */
@Mapper(componentModel = "jsr330")
public abstract class UserMapper {

    @Mapping(source = "address", target = "area")
    public abstract User fromDto(UserInfoDto userInfoDto);

    public UserRecommendDto fromUser(User user) {
        if (user == null) {
            return null;
        }
        UserRecommendDto userRecommendDto = new UserRecommendDto();
        userRecommendDto.setName(user.getNickname());
        userRecommendDto.setMobile(user.getMobile());
        userRecommendDto.setMerchant(user.getUserRoleSet().stream().anyMatch(item ->
            RoleDict.CODE_MERCHANT.compare(item.getRole().getCode())));

        userRecommendDto.setId(user.getId());
        userRecommendDto.setAvatarImage(user.getAvatarImage());
        userRecommendDto.setCreateTime(user.getCreateTime());
        return userRecommendDto;
    }

    public abstract List<UserRecommendDto> fromUserList(List<User> userList);

}
