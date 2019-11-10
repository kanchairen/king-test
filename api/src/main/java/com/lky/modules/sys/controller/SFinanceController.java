package com.lky.modules.sys.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.*;
import com.lky.dto.CapitalChangeDto;
import com.lky.dto.DetailRecordDto;
import com.lky.dto.PlatformCountDto;
import com.lky.dto.UserEpitomeDto;
import com.lky.entity.ChangeWPointRecord;
import com.lky.entity.SUser;
import com.lky.entity.User;
import com.lky.entity.UserAsset;
import com.lky.enums.dict.UserDict;
import com.lky.service.AppUserService;
import com.lky.service.ChangeWPointRecordService;
import com.lky.service.UserInfoService;
import com.lky.service.UserService;
import com.lky.utils.BeanUtils;
import com.lky.utils.ShiroUtils;
import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.AssetResCode.*;

/**
 * 财务管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/3/7
 */
@RestController
@RequestMapping("/sys/finance")
@Api(value = "/sys/finance", description = "财务管理")
@Transactional
public class SFinanceController extends BaseController {

    @Inject
    private AppUserService appUserService;

    @Inject
    private UserInfoService userInfoService;

    @Inject
    private UserService userService;

    @Inject
    private ChangeWPointRecordService changeWPointRecordService;

    @ApiOperation(value = "生成统计报表", response = ResponseInfo.class, notes = "fileName")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "condition", value = "姓名/手机号码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "role", value = "用户类型",
                    allowableValues = "consumer,merchant", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "排序类型", paramType = "query", dataType = "string",
                    allowableValues = "balance,wpoint,rpoint,lockWPoint,merchantWPoint,merchantRPoint,merchantLockWPoint,surplusGrain"),
            @ApiImplicitParam(name = "desc", value = "是否降序", paramType = "query", dataType = "boolean"),
    })
    @GetMapping(value = "download/report")
    public ResponseEntity<InputStreamSource> saleReport(@RequestParam(required = false) String condition,
                                                        @RequestParam(required = false) String role,
                                                        @RequestParam(required = false) String type,
                                                        @RequestParam(required = false) Boolean desc) throws IOException {
        //过滤前后空格，和全空格
        if (StringUtils.isNotEmpty(condition)) {
            condition = condition.trim();
        } else {
            condition = null;
        }
        String filePath = userInfoService.buildReport(condition, role, type, desc);
        FileSystemResource file = new FileSystemResource(filePath);
        return ResponseEntity
                .ok()
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

    @ApiOperation(value = "获取导入模板", response = ResponseInfo.class)
    @GetMapping(value = "download/model")
    public ResponseEntity<InputStreamSource> getModelExcel() throws IOException {
        File file = ResourceUtils.getFile("classpath:static/view/batchModel.xlsx");
        FileInputStream fileInputStream = FileUtils.openInputStream(file);
        return ResponseEntity
                .ok()
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(fileInputStream));
    }

    @ApiOperation(value = "获取用户财务列表", response = UserEpitomeDto.class, notes = "userEpitome", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "condition", value = "姓名/手机号码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "role", value = "用户类型",
                    allowableValues = "consumer,merchant", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "排序类型", paramType = "query", dataType = "string",
                    allowableValues = "balance,wpoint,rpoint,lockWPoint,merchantWPoint,merchantRPoint,merchantLockWPoint,surplusGrain"),
            @ApiImplicitParam(name = "desc", value = "是否降序", paramType = "query", dataType = "boolean"),
    })
    @RequiresPermissions("membership:finance:list")
    @GetMapping(value = "user/list")
    public ResponseInfo userList(@RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 @RequestParam(required = false) String condition,
                                 @RequestParam(required = false) String role,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) Boolean desc) {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        //过滤前后空格，和全空格
        if (StringUtils.isNotEmpty(condition)) {
            condition = condition.trim();
        } else {
            condition = null;
        }
        responseInfo.putData("userEpitome",
                userInfoService.findEpitomeList(pageNumber, pageSize, condition, role, type, desc));
        return responseInfo;
    }

    @ApiOperation(value = "根据用户ID获取个人信息", response = UserEpitomeDto.class, notes = "userEpitome")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "app用户id", required = true, paramType = "path", dataType = "int"),
    })
    @GetMapping(value = "user/{id}")
    public ResponseInfo getUserEpitome(@PathVariable Integer id) {
        User user = userService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, user);
        UserAsset userAsset = user.getUserAsset();
        UserEpitomeDto userEpitome = new UserEpitomeDto();
        BeanUtils.copyPropertiesIgnoreNull(user, userEpitome);
        BeanUtils.copyPropertiesIgnoreNull(userAsset, userEpitome, "id");

        userEpitome.setRoleType(UserDict.getEnum(userEpitome.getRoleType()).getValue());
        userEpitome.setLockWpoint(userAsset.getLockWPoint());
        userEpitome.setMerchantLockWpoint(userAsset.getMerchantLockWPoint());
        userEpitome.setMerchantRpoint(userAsset.getMerchantRPoint());
        userEpitome.setMerchantWpoint(userAsset.getMerchantWPoint());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("userEpitome", userEpitome);
        return responseInfo;
    }

    @ApiOperation(value = "根据id获取app用户赠送/扣减明细列表", response = ChangeWPointRecord.class, notes = "changeList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "endTime", value = "截止时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "id", value = "app用户id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "资产类型", required = true, paramType = "query", dataType = "string",
                    allowableValues = "balance,wpoint,rpoint,lockWPoint,merchantWPoint,merchantRPoint,merchantLockWPoint"),
    })
    @GetMapping(value = "user/{id}/list")
    public ResponseInfo getUserEpitome(@RequestParam(defaultValue = "0") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) Long beginTime,
                                       @RequestParam(required = false) Long endTime,
                                       @PathVariable Integer id, @RequestParam String type) {
        User user = userService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, user);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("changeList",
                changeWPointRecordService.findByCondition(user, type, beginTime, endTime, pageable));
        return responseInfo;
    }

    @ApiOperation(value = "Excel批量处理修改", response = ResponseInfo.class)
    @PostMapping(value = "user/batchChange")
    public ResponseInfo addFile(@ApiParam(name = "file", value = "上传的Excel文件")
                                @RequestParam("file") MultipartFile file) {
        AssertUtils.isTrue(PARAMS_EXCEPTION, !file.isEmpty());
        List<CapitalChangeDto> changeDtoList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            //excel表中第三行开始为有效数据，第一行和第二行为说明和标题。
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    String mobile = "";
                    double changeNum = 0;
                    String type = "";
                    //如果电话号码被填入数字格式则转为字符中。
                    if (row.getCell(0) != null) {
                        switch (row.getCell(0).getCellTypeEnum()) {
                            case NUMERIC:
                                BigDecimal bd1 = new BigDecimal(Double.toString(row.getCell(0).getNumericCellValue()));
                                mobile = bd1.toPlainString();
                                break;
                            case STRING:
                                mobile = row.getCell(0).getStringCellValue();
                                break;
                        }
                    }
                    if (row.getCell(1) != null && CellType.STRING == row.getCell(1).getCellTypeEnum()) {
                        type = row.getCell(1).getStringCellValue();
                    }
                    //如果增加/扣除数量为字符串则转换为double类型。
                    if (row.getCell(2) != null) {
                        switch (row.getCell(2).getCellTypeEnum()) {
                            case NUMERIC:
                                changeNum = row.getCell(2).getNumericCellValue();
                                break;
                            case STRING:
                                Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]?+[0-9]?");
                                String str = row.getCell(2).getStringCellValue().trim();
                                AssertUtils.isTrue(EXCEL_ROW_NUMBER_WRONG, String.valueOf(i + 1),
                                        StringUtils.isNotEmpty(str) && pattern.matcher(str).matches());
                                changeNum = Double.valueOf(str);
                                break;
                        }
                    }
                    if (StringUtils.isEmpty(mobile) && StringUtils.isEmpty(type) && changeNum == 0) {
                        continue;
                    }
                    AssertUtils.isTrue(EXCEL_ROW_NUMBER_WRONG, String.valueOf(i + 1),
                            changeNum <= 100000000 && changeNum >= -100000000 && changeNum % 1 == 0 && changeNum != 0);
                    AssertUtils.isTrue(EXCEL_ROW_FORMAT_WRONG, String.valueOf(i + 1),
                            StringUtils.isNotEmpty(mobile) && StringUtils.isNotEmpty(type));
                    AssertUtils.isTrue(EXCEL_ROW_MOBILE_WRONG, String.valueOf(i + 1), RegexUtils.isMobileNumber(mobile));
                    CapitalChangeDto capitalChangeDto = new CapitalChangeDto();
                    capitalChangeDto.setMobile(mobile.trim());
                    capitalChangeDto.setNumber(changeNum);
                    capitalChangeDto.setType(type);
                    changeDtoList.add(capitalChangeDto);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!CollectionUtils.isEmpty(changeDtoList)) {
            appUserService.addBatchRecord(changeDtoList);
        }
        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "申请赠送/扣减用户账户数据", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "app用户id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "资金类型", required = true, paramType = "query", dataType = "string",
                    allowableValues = "wpoint,rpoint,lockWPoint,merchantWPoint,merchantRPoint,merchantLockWPoint"),
            @ApiImplicitParam(name = "calculated", value = "是否加入到代理商收益中计算（白积分必传）",
                    paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "number", value = "正为赠送/负为扣减类型数量",
                    required = true, paramType = "query", dataType = "double"),
    })
    @PutMapping(value = "user/{id}/recharge")
    public ResponseInfo recharge(@PathVariable Integer id, @RequestParam Double number, @RequestParam String type,
                                 @RequestParam(required = false) Boolean calculated) {

        AssertUtils.notNull(PARAMS_IS_NULL, id, number);
        AssertUtils.isTrue(RECHARGE_NUMBER_WRONG,
                number <= 100000000 && number >= -100000000 && number % 1 == 0 && number != 0);
        SUser sUser = ShiroUtils.getSUser();
        User user = userService.findById(id);
        appUserService.webAddChangeRecord(sUser, user, type, number, calculated);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "用户账单明细", response = DetailRecordDto.class, notes = "changeList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "endTime", value = "截止时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "id", value = "app用户id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "资金类型", required = true, paramType = "query", dataType = "string",
                    allowableValues = "balance,wpoint,rpoint,lockWPoint,merchantWPoint,merchantRPoint,merchantLockWPoint,surplusGrain"),
    })
    @GetMapping(value = "user/{id}/detail")
    public ResponseInfo getTypeDetail(@RequestParam(defaultValue = "0") int pageNumber,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestParam(required = false) Long beginTime,
                                      @RequestParam(required = false) Long endTime,
                                      @PathVariable Integer id, @RequestParam String type) {
        User user = userService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, user);
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("changeList",
                userInfoService.findUserFundFlow(user, type, beginTime, endTime, pageable));
        return responseInfo;
    }

    @ApiOperation(value = "查看平台财务", response = PlatformCountDto.class, notes = "platformCount")
    @GetMapping(value = "statistics")
    @RequiresPermissions("finance:data:get")
    public ResponseInfo getUserEpitome() {
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("platformCount",
                userInfoService.platformCount());
        return responseInfo;
    }
}
