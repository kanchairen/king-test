package com.lky.mapper;

import com.lky.commons.utils.ArithUtils;
import com.lky.commons.utils.JsonUtils;
import com.lky.dto.AgentArea;
import com.lky.dto.AgentDto;
import com.lky.entity.AUser;
import com.lky.entity.AUserAsset;
import com.lky.entity.AUserInfo;
import com.lky.utils.BeanUtils;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理商Dto转换
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/12/22
 */
@Mapper(componentModel = "jsr330")
public abstract class AgentMapper {

    public List<AgentDto> toDtoList(List<AUser> aUserList) {
        ArrayList<AgentDto> agentDtoList = new ArrayList<>();
        for (AUser aUser : aUserList) {
            agentDtoList.add(this.toDto(aUser));
        }
        return agentDtoList;
    }

    public AgentDto toDto(AUser aUser) {
        if (aUser == null) {
            return null;
        }
        AgentDto agentDto = new AgentDto();
        BeanUtils.copyPropertiesIgnoreNull(aUser, agentDto);
        AUserInfo aUserInfo = aUser.getAUserInfo();
        AUserAsset aUserAsset = aUser.getAUserAsset();
        agentDto.setArea(JsonUtils.jsonToObject(aUserInfo.getArea(), AgentArea.class));
        BeanUtils.copyPropertiesIgnoreNull(aUserInfo, agentDto, "id", "area");

        agentDto.setSumBackAmount(aUserAsset.getSumBackAmount());
        //待倒扣金额大于等于0
        double waitBackAmount = ArithUtils.round(aUserInfo.getAmount() - aUserInfo.getPayAmount() - aUserAsset.getSumBackAmount(), 2);
        agentDto.setWaitBackAmount(waitBackAmount > 0 ? waitBackAmount : 0);

        return agentDto;
    }
}
