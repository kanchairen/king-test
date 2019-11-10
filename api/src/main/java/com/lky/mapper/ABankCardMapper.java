package com.lky.mapper;

import com.lky.dto.UserBankCardDto;
import com.lky.entity.ABankCard;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 代理商绑定的银行卡
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
@Mapper(componentModel = "jsr330")
public interface ABankCardMapper {

    UserBankCardDto toDto(ABankCard aBankCard);

    List<UserBankCardDto> toListDto(List<ABankCard> aBankCardList);
}
