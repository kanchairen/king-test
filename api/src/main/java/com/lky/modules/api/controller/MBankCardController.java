package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.BankCardDto;
import com.lky.dto.UserBankCardDto;
import com.lky.entity.BankCard;
import com.lky.entity.User;
import com.lky.enums.dict.SmsLogDict;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.BankCardMapper;
import com.lky.service.BankCardService;
import com.lky.service.EnvironmentService;
import com.lky.service.SmsLogService;
import com.lky.utils.PasswordUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;

/**
 * 银行卡
 *
 * @author luckyhua
 * @version 1.0
 * @since 17-10-28
 */
@RestController
@RequestMapping("api/bankcard")
@Api(value = "api/bankcard", description = "银行卡")
public class MBankCardController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(MBankCardController.class);

    @Inject
    private BankCardService bankCardService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private BankCardMapper bankCardMapper;

    @ApiOperation(value = "添加银行卡", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @ApiParam(name = "bankCardDto", value = "银行卡dto")
                               @RequestBody BankCardDto bankCardDto) {

        String[] checkFiled = {"bankCardDto", "bankName", "bankArea", "branchName",
                "bankcard", "realName", "cardNo", "mobile", "code", "payPwd"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, bankCardDto, bankCardDto.getBankName(),
                bankCardDto.getBankArea(), bankCardDto.getBranchName(), bankCardDto.getBankcard(),
                bankCardDto.getRealName(), bankCardDto.getCardNo(), bankCardDto.getMobile(),
                bankCardDto.getCode(), bankCardDto.getPayPwd());
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, bankCardDto.getMobile());
        AssertUtils.isIdCard(ID_CARD_FORMAT_ERROR, bankCardDto.getCardNo());
        String payPwd = bankCardDto.getPayPwd();
        AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.notNull(PAY_PWD_NOT_SET, user.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, user.getPayPwd()));
        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(
                    bankCardDto.getMobile(), bankCardDto.getCode(), SmsLogDict.TYPE_BANK_PAY_PWD));
        }

        bankCardService.create(user, bankCardDto);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "解绑银行卡", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo del(@PathVariable Integer id) {

        BankCard bankCard = bankCardService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, bankCard);
        bankCard.setBind(Boolean.FALSE);
        bankCardService.update(bankCard);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "用户银行卡列表", notes = "bankCardList", response = UserBankCardDto.class, responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user) {

        List<BankCard> bankCardList = bankCardService.findByUser(user);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("bankCardList", bankCardMapper.toListDto(bankCardList));
        return responseInfo;
    }
}
