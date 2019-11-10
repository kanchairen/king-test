package com.lky.modules.sys.controller;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.AuthRecordDto;
import com.lky.dto.BankCardDto;
import com.lky.dto.RelateUserDto;
import com.lky.dto.WPointRecordDto;
import com.lky.entity.AuthRecord;
import com.lky.entity.ChangeWPointRecord;
import com.lky.entity.User;
import com.lky.entity.WPointRecord;
import com.lky.enums.dict.WPointRecordDict;
import com.lky.mapper.AuthRecordMapper;
import com.lky.mapper.WPointRecordMapper;
import com.lky.service.*;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.AssetResCode.WPOINT_NOT_ENOUGH;
import static com.lky.enums.code.UserResCode.AUTH_CARD_NUMBER_EXIST;
import static com.lky.enums.code.UserResCode.ID_CARD_FORMAT_ERROR;
import static com.lky.enums.code.UserResCode.MOBILE_FORMAT_ERROR;
import static com.lky.enums.dict.AuthRecordDict.*;
import static com.lky.enums.dict.ChangeWPointRecordDict.*;

/**
 * app用户管理
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/20
 */
@RestController
@RequestMapping("/sys/app/user")
@Api(value = "/sys/app/user", description = "app用户管理")
@Transactional
public class SAppUserController extends BaseController {

    @Inject
    private UserService userService;

    @Inject
    private WPointRecordService wPointRecordService;

    @Inject
    private WPointRecordMapper wPointRecordMapper;

    @Inject
    private AuthRecordService authRecordService;

    @Inject
    private AuthRecordMapper authRecordMapper;

    @Inject
    private ChangeWPointRecordService changeWPointRecordService;

    @Inject
    private AppUserService appUserService;

    @Inject
    private BankCardService bankCardService;


    @ApiOperation(value = "用户列表", response = User.class, notes = "userList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "condition", value = "模糊查询手机号或是用户昵称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "auth", allowableValues = "agree,unauthorized,apply,refuse",
                    value = "实名认证状态", paramType = "query", dataType = "string"),
    })
    @GetMapping("list")
    @RequiresPermissions("membership:manager:list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo list(@RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String condition,
                             @RequestParam(required = false) String auth) {

        if (StringUtils.isNotEmpty(auth)) {
            AssertUtils.isContain(PARAMS_EXCEPTION, auth, STATE_AGREE, STATE_UNAUTHORIZED, STATE_APPLY, STATE_REFUSE);
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(condition)) {
                Predicate p1 = cb.like(root.get("nickname"), "%" + condition.trim() + "%");
                Predicate p2 = cb.like(root.get("mobile"), "%" + condition.trim() + "%");
                predicates.add(cb.or(p1, p2));
            }

            if (StringUtils.isNotEmpty(auth)) {
                Predicate p1 = cb.equal(root.get("authState"), auth.trim());
                predicates.add(p1);
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<User> userList = userService.findAll(spec, pageable);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("userList", userList);
        return responseInfo;
    }

    @ApiOperation(value = "修改用户地址", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "address", value = "用户地址", required = true, paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "{id}/edit_address")
    public ResponseInfo editAddress(@PathVariable Integer id,
                                    @RequestParam String address) {
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id, address);
        userService.editAddress(id, address);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "给用户绑定银行卡", response = ResponseInfo.class)
    @PostMapping("bankcard")
    public ResponseInfo create(
            @ApiParam(name = "id", value = "用户id") @RequestParam Integer id,
            @ApiParam(name = "bankCardDto", value = "银行卡dto")
            @RequestBody BankCardDto bankCardDto) {
        User user = userService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, user);
        String[] checkFiled = {"bankCardDto", "bankName", "bankArea", "branchName",
                "bankcard", "realName", "cardNo", "mobile"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, bankCardDto, bankCardDto.getBankName(),
                bankCardDto.getBankArea(), bankCardDto.getBranchName(), bankCardDto.getBankcard(),
                bankCardDto.getRealName(), bankCardDto.getCardNo(), bankCardDto.getMobile());
        AssertUtils.isMobile(MOBILE_FORMAT_ERROR, bankCardDto.getMobile());
        AssertUtils.isIdCard(ID_CARD_FORMAT_ERROR, bankCardDto.getCardNo());
        bankCardService.sysCreate(user, bankCardDto);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "查看用户上下级列表", response = RelateUserDto.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int"),
    })
    @GetMapping("{id}/relateUser")
    public ResponseInfo relateUser(@PathVariable Integer id,
                                   @RequestParam(defaultValue = "0") int pageNumber,
                                   @RequestParam(defaultValue = "10") int pageSize) {

        RelateUserDto relateUserDto = new RelateUserDto();
        User user = userService.findById(id);
        if (user != null && user.getParentId() != null) {
            User parentUser = userService.findById(user.getParentId());
            relateUserDto.setNickname(parentUser.getNickname());
            relateUserDto.setMobile(parentUser.getMobile());
        }

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("parentId"), id));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Page<User> childList = userService.findAll(spec, pageable);
        relateUserDto.setChildUserList(childList);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("relateUserDto", relateUserDto);
        return responseInfo;
    }

    @ApiOperation(value = "获取实名认证申请记录", response = AuthRecordDto.class, notes = "authRecord")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "auth/record")
    public ResponseInfo getAuthRecord(@RequestParam Integer userId) {
        User user = new User();
        user.setId(userId);
        AuthRecord authRecord = authRecordService.findByUser(user);
        AssertUtils.notNull(PARAMS_EXCEPTION, authRecord);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("authRecord", authRecordMapper.toDto(authRecord));
        return responseInfo;
    }

    @ApiOperation(value = "实名认证审核", response = ResponseInfo.class)
    @PutMapping(value = "auth/audit")
    public ResponseInfo authAudit(@ApiParam(name = "authRecordDto", value = "实名认证信息dto")
                                  @RequestBody AuthRecordDto authRecordDto) {
        //参数校验
        AssertUtils.notNull(PARAMS_IS_NULL, authRecordDto, authRecordDto.getState(), authRecordDto.getUserId());
        AssertUtils.isContain(PARAMS_EXCEPTION, authRecordDto.getState(),
                STATE_AGREE, STATE_UNAUTHORIZED, STATE_APPLY, STATE_REFUSE);
        User user = userService.findById(authRecordDto.getUserId());
        AuthRecord authRecord = authRecordService.findByUser(user);
        AssertUtils.notNull(PARAMS_EXCEPTION, authRecord);
        //效验一张身份证只能绑定一个GNC帐号
        if (STATE_AGREE.getKey().equals(authRecordDto.getState())) {
            AssertUtils.isTrue(AUTH_CARD_NUMBER_EXIST,
                    CollectionUtils.isEmpty(authRecordService.findByCardNumberAudit(authRecord.getCardNumber())));
        }
        appUserService.authAudit(authRecord, authRecordDto, user);

        return ResponseInfo.buildSuccessResponseInfo();
    }

    @ApiOperation(value = "申请赠送/扣减用户G米", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "app用户id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "calculated", value = "是否加入到代理商收益中计算", required = true, paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "number", value = "正为赠送/负为扣减用户G米数量",
                    required = true, paramType = "query", dataType = "double"),
    })
    @PutMapping(value = "{id}/recharge")
    public ResponseInfo recharge(@PathVariable Integer id, @RequestParam Double number, @RequestParam Boolean calculated) {

        AssertUtils.notNull(PARAMS_IS_NULL, id, number);
        AssertUtils.isTrue(PARAMS_EXCEPTION, number != 0);

        //充值赠送用户G米
        User user = userService.findById(id);
        if (number < 0) {
            AssertUtils.isTrue(WPOINT_NOT_ENOUGH, user.getUserAsset().getWpoint() + number >= 0);
        }
        appUserService.addRecord(user, number, calculated);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "赠送/扣减用户资产列表", notes = "changeWPointRecordList", response = ChangeWPointRecord.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "资金类型", paramType = "query", dataType = "string",
                    allowableValues = "wpoint,rpoint,lockWPoint,merchantWPoint,merchantRPoint,merchantLockWPoint"),
            @ApiImplicitParam(name = "auditState", value = "审核状态", allowableValues = "apply,agree,refuse",
                    paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "condition", value = "模糊查询手机号或是用户昵称", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "change/wpoint/list")
    @RequiresPermissions("finance:audit:list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo changeWPointList(@RequestParam(defaultValue = "0") int pageNumber,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(required = false) String condition,
                                         @RequestParam(required = false) String type,
                                         @RequestParam(required = false) String auditState) {

        if (StringUtils.isNotEmpty(auditState)) {
            AssertUtils.isContain(PARAMS_EXCEPTION, auditState, AUDIT_STATE_APPLY, AUDIT_STATE_AGREE, AUDIT_STATE_REFUSE);
        }

        Specification<ChangeWPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtils.isNotEmpty(type)) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (StringUtils.isNotEmpty(auditState)) {
                predicates.add(cb.equal(root.get("auditState"), auditState));
            }
            Join<WPointRecord, User> userJoin = root.join("user", JoinType.INNER);
            if (StringUtils.isNotEmpty(condition)) {
                Predicate p1 = cb.like(userJoin.get("nickname"), "%" + condition.trim() + "%");
                Predicate p2 = cb.like(userJoin.get("mobile"), "%" + condition.trim() + "%");
                predicates.add(cb.or(p1, p2));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<ChangeWPointRecord> changeWPointRecordList = changeWPointRecordService.findAll(spec, pageable);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("changeWPointRecordList", changeWPointRecordList);
        return responseInfo;
    }

    @ApiOperation(value = "审核赠送/扣减用户资产", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "赠送/扣减记录id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "auditState", value = "审核状态", required = true, allowableValues = "agree,refuse",
                    paramType = "query", dataType = "string"),
    })
    @PutMapping(value = "change/wpoint/audit/{id}")
    public ResponseInfo auditChangeWPoint(@PathVariable Integer id,
                                          @RequestParam String auditState) {

        AssertUtils.isContain(PARAMS_EXCEPTION, auditState, AUDIT_STATE_AGREE, AUDIT_STATE_REFUSE);

        ChangeWPointRecord record = changeWPointRecordService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, record);
        AssertUtils.isTrue(PARAMS_EXCEPTION, AUDIT_STATE_APPLY.compare(record.getAuditState()));

        User user = record.getUser();
        appUserService.audit(user, record, auditState);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "充值赠送G米记录列表", response = WPointRecordDto.class, notes = "wPointRecordList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "endTime", value = "截止时间", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "condition", value = "模糊查询手机号或是用户昵称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", dataType = "int"),
    })
    @GetMapping("recharge/list")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseInfo rechargeList(@RequestParam(defaultValue = "0") int pageNumber,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(required = false) Long beginTime,
                                     @RequestParam(required = false) Long endTime,
                                     @RequestParam(required = false) String condition,
                                     @RequestParam(required = false) Integer userId) {

        Specification<WPointRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("type"), String.valueOf(WPointRecordDict.TYPE_CONSUMER_SYS_GIVE)));
            Join<WPointRecord, User> userJoin = root.join("user", JoinType.INNER);
            if (StringUtils.isNotEmpty(condition)) {
                Predicate p1 = cb.like(userJoin.get("nickname"), "%" + condition.trim() + "%");
                Predicate p2 = cb.like(userJoin.get("mobile"), "%" + condition.trim() + "%");
                predicates.add(cb.or(p1, p2));
            }
            if (userId != null) {
                predicates.add(cb.equal(userJoin.get("id"), userId));
            }
            if (beginTime != null && beginTime > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), new Date(beginTime)));
            }
            if (endTime != null && endTime > 0) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), new Date(endTime)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "createTime"));
        Page<WPointRecord> wPointRecordList = wPointRecordService.findAll(spec, pageable);
        List<WPointRecordDto> wPointRecordDtoList = wPointRecordMapper.toDtoList(wPointRecordList.getContent());
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("wPointRecordList", new PageImpl<>(wPointRecordDtoList, pageable, wPointRecordList.getTotalElements()));
        return responseInfo;
    }
}
