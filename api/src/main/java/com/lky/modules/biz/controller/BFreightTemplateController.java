package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.commons.utils.JsonUtils;
import com.lky.dto.FreightTemplateDto;
import com.lky.entity.*;
import com.lky.enums.code.ShopResCode;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.FreightMapper;
import com.lky.service.FreightTemplateService;
import com.lky.service.ProductGroupService;
import com.lky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.ShopResCode.FREIGHT_TEMPLATE_IS_USED;
import static com.lky.enums.code.ShopResCode.FREIGHT_TEMPLATE_NOT_CHANGE_TYPE;
import static com.lky.enums.dict.FreightTemplateDict.*;

/**
 * 运费模板
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/23
 */
@RestController
@RequestMapping("biz/freightTemplate")
@Api(value = "biz/freight", description = "运费模板")
public class BFreightTemplateController extends BaseController {

    @Inject
    private ShopService shopService;

    @Inject
    private FreightTemplateService freightTemplateService;

    @Inject
    private FreightMapper freightMapper;

    @Inject
    private ProductGroupService productGroupService;


    @ApiOperation(value = "添加运费模板", response = FreightTemplateDto.class, notes = "freightTemplate")
    @PostMapping("")
    public ResponseInfo create(@ApiIgnore @LoginUser User user,
                               @ApiParam(name = "freightTemplateDto", value = "运费模板dto")
                               @RequestBody FreightTemplateDto templateDto) {
        FreightTemplate freightTemplate = freightMapper.fromDto(templateDto);
        this.verifyTemplate(freightTemplate);
        Shop shop = shopService.findByUser(user);
        //标题重名校验
        AssertUtils.isTrue(ShopResCode.NAME_EXIST, !freightTemplateService.repeatTitle(shop.getId(), templateDto.getName()));
        AssertUtils.notNull(PARAMS_IS_NULL, shop);
        freightTemplate.setShopId(shop.getId());
        freightTemplate.setCreateTime(new Date());
        freightTemplateService.save(freightTemplate);
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("freightTemplate", freightMapper.toDto(freightTemplate));
        return responseInfo;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseInfo delete(@ApiIgnore @LoginUser User user,
                               @PathVariable int id) {
        FreightTemplate template = freightTemplateService.findById(id);
        AssertUtils.notNull(PARAMS_EXCEPTION, template);

        //校验权限
        AssertUtils.isTrue(PARAMS_EXCEPTION, shopService.findByUser(user).getId() == (template.getShopId()));

        //验证模板是否被商品组占用
        SimpleSpecificationBuilder<ProductGroup> builder = new SimpleSpecificationBuilder<>();
        builder.add("freightTemplate", SpecificationOperator.Operator.eq, id);
        List<ProductGroup> productGroupList = productGroupService.findAll(builder.generateSpecification());
        AssertUtils.isTrue(FREIGHT_TEMPLATE_IS_USED, CollectionUtils.isEmpty(productGroupList));
        freightTemplateService.delete(template);
        return ResponseInfo.buildSuccessResponseInfo();
    }


    private void verifyTemplate(FreightTemplate template) {
        //校验必选参数
        AssertUtils.notNull(PARAMS_IS_NULL, template.getName(), template.getPriceType(),
                template.getSendAddress(), template.getDeliveryTime());
        AssertUtils.isTrue(PARAMS_EXCEPTION, !CollectionUtils.isEmpty(template.getFreightRuleList()));

        template.getFreightRuleList().forEach(r -> {
            if (r.getCitySet() == null) {
                r.setCitySet(new HashSet<>());
            }
        });

        //校验name参数名称
//        AssertUtils.verifyParam(template.getName(), Constant.LENGTH_32);

        //校验参数计价方式
        AssertUtils.isContain(PARAMS_EXCEPTION, template.getPriceType(),
                PRICE_TYPE_NUM, PRICE_TYPE_VOLUME, PRICE_TYPE_WEIGHT);

        //校验数值型参数发货时间
        AssertUtils.isTrue(PARAMS_EXCEPTION, template.getDeliveryTime() > 0);

        //校验数值型参数运费规则IDs
        Set<Integer> existCitySet = new HashSet<>();
        int noCityRule = 0;
        for (FreightRule freightRule : template.getFreightRuleList()) {
            //如果是新增（ruleId为null或0）时，citySet为空集合
            if (CollectionUtils.isEmpty(freightRule.getCitySet())) {
                noCityRule++;
            } else {
                verifyFreightRule(freightRule, existCitySet);
            }
        }
        AssertUtils.isTrue(PARAMS_EXCEPTION, noCityRule == 1);
    }

    private void verifyFreightRule(FreightRule freightRule, Set<Integer> existCitySet) {
        //校验必选参数
        AssertUtils.notNull(PARAMS_EXCEPTION, freightRule, freightRule.getBase(),
                freightRule.getBasePrice(), freightRule.getExtra(), freightRule.getExtraPrice());

        //校验参数区域IDs
        Set<Area> citySet = freightRule.getCitySet();
        if (!CollectionUtils.isEmpty(citySet)) {
            for (Area area : citySet) {
                AssertUtils.isTrue(PARAMS_EXCEPTION, !existCitySet.contains(area.getId()));
                existCitySet.add(area.getId());
            }
        }
    }


    @ApiOperation(value = "根据id获取运费模板", response = FreightTemplateDto.class, notes = "freightTemplate, using是否被商品占用")
    @GetMapping(value = "{id}")
    public ResponseInfo get(@ApiIgnore @LoginUser User user,
                            @PathVariable int id) {
        FreightTemplate template = freightTemplateService.findById(id);
        AssertUtils.notNull(PARAMS_IS_NULL, template);

        //校验权限
        AssertUtils.isTrue(PARAMS_EXCEPTION, shopService.findByUser(user).getId() == (template.getShopId()));
        //模板是否被商品组占用
        SimpleSpecificationBuilder<ProductGroup> builder = new SimpleSpecificationBuilder<>();
        builder.add("freightTemplate", SpecificationOperator.Operator.eq, id);
        List<ProductGroup> productGroupList = productGroupService.findAll(builder.generateSpecification());
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putBeanData("freightTemplate", freightMapper.toDto(template));
        responseInfo.putData("using", !CollectionUtils.isEmpty(productGroupList));
        return responseInfo;
    }


    @ApiOperation(value = "获取运费模板列表", response = FreightTemplateDto.class, notes = "freightTemplateList", responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user) {
        SimpleSpecificationBuilder<FreightTemplate> builder = new SimpleSpecificationBuilder<>();
        builder.add("shopId", SpecificationOperator.Operator.eq, shopService.findByUser(user).getId());
        List<FreightTemplate> templateList = freightTemplateService.findAll(builder.generateSpecification());
        List<FreightTemplateDto> freightTemplateList = new ArrayList<>();
        templateList.forEach(freightTemplate -> freightTemplateList.add(freightMapper.toDto(freightTemplate)));
        ResponseInfo responseInfo = ResponseInfo.buildSuccessResponseInfo();
        responseInfo.putData("freightTemplateList", freightTemplateList);
        return responseInfo;
    }


    @ApiOperation(value = "编辑运费模板", response = ResponseInfo.class)
    @PutMapping(value = "")
    public ResponseInfo edit(@ApiIgnore @LoginUser User user,
                             @RequestBody FreightTemplateDto templateDto) {
        FreightTemplate template = freightTemplateService.findById(templateDto.getId());
        AssertUtils.notNull(PARAMS_EXCEPTION, template);

        //校验权限
        Shop shop = shopService.findByUser(user);
        AssertUtils.isTrue(PARAMS_EXCEPTION, shop.getId() == (template.getShopId()));
        //标题重名校验
        if (!template.getName().equals(templateDto.getName())) {
            AssertUtils.isTrue(ShopResCode.NAME_EXIST, !freightTemplateService.repeatTitle(shop.getId(), templateDto.getName()));
        }
        templateDto.setShopId(template.getShopId());

        List<FreightRule> freightRuleList = template.getFreightRuleList();
        Map<Integer, FreightRule> freightRuleMap = freightRuleList.stream()
                .collect(Collectors.toMap(FreightRule::getId, freightRule -> freightRule));
        freightRuleList.clear();

        templateDto.getFreightRuleList().forEach(freightRule -> {
            if (freightRuleMap.containsKey(freightRule.getId())) {
                BeanUtils.copyProperties(freightRule, freightRuleMap.get(freightRule.getId()));
                freightRuleList.add(freightRuleMap.get(freightRule.getId()));
            } else {
                freightRuleList.add(freightRule);
            }
        });

        //校验参数计价方式
        AssertUtils.isContain(PARAMS_EXCEPTION, template.getPriceType(),
                PRICE_TYPE_NUM, PRICE_TYPE_VOLUME, PRICE_TYPE_WEIGHT);
        //被占用模板则不能修改计费方式
        if (!template.getPriceType().equals(templateDto.getPriceType())) {
            SimpleSpecificationBuilder<ProductGroup> builder = new SimpleSpecificationBuilder<>();
            builder.add("freightTemplate", SpecificationOperator.Operator.eq, template.getId());
            AssertUtils.isTrue(FREIGHT_TEMPLATE_NOT_CHANGE_TYPE, productGroupService.count(builder.generateSpecification()) == 0);
        }

        BeanUtils.copyProperties(templateDto, template, "sendAddress", "freightRuleList");
        template.setSendAddress(JsonUtils.objectToJson(templateDto.getAddressDto()));

        //校验参数
        verifyTemplate(template);

        template.setUpdateTime(new Date());
        if (freightTemplateService.update(template) != null) {
            return ResponseInfo.buildSuccessResponseInfo();
        }

        return ResponseInfo.buildErrorResponseInfo();
    }

}
