package com.lky.mapper;

import com.lky.dto.UserBankCardDto;
import com.lky.entity.BankCard;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 用户、商家绑定的银行卡
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
@Mapper(componentModel = "jsr330")
public interface BankCardMapper {

    UserBankCardDto toDto(BankCard bankCard);

    List<UserBankCardDto> toListDto(List<BankCard> bankCardList);
}
