package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.entity.QrCode;
import com.lky.entity.SUser;
import com.lky.service.QrCodeService;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.commons.code.PublicResCode.SERVER_EXCEPTION;
import static com.lky.enums.code.SetResCode.*;

/**
 * 活动二维码，设置现金对应G米比例
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/17
 */
@RestController
@RequestMapping(value = "sys/qrCode")
@Api(value = "sys/qrCode", description = "活动二维码")
public class SQrCodeController extends BaseController {

    @Inject
    private QrCodeService qrCodeService;

    @ApiOperation(value = "添加活动二维码", response = QrCode.class, notes = "返回新增二维码")
    @PostMapping(value = "")
    @ResponseBody
    public ResponseInfo create(@RequestBody QrCode qrCode) {
        //效验参数
        SUser sUser = ShiroUtils.getSUser();
        AssertUtils.notNull(SERVER_EXCEPTION, sUser);
        String[] checkFiled = {"rate", "beginTime", "endTime", "calculated", "redirect"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, qrCode.getRate(), qrCode.getBeginTime(),
                qrCode.getEndTime(), qrCode.getCalculated(), qrCode.getRedirect());
        AssertUtils.isTrue(BEGIN_TIME_AFTER_END_TIME, qrCode.getEndTime().after(qrCode.getBeginTime()));
        AssertUtils.isTrue(QR_CODE_RATE, qrCode.getRate() > 0 && qrCode.getRate() <= 1000);
        if (qrCode.getRedirect()) {
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"threshold", "url"}, qrCode.getThreshold(), qrCode.getUrl());
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("qrCode", qrCodeService.createQrCode(qrCode));
        return responseInfo;
    }

    @ApiOperation(value = "删除活动二维码", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        QrCode qrCode = qrCodeService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, qrCode);
        //只能删除失效的二维码
        Date now = new Date();
        AssertUtils.isTrue(QR_CODE_STATE, !qrCode.getState() ||
                !(qrCode.getBeginTime().before(now) && qrCode.getEndTime().after(now)));
        qrCodeService.delete(id);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "活动二维码详情", response = QrCode.class, notes = "qrCode")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        QrCode qrCode = qrCodeService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("qrCode", qrCode);
        return responseInfo;
    }

    @ApiOperation(value = "活动二维码列表", response = QrCode.class , notes = "qrCodeList", responseContainer = "List")
    @GetMapping(value = "list")
    @RequiresPermissions("active:qr:code:list")
    public ResponseInfo list() {
        List<QrCode> qrCodeList = qrCodeService.findAll(new Sort(Sort.Direction.DESC, "id"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("qrCodeList", qrCodeList);
        return responseInfo;
    }

    @ApiOperation(value = "修改活动二维码的状态", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo editState(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        QrCode qrCode = qrCodeService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, qrCode);
        qrCode.setState(!qrCode.getState());
        qrCodeService.update(qrCode);
        return ResponseUtils.buildResponseInfo();
    }

}