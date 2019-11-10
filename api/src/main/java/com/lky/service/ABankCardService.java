package com.lky.service;

import com.alibaba.fastjson.JSONObject;
import com.lky.commons.bank.BankUtils;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.ABankCardDao;
import com.lky.dto.BankCardDto;
import com.lky.dto.BankResultDto;
import com.lky.entity.ABankCard;
import com.lky.entity.AUser;
import com.lky.enums.code.UserResCode;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.code.UserResCode.BANK_CARD_INFO_ERROR;

/**
 * 代理商绑定的银行卡Service
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-22
 */
@Service
public class ABankCardService extends BaseService<ABankCard, Integer> {

    @Inject
    private ABankCardDao aBankCardDao;

    @Override
    public BaseDao<ABankCard, Integer> getBaseDao() {
        return this.aBankCardDao;
    }

    /**
     * 绑定新的银行卡
     *
     * @param aUser        用户
     * @param bankCardDto 银行卡信息
     */
    public void create(AUser aUser, BankCardDto bankCardDto) {
        String mobile = bankCardDto.getMobile();
        String realName = bankCardDto.getRealName();
        String cardNo = bankCardDto.getCardNo();
        String bankcard = bankCardDto.getBankcard();

        //校验银行卡四要素是否匹配（手机号、银行卡号、身份证号、姓名）
        String jsonBank = BankUtils.checkFour(mobile, bankcard, cardNo, realName);
        AssertUtils.notNull(BANK_CARD_INFO_ERROR, jsonBank);

        //如果绑定银行卡已经存在，则不添加新的记录
        ABankCard bind = findByUserAndCard(aUser, bankcard, true);
        AssertUtils.isNull(UserResCode.BANK_CARD_HAS_BIND, bind);

        ABankCard notBind = findByUserAndCard(aUser, bankcard, false);
        if (notBind != null) {
            notBind.setBind(true);
            notBind.setMobile(bankCardDto.getMobile());
            notBind.setBankArea(bankCardDto.getBankArea());
            notBind.setBranchName(bankCardDto.getBranchName());
            super.update(notBind);
        } else {
            BankResultDto bankResultDto = JSONObject.parseObject(jsonBank, BankResultDto.class);
            ABankCard aBankCard = toABankCard(bankResultDto);
            aBankCard.setAUser(aUser);
            aBankCard.setBankArea(bankCardDto.getBankArea());
            aBankCard.setBranchName(bankCardDto.getBranchName());
            aBankCard.setMobile(bankCardDto.getMobile());
            aBankCard.setBankcard(bankCardDto.getBankcard());
            aBankCard.setCardNo(bankCardDto.getCardNo());
            aBankCard.setRealName(bankCardDto.getRealName());
            super.save(aBankCard);
        }
    }

    /**
     * 查找用户绑定的银行卡信息
     *
     * @param aUser 用户
     * @return 绑定的银行卡列表
     */
    public List<ABankCard> findByUser(AUser aUser) {
        SimpleSpecificationBuilder<ABankCard> builder = new SimpleSpecificationBuilder<>();
        builder.add("aUser", SpecificationOperator.Operator.eq, aUser);
        builder.add("bind", SpecificationOperator.Operator.eq, Boolean.TRUE);

        return super.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "id"));
    }

    /**
     * 查找用户银行卡有没有添加过
     *
     * @param aUser     用户
     * @param bankcard 银行卡号
     * @param bind     是否绑定
     * @return 银行卡信息
     */
    public ABankCard findByUserAndCard(AUser aUser, String bankcard, Boolean bind) {
        SimpleSpecificationBuilder<ABankCard> builder = new SimpleSpecificationBuilder<>();
        builder.add("aUser", SpecificationOperator.Operator.eq, aUser);
        builder.add("bankcard", SpecificationOperator.Operator.eq, bankcard);
        if (bind != null) {
            builder.add("bind", SpecificationOperator.Operator.eq, bind);
        }
        List<ABankCard> aBankCardList = super.findAll(builder.generateSpecification());
        if (!CollectionUtils.isEmpty(aBankCardList)) {
            return aBankCardList.get(0);
        }
        return null;
    }

    private ABankCard toABankCard(BankResultDto bankResultDto) {
        ABankCard aBankCard = new ABankCard();
        aBankCard.setBankNum(bankResultDto.getBanknum());
        aBankCard.setBankName(bankResultDto.getBankname());
        aBankCard.setCardPrefixNum(bankResultDto.getCardprefixnum());
        aBankCard.setCardName(bankResultDto.getCardname());
        aBankCard.setCardType(bankResultDto.getCardtype());
        aBankCard.setCardPrefixLength(bankResultDto.getCardprefixlength());
        aBankCard.setCardLength(bankResultDto.getCardlength());
        aBankCard.setIsLuhn(bankResultDto.getLuhn());
        aBankCard.setIsCreditCard(bankResultDto.getIscreditcard());
        aBankCard.setBankUrl(bankResultDto.getBankurl());
        aBankCard.setEnBankName(bankResultDto.getEnbankname());
        aBankCard.setAbbreviation(bankResultDto.getAbbreviation());
        aBankCard.setBankImage(bankResultDto.getBankimage());
        aBankCard.setServicePhone(bankResultDto.getServicephone());
        return aBankCard;
    }
}
