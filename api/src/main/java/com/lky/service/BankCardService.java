package com.lky.service;

import com.alibaba.fastjson.JSONObject;
import com.lky.commons.bank.BankUtils;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dao.BankCardDao;
import com.lky.dto.BankCardDto;
import com.lky.dto.BankResultDto;
import com.lky.entity.BankCard;
import com.lky.entity.User;
import com.lky.enums.code.UserResCode;
import com.lky.utils.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.code.UserResCode.BANK_CARD_INFO_ERROR;
import static com.lky.global.constant.Constant.DEFAULT_BANK_IMAGE_LOGO;

/**
 * 银行卡
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@Service
public class BankCardService extends BaseService<BankCard, Integer> {

    @Inject
    private BankCardDao bankCardDao;

    @Override
    public BaseDao<BankCard, Integer> getBaseDao() {
        return this.bankCardDao;
    }

    /**
     * app端用户绑定新的银行卡
     *
     * @param user        用户
     * @param bankCardDto 银行卡信息
     */
    public void create(User user, BankCardDto bankCardDto) {
        String mobile = bankCardDto.getMobile();
        String realName = bankCardDto.getRealName();
        String cardNo = bankCardDto.getCardNo();
        String bankcard = bankCardDto.getBankcard();

        //校验银行卡四要素是否匹配（手机号、银行卡号、身份证号、姓名）
        String jsonBank = BankUtils.checkFour(mobile, bankcard, cardNo, realName);
        AssertUtils.notNull(BANK_CARD_INFO_ERROR, jsonBank);

        //如果绑定银行卡已经存在，则不添加新的记录
        BankCard bind = findByUserAndCard(user, bankcard, true);
        AssertUtils.isNull(UserResCode.BANK_CARD_HAS_BIND, bind);

        BankCard notBind = findByUserAndCard(user, bankcard, false);
        if (notBind != null) {
            notBind.setBind(true);
            notBind.setMobile(bankCardDto.getMobile());
            notBind.setBankArea(bankCardDto.getBankArea());
            notBind.setBranchName(bankCardDto.getBranchName());
            super.update(notBind);
        } else {
            BankResultDto bankResultDto = JSONObject.parseObject(jsonBank, BankResultDto.class);
            BankCard bankCard = toBankCard(bankResultDto);
            bankCard.setUser(user);
            bankCard.setBankArea(bankCardDto.getBankArea());
            bankCard.setBranchName(bankCardDto.getBranchName());
            bankCard.setMobile(bankCardDto.getMobile());
            bankCard.setBankcard(bankCardDto.getBankcard());
            bankCard.setCardNo(bankCardDto.getCardNo());
            bankCard.setRealName(bankCardDto.getRealName());
            if (StringUtils.isEmpty(bankCard.getBankImage())) {
                bankCard.setBankImage(DEFAULT_BANK_IMAGE_LOGO);
            }
            super.save(bankCard);
        }
    }

    /**
     * 查找用户绑定的银行卡信息
     *
     * @param user 用户
     * @return 绑定的银行卡列表
     */
    public List<BankCard> findByUser(User user) {
        SimpleSpecificationBuilder<BankCard> builder = new SimpleSpecificationBuilder<>();
        builder.add("user", SpecificationOperator.Operator.eq, user);
        builder.add("bind", SpecificationOperator.Operator.eq, Boolean.TRUE);

        return super.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "id"));
    }

    /**
     * 查找用户银行卡有没有添加过
     *
     * @param user     用户
     * @param bankcard 银行卡号
     * @param bind     是否绑定
     * @return 银行卡信息
     */
    public BankCard findByUserAndCard(User user, String bankcard, Boolean bind) {
        SimpleSpecificationBuilder<BankCard> builder = new SimpleSpecificationBuilder<>();
        builder.add("user", SpecificationOperator.Operator.eq, user);
        builder.add("bankcard", SpecificationOperator.Operator.eq, bankcard);
        if (bind != null) {
            builder.add("bind", SpecificationOperator.Operator.eq, bind);
        }
        List<BankCard> bankCardList = super.findAll(builder.generateSpecification());
        if (!CollectionUtils.isEmpty(bankCardList)) {
            return bankCardList.get(0);
        }
        return null;
    }

    private BankCard toBankCard(BankResultDto bankResultDto) {
        BankCard bankCard = new BankCard();
        bankCard.setBankNum(bankResultDto.getBanknum());
        bankCard.setBankName(bankResultDto.getBankname());
        bankCard.setCardPrefixNum(bankResultDto.getCardprefixnum());
        bankCard.setCardName(bankResultDto.getCardname());
        bankCard.setCardType(bankResultDto.getCardtype());
        bankCard.setCardPrefixLength(bankResultDto.getCardprefixlength());
        bankCard.setCardLength(bankResultDto.getCardlength());
        bankCard.setIsLuhn(bankResultDto.getLuhn());
        bankCard.setIsCreditCard(bankResultDto.getIscreditcard());
        bankCard.setBankUrl(bankResultDto.getBankurl());
        bankCard.setEnBankName(bankResultDto.getEnbankname());
        bankCard.setAbbreviation(bankResultDto.getAbbreviation());
        bankCard.setBankImage(bankResultDto.getBankimage());
        bankCard.setServicePhone(bankResultDto.getServicephone());
        return bankCard;
    }

    /**
     * 系统管理员给用户绑定新的银行卡
     *
     * @param user        用户
     * @param bankCardDto 银行卡信息
     */
    public void sysCreate(User user, BankCardDto bankCardDto) {
        String bankcard = bankCardDto.getBankcard();
        //如果绑定银行卡已经存在，则不添加新的记录
        BankCard bind = findByUserAndCard(user, bankcard, true);
        AssertUtils.isNull(UserResCode.BANK_CARD_HAS_BIND, bind);
        BankCard notBind = findByUserAndCard(user, bankcard, false);
        //获取绑定银行的相关信息
        SimpleSpecificationBuilder<BankCard> builder = new SimpleSpecificationBuilder<>();
        builder.add("bankName", SpecificationOperator.Operator.likeAll, bankCardDto.getBankName().trim());
        builder.add("bankImage", SpecificationOperator.Operator.isNotNull, "bankImage");
        Pageable pager = new PageRequest(0, 1);
        List<BankCard> bankCardList = super.findAll(builder.generateSpecification(), pager).getContent();
        BankCard modelCard = null;
        if (!CollectionUtils.isEmpty(bankCardList)) {
            modelCard = bankCardList.get(0);
        }
        if (notBind != null) {
            notBind.setBind(true);
            if (modelCard != null) {
                BeanUtils.copyPropertiesIgnoreNull(modelCard, notBind, "id", "user", "mobile", "bankcard",
                        "cardNo", "realName", "bankArea", "branchName", "cardPrefixNum", "cardName", "cardType",
                        "cardPrefixLength", "cardLength", "isLuhn", "isCreditCard", "bind");
            } else {
                notBind.setBankImage(null);
            }
            BeanUtils.copyPropertiesIgnoreNull(bankCardDto, notBind);
            if (StringUtils.isEmpty(notBind.getBankImage())) {
                notBind.setBankImage(DEFAULT_BANK_IMAGE_LOGO);
            }
            super.update(notBind);
        } else {
            BankCard bankCard = new BankCard();
            if (modelCard != null) {
                BeanUtils.copyPropertiesIgnoreNull(modelCard, bankCard, "id", "user", "mobile", "bankcard",
                        "cardNo", "realName", "bankArea", "branchName", "cardPrefixNum", "cardName", "cardType",
                        "cardPrefixLength", "cardLength", "isLuhn", "isCreditCard", "bind");
            }
            BeanUtils.copyPropertiesIgnoreNull(bankCardDto, bankCard);
            bankCard.setUser(user);
            if (StringUtils.isEmpty(bankCard.getBankImage())) {
                bankCard.setBankImage(DEFAULT_BANK_IMAGE_LOGO);
            }
            super.save(bankCard);
        }
    }
}
