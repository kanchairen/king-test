package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.ReceiveAddress;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.service.ReceiveAddressService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.UserResCode.MOBILE_FORMAT_ERROR;

/**
 * 收货地址
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/25
 */
@RestController
@RequestMapping("api/address")
@Api(value = "api/address", description = "收货地址")
public class MReceiveAddressController extends BaseController {

    @Inject
    private ReceiveAddressService receiveAddressService;

    @ApiOperation(value = "添加收货地址", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @ApiParam(name = "address", value = "收货地址") @RequestBody ReceiveAddress address) {

        String[] checkFiled = {"address", "mobile", "name", "addressDetail"};

        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled,
                address, address.getMobile(), address.getName(), address.getAddressDetail());
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, address.getMobile());

        receiveAddressService.create(user, address);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改收货地址", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@ApiIgnore @LoginUser User user,
                             @PathVariable Integer id,
                             @ApiParam(name = "address", value = "收货地址") @RequestBody ReceiveAddress address) {

        String[] checkFiled = {"id", "address", "mobile", "name", "addressDetail"};

        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, id,
                address, address.getMobile(), address.getName(), address.getAddressDetail());
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, address.getMobile());
        ReceiveAddress receiveAddress = receiveAddressService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, receiveAddress);
        AssertUtils.isTrue(PARAMS_EXCEPTION, receiveAddress.getUserId() == user.getId());

        receiveAddressService.modify(receiveAddress, address);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "收货地址详情", response = ReceiveAddress.class, notes = "address")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@ApiIgnore @LoginUser User user,
                            @PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);
        ReceiveAddress receiveAddress = receiveAddressService.findById(id);
        AssertUtils.isTrue(PARAMS_EXCEPTION, receiveAddress.getUserId() == user.getId());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("address", receiveAddress);
        return responseInfo;
    }

    @ApiOperation(value = "收货地址列表", response = ReceiveAddress.class, notes = "addressList", responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user) {

        List<ReceiveAddress> addressList = receiveAddressService.findByUserId(user.getId());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("addressList", addressList);
        return responseInfo;
    }

    @ApiOperation(value = "删除收货地址", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@ApiIgnore @LoginUser User user,
                               @PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);
        ReceiveAddress receiveAddress = receiveAddressService.findById(id);
        AssertUtils.isTrue(PARAMS_EXCEPTION, receiveAddress.getUserId() == user.getId());

        receiveAddressService.remove(receiveAddress);

        return ResponseUtils.buildResponseInfo();
    }
}
