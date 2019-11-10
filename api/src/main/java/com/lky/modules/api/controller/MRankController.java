package com.lky.modules.api.controller;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.DateUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.dto.RankDto;
import com.lky.entity.Rank;
import com.lky.entity.User;
import com.lky.service.RankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.enums.dict.RankDict.TYPE_SOLD;
import static com.lky.enums.dict.RankDict.TYPE_WPoint;

/**
 * 排行榜
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/11/13
 */
@RestController
@RequestMapping("api/ranks")
@Api(value = "api/ranks", description = "排行榜")
public class MRankController extends BaseController {

    @Inject
    private RankService rankService;

    @ApiOperation(value = "获取排行榜", response = RankDto.class, notes = "rankList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "type", value = "类型", paramType = "query", dataType = "String",
                    required = true, allowableValues = "wpoint,sold"),
    })
    @GetMapping(value = "list")
    public ResponseInfo userRanks(@RequestParam(defaultValue = "0") int pageNumber,
                                  @RequestParam(defaultValue = "10") int pageSize,
                                  @RequestParam String type) {

        AssertUtils.isContain(PARAMS_EXCEPTION, type, TYPE_WPoint, TYPE_SOLD);
        Date yesterday = DateUtils.add(new Date(), Calendar.DATE, -1);
        Specification<Rank> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), yesterday));
            predicates.add(cb.equal(root.get("type"), type));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "num"));
        Page<Rank> rankListPage = rankService.findAll(spec, pageable);
        List<Rank> rankDataList = rankListPage.getContent();
        List<RankDto> rankList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rankDataList)) {
            for (Rank rank : rankDataList) {
                RankDto rankDto = new RankDto();
                rankDto.setId(rank.getId());
                rankDto.setNum(rank.getNum());
                rankDto.setType(rank.getType());
                User user = rank.getUser();
                if (TYPE_WPoint.getKey().equals(type)) {
                    String mobile = user.getMobile();
                    rankDto.setUserName(mobile.substring(0, 3) + "*****" + mobile.substring(8, mobile.length()));
                    rankDto.setTargetId(user.getId());
                } else {
                    rankDto.setShopName(rank.getShopName());
                    rankDto.setTargetId(rank.getShopId());
                }
                rankList.add(rankDto);
            }
        }
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("rankList", new PageImpl<>(rankList, pageable, rankListPage.getTotalElements()));
        return responseInfo;
    }

}
