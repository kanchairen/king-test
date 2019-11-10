package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.CartListDto;
import com.lky.entity.Cart;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;

/**
 * 购物车
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/24
 */
@RestController
@RequestMapping("api/cart")
@Api(value = "api/cart", description = "购物车")
public class MCartController extends BaseController {

    @Inject
    private CartService cartService;

    @ApiOperation(value = "加入购物车", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @ApiParam(name = "cart", value = "购物车") @RequestBody Cart cart) {

        String[] checkFiled = {"cart", "productId", "number"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, cart, cart.getProductId(), cart.getNumber());
        AssertUtils.isTrue(PARAMS_EXCEPTION, cart.getNumber() > 0);

        cartService.create(user, cart.getProductId(), cart.getNumber());

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改购物车", notes = "cart购物车", response = CartListDto.class)
    @ApiImplicitParams({
    })
    @PutMapping(value = "")
    public ResponseInfo edit(@ApiIgnore @LoginUser User user,
                             @ApiParam(name = "cart", value = "编辑购物车") @RequestBody Cart cart) {

        AssertUtils.notNull(PARAMS_IS_NULL, cart);

        cart = cartService.modify(user, cart);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("cart", cart);
        return responseInfo;
    }

    @ApiOperation(value = "删除购物车", response = ResponseInfo.class)
    @ApiImplicitParams({
    })
    @DeleteMapping(value = "")
    public ResponseInfo delete(@RequestBody Integer[] idList) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, idList);

        List<Cart> list = cartService.findList(idList);
        for (Cart cart : list) {
            AssertUtils.notNull(PARAMS_EXCEPTION, cart);
        }

        cartService.deleteByIds(idList);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "购物车列表", notes = "cartList", response = CartListDto.class, responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user) {

        List<CartListDto> list = cartService.list(user);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("cartList", list);
        return responseInfo;
    }
}
