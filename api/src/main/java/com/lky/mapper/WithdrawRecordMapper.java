package com.lky.mapper;

import com.lky.dto.WithdrawRecordDto;
import com.lky.entity.WithdrawRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 用户大米提现记录
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-16
 */
@Mapper(componentModel = "jsr330")
public interface WithdrawRecordMapper {

    @Mappings({
            @Mapping(source = "bankCard.mobile", target = "mobile"),
            @Mapping(source = "bankCard.realName", target = "realName"),
            @Mapping(source = "bankCard.cardNo", target = "cardNo"),
            @Mapping(source = "bankCard.bankcard", target = "bankcard"),
            @Mapping(source = "bankCard.cardName", target = "cardName"),
            @Mapping(source = "bankCard.bankName", target = "bankName"),
            @Mapping(source = "bankCard.bankArea", target = "bankArea"),
            @Mapping(source = "bankCard.branchName", target = "branchName"),
    })
    WithdrawRecordDto toDto(WithdrawRecord withdrawRecord);

    List<WithdrawRecordDto> toDtoList(List<WithdrawRecord> withdrawRecordList);
}
