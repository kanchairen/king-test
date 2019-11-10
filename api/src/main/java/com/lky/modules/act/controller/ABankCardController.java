package com.lky.modules.act.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.BankCardDto;
import com.lky.dto.UserBankCardDto;
import com.lky.entity.ABankCard;
import com.lky.entity.AUser;
import com.lky.enums.dict.SmsLogDict;
import com.lky.mapper.ABankCardMapper;
import com.lky.service.ABankCardService;
import com.lky.service.EnvironmentService;
import com.lky.service.SmsLogService;
import com.lky.utils.PasswordUtils;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.*;

/**
 * 代理商绑定银行卡
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-22
 */
@RestController
@RequestMapping("act/bankcard")
@Api(value = "act/bankcard", description = "代理商绑定银行卡")
public class ABankCardController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ABankCardController.class);

    @Inject
    private ABankCardService aBankCardService;

    @Inject
    private SmsLogService smsLogService;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private ABankCardMapper aBankCardMapper;

    @ApiOperation(value = "代理商绑定银行卡", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "bankCardDto", value = "银行卡dto")
                               @RequestBody BankCardDto bankCardDto) {

        AUser aUser = ShiroUtils.getAUser();
        //参数校验
        String[] checkFiled = {"bankCardDto", "bankName", "bankArea", "branchName",
                "bankcard", "realName", "cardNo", "mobile", "code", "payPwd"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, bankCardDto, bankCardDto.getBankName(),
                bankCardDto.getBankArea(), bankCardDto.getBranchName(), bankCardDto.getBankcard(),
                bankCardDto.getRealName(), bankCardDto.getCardNo(), bankCardDto.getMobile(),
                bankCardDto.getCode(), bankCardDto.getPayPwd());
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, bankCardDto.getMobile());
        AssertUtils.isIdCard(ID_CARD_FORMAT_ERROR, bankCardDto.getCardNo());

        //支付密码校验
        String payPwd = bankCardDto.getPayPwd();
        AssertUtils.isTrue(PAY_PWD_FORMAT_ERROR, payPwd.length() == 6);
        AssertUtils.notNull(PAY_PWD_NOT_SET, aUser.getPayPwd());
        AssertUtils.isTrue(PAY_PWD_ERROR, PasswordUtils.validatePassword(payPwd, aUser.getPayPwd()));

        if (environmentService.executeEnv()) {
            AssertUtils.isTrue(CODE_ERROR, smsLogService.checkMobileCode(
                    bankCardDto.getMobile(), bankCardDto.getCode(), SmsLogDict.TYPE_ACT_BANK_BIND));
        }

        aBankCardService.create(aUser, bankCardDto);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "代理商解绑银行卡", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id",
                    required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo del(@PathVariable Integer id) {

        ABankCard aBankCard = aBankCardService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, aBankCard);
        aBankCard.setBind(Boolean.FALSE);
        aBankCardService.update(aBankCard);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "代理商绑定的银行卡列表", notes = "bankCardList",
            response = UserBankCardDto.class, responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list() {

        List<ABankCard> aBankCardList = aBankCardService.findByUser(ShiroUtils.getAUser());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("bankCardList", aBankCardMapper.toListDto(aBankCardList));
        return responseInfo;
    }
}
