package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.ClimateCardLink;
import com.lky.service.GoldMasterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.dict.ClimateCardDict.LINK_TYPE_BUY;
import static com.lky.enums.dict.ClimateCardDict.LINK_TYPE_QUOTATIONS;

/**
 * 金主数字资产服务平台
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/23
 */
@RestController
@RequestMapping("sys/goldMaster")
@Api(value = "sys/goldMaster", description = "金主数字资产服务平台")
public class SGoldMasterController extends BaseController {

    @Inject
    private GoldMasterService goldMasterService;

    @ApiOperation(value = "获取天时卡链接", response = ResponseInfo.class, notes = "buyLink购买链接, quotationsLink行情链接")
    @GetMapping(value = "get")
    @RequiresPermissions("gold:master:get")
    public ResponseInfo getList() {
        List<ClimateCardLink> linkList = goldMasterService.findAll();
        String buyLink = null;
        String quotationsLink = null;
        if (!CollectionUtils.isEmpty(linkList)) {
            for (ClimateCardLink climateCardLink : linkList) {
                if (String.valueOf(LINK_TYPE_BUY).equals(climateCardLink.getType())) {
                    buyLink = climateCardLink.getLink();
                }
                else if (String.valueOf(LINK_TYPE_QUOTATIONS).equals(climateCardLink.getType())) {
                    quotationsLink = climateCardLink.getLink();
                }
            }
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("buyLink", buyLink);
        responseInfo.putData("quotationsLink", quotationsLink);
        return responseInfo;
    }

    @ApiOperation(value = "修改天时卡链接", response = ResponseInfo.class)
    @PutMapping(value = "")
    public ResponseInfo edit(@ApiParam(name = "buyLink", value = "购买天时卡链接") @RequestParam String buyLink,
                             @ApiParam(name = "quotationsLink", value = "天时卡行情链接")
                             @RequestParam String quotationsLink) {
        String[] fields = {"buyLink", "quotationsLink"};
        AssertUtils.notNull(PARAMS_IS_NULL, fields, buyLink, quotationsLink);
        List<ClimateCardLink> linkList = goldMasterService.findAll();
        if (CollectionUtils.isEmpty(linkList)) {
            ClimateCardLink buy = new ClimateCardLink();
            buy.setLink(buyLink);
            buy.setType(String.valueOf(LINK_TYPE_BUY));
            goldMasterService.save(buy);

            ClimateCardLink quotations = new ClimateCardLink();
            quotations.setLink(quotationsLink);
            quotations.setType(String.valueOf(LINK_TYPE_QUOTATIONS));
            goldMasterService.save(quotations);
        } else {
            for (ClimateCardLink climateCardLink : linkList) {
                if (String.valueOf(LINK_TYPE_BUY).equals(climateCardLink.getType())) {
                    climateCardLink.setLink(buyLink);
                    goldMasterService.update(climateCardLink);
                }
                else if (String.valueOf(LINK_TYPE_QUOTATIONS).equals(climateCardLink.getType())) {
                    climateCardLink.setLink(quotationsLink);
                    goldMasterService.update(climateCardLink);
                }
            }
        }
        return ResponseUtils.buildResponseInfo();
    }
}
