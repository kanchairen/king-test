package com.lky.modules.api;

import com.lky.LkyApplication;
import com.lky.commons.utils.JsonUtils;
import com.lky.dto.UserBankCardDto;
import com.lky.entity.BankCard;
import com.lky.entity.User;
import com.lky.mapper.BankCardMapper;
import com.lky.service.BankCardService;
import com.lky.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

/**
 * 银行卡列表测试类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 17-11-15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LkyApplication.class)
public class BankCardTest {

    @Inject
    private UserService userService;

    @Inject
    private BankCardService bankCardService;

    @Inject
    private BankCardMapper bankCardMapper;

    @Test
    public void listBank() {

        User user = userService.findById(3);

        List<BankCard> cardList = bankCardService.findByUser(user);

        List<UserBankCardDto> bankCardDtos = bankCardMapper.toListDto(cardList);
        System.out.println(JsonUtils.objectToJson(cardList));
        System.out.println(JsonUtils.objectToJson(bankCardDtos));
    }
}
