package com.lky.modules.api.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.dto.UserCollectDto;
import com.lky.entity.User;
import com.lky.global.annotation.LoginUser;
import com.lky.service.UserCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;

/**
 * 用户收藏
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/12
 */
@RestController
@RequestMapping("api/collect")
@Api(value = "api/collect", description = "用户收藏")
public class MUserCollectController extends BaseController {

    @Inject
    private UserCollectService userCollectService;


    @ApiOperation(value = "点击店铺收藏", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "店铺id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping("shop/{id}")
    public ResponseInfo shopCollect(@ApiIgnore @LoginUser User user,
                                    @PathVariable Integer id) {

        userCollectService.shopCollect(user, id);

        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "取消店铺收藏", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "店铺id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping("shop/{id}/cancel")
    public ResponseInfo cancelShopCollect(@ApiIgnore @LoginUser User user,
                                          @PathVariable Integer id) {

        userCollectService.cancelShopCollect(user, id);

        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "我的收藏", response = UserCollectDto.class, notes = "collectList", responseContainer = "List")
    @GetMapping("list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user) {

        List<UserCollectDto> list = userCollectService.list(user);

        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("collectList", list);
        return responseInfo;
    }
}
