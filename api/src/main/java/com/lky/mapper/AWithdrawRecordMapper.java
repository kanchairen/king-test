package com.lky.mapper;

import com.lky.dto.WithdrawRecordDto;
import com.lky.entity.AWithdrawRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 代理商大米提现记录
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-16
 */
@Mapper(componentModel = "jsr330")
public interface AWithdrawRecordMapper {

    @Mappings({
            @Mapping(source = "abankCard.mobile", target = "mobile"),
            @Mapping(source = "abankCard.realName", target = "realName"),
            @Mapping(source = "abankCard.cardNo", target = "cardNo"),
            @Mapping(source = "abankCard.bankcard", target = "bankcard"),
            @Mapping(source = "abankCard.cardName", target = "cardName"),
            @Mapping(source = "abankCard.bankName", target = "bankName"),
            @Mapping(source = "abankCard.bankArea", target = "bankArea"),
            @Mapping(source = "abankCard.branchName", target = "branchName"),
    })
    WithdrawRecordDto toDto(AWithdrawRecord aWithdrawRecord);

    List<WithdrawRecordDto> toDtoList(List<AWithdrawRecord> aWithdrawRecordList);
}
