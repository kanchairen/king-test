package com.lky.modules.biz.controller;

import com.lky.commons.base.BaseController;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.response.ResponseInfo;
import com.lky.commons.specification.SimpleSpecificationBuilder;
import com.lky.commons.specification.SpecificationOperator;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.ResponseUtils;
import com.lky.commons.utils.StringUtils;
import com.lky.dto.CustomTextDto;
import com.lky.dto.ProductGroupDto;
import com.lky.dto.ShopDto;
import com.lky.entity.*;
import com.lky.enums.code.MerchantResCode;
import com.lky.enums.dict.BannerDict;
import com.lky.global.annotation.LoginUser;
import com.lky.mapper.CustomTextMapper;
import com.lky.mapper.ShopMapper;
import com.lky.service.*;
import com.lky.utils.BeanUtils;
import io.swagger.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.dict.BannerDict.*;

/**
 * 积分商城banner管理
 *
 * @author huangjingang
 * @version 1.0
 * @since 2018/1/8
 */
@RestController
@RequestMapping("biz/banner")
@Api(value = "biz/banner", description = "积分商城banner管理")
public class BBannerController extends BaseController {
    @Inject
    private BannerService bannerService;

    @Inject
    private ShopService shopService;

    @Inject
    private CustomTextService customTextService;

    @Inject
    private CustomTextMapper customTextMapper;

    @Inject
    private ShopMapper shopMapper;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private ProductService productService;

    @ApiOperation(value = "添加banner", response = ResponseInfo.class)
    @PostMapping("")
    public ResponseInfo create(@ApiParam(name = "banner", value = "banner图片") @RequestBody Banner banner,
                               @ApiIgnore @LoginUser User user) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);
        ShopConfig shopConfig = shop.getShopConfig();

        //效验店铺是否为红积分/白积分商城
        banner.setType(null);
        if (shopConfig.getOpenWPoint() != null && shopConfig.getOpenWPoint()) {
            banner.setType(BannerDict.TYPE_W_POINT.getKey());
        }
        if (shopConfig.getOpenRPoint() != null && shopConfig.getOpenRPoint()) {
            banner.setType(BannerDict.TYPE_R_POINT.getKey());
        }
        AssertUtils.notNull(PublicResCode.NOT_AUTHORIZED, banner.getType());
        banner.setShopId(shop.getId());

        String[] checkFields = {"banner", "bannerImg"};

        AssertUtils.notNull(PARAMS_IS_NULL, checkFields, banner, banner.getBannerImg());

        if (StringUtils.isNotEmpty(banner.getLinkType())) {
            AssertUtils.isContain(PARAMS_EXCEPTION, banner.getLinkType(),
                    LINK_TYPE_PRODUCT, LINK_TYPE_SHOP, LINK_TYPE_CUSTOM_TEXT, LINK_TYPE_CUSTOM_LINK);
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"linkValue"}, banner.getLinkValue());
        }

        bannerService.create(banner);

        return ResponseUtils.buildResponseInfo();
    }


    @ApiOperation(value = "banner详情", response = Banner.class, notes = "banner")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "{id}")
    public ResponseInfo get(@PathVariable Integer id) {

        AssertUtils.notNull(PARAMS_IS_NULL, id);

        Banner banner = bannerService.findById(id);

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("banner", banner);
        return responseInfo;
    }

    @ApiOperation(value = "banner列表", response = Banner.class , notes = "bannerList", responseContainer = "List")
    @GetMapping(value = "list")
    public ResponseInfo list(@ApiIgnore @LoginUser User user) {

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);

        SimpleSpecificationBuilder<Banner> builder = new SimpleSpecificationBuilder<>();
        builder.add("shopId", SpecificationOperator.Operator.eq, shop.getId());

        List<Banner> bannerList = bannerService.findList(builder.generateSpecification(), new Sort(Sort.Direction.DESC, "sortIndex"));

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("bannerList", bannerList);
        return responseInfo;
    }

    @ApiOperation(value = "删除banner", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "{id}")
    public ResponseInfo delete(@PathVariable Integer id,
                               @ApiIgnore @LoginUser User user) {
        //参数效验
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);
        Banner banner = bannerService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, banner);
        //权限效验
        AssertUtils.isTrue(PublicResCode.NOT_AUTHORIZED, shop.getId().equals(banner.getShopId()));
        bannerService.delete(banner);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改banner", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "banner", value = "banner图片") @RequestBody Banner banner,
                             @ApiIgnore @LoginUser User user) {
        //参数效验
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);
        Banner source = bannerService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, source);
        //权限效验
        AssertUtils.isTrue(PublicResCode.NOT_AUTHORIZED, shop.getId().equals(source.getShopId()));

        String[] checkFields = {"banner", "bannerImg"};
        AssertUtils.notNull(PARAMS_IS_NULL, checkFields, banner, banner.getBannerImg());

        if (StringUtils.isNotEmpty(banner.getLinkType())) {
            AssertUtils.isContain(PARAMS_EXCEPTION, banner.getLinkType(),
                    LINK_TYPE_PRODUCT, LINK_TYPE_SHOP, LINK_TYPE_CUSTOM_TEXT, LINK_TYPE_CUSTOM_LINK);
            AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"linkValue"}, banner.getLinkValue());
        }

        bannerService.modify(id, banner);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "修改排序", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceId", value = "源id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "destId", value = "目标id", required = true, paramType = "query", dataType = "int"),
    })
    @PutMapping(value = "sorted")
    public ResponseInfo sorted(@RequestParam Integer sourceId,
                               @RequestParam Integer destId) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, sourceId, destId);

        Banner source = bannerService.findById(sourceId);
        Banner dest = bannerService.findById(destId);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, source, dest);
        bannerService.swapPosition(source, dest);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "添加自定义页面", response = ResponseInfo.class)
    @PostMapping("custom/text")
    public ResponseInfo create(@ApiParam(name = "customText", value = "自定义文本") @RequestBody CustomText customText,
                               @ApiIgnore @LoginUser User user) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);
        ShopConfig shopConfig = shop.getShopConfig();

        //效验店铺是否为红积分/白积分商城
        AssertUtils.isTrue(PublicResCode.NOT_AUTHORIZED,
                ((shopConfig.getOpenWPoint() != null && shopConfig.getOpenWPoint()) ||
                        (shopConfig.getOpenRPoint() != null && shopConfig.getOpenRPoint())));
        customText.setShopId(shop.getId());
        String[] checkFields = {"customText", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields,
                customText, customText.getTitle(), customText.getContent());
        customTextService.create(customText);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "自定义页面列表", response = CustomTextDto.class, notes = "customTextList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "custom/text/list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") Integer pageNumber,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @ApiIgnore @LoginUser User user) {
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id"));
        SimpleSpecificationBuilder<CustomText> builder = new SimpleSpecificationBuilder<>();
        builder.add("shopId", SpecificationOperator.Operator.eq, shop.getId());

        Page<CustomText> customTextPage = customTextService.findAll(builder.generateSpecification(), pageable);
        List<CustomTextDto> customTextDtoList = customTextMapper.toPageDto(customTextPage.getContent());
        Page<CustomTextDto> customTextList = new PageImpl<>(customTextDtoList, pageable, customTextPage.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("customTextList", customTextList);
        return responseInfo;
    }

    @ApiOperation(value = "自定义页面详情", response = CustomTextDto.class, notes = "customText")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping(value = "custom/text/{id}")
    public ResponseInfo getCustomText(@PathVariable Integer id) {

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);

        CustomText customText = customTextService.findById(id);
        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("customText", customText);
        return responseInfo;
    }

    @ApiOperation(value = "修改自定义页面", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @PutMapping(value = "custom/text/{id}")
    public ResponseInfo edit(@PathVariable Integer id,
                             @ApiParam(name = "customText", value = "自定义文本") @RequestBody CustomText customText,
                             @ApiIgnore @LoginUser User user) {

        String[] checkFields = {"customText", "id", "title", "content"};

        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, checkFields,
                customText, id, customText.getTitle(), customText.getContent());

        CustomText text = customTextService.findById(id);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, new String[]{"id"}, text);

        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);
        AssertUtils.isTrue(PublicResCode.NOT_AUTHORIZED, shop.getId().equals(text.getShopId()));

        BeanUtils.copyPropertiesIgnoreNull(customText, text, "createTime", "updateTime", "id", "shopId");

        text.setUpdateTime(new Date());
        customTextService.save(text);

        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "删除自定义页面", response = ResponseInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "int")
    })
    @DeleteMapping(value = "custom/text/{id}")
    public ResponseInfo deleteCustomText(@PathVariable Integer id,
                               @ApiIgnore @LoginUser User user) {
        AssertUtils.notNull(PublicResCode.PARAMS_IS_NULL, id);
        CustomText customText = customTextService.findById(id);
        Shop shop = shopService.findByUser(user);
        AssertUtils.notNull(MerchantResCode.NO_MERCHANT, shop);
        AssertUtils.notNull(PublicResCode.PARAMS_EXCEPTION, customText);
        AssertUtils.isTrue(PublicResCode.NOT_AUTHORIZED, shop.getId().equals(customText.getShopId()));
        customTextService.delete(id);
        return ResponseUtils.buildResponseInfo();
    }

    @ApiOperation(value = "店铺列表", response = ShopDto.class, notes = "shopList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "店铺名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", paramType = "query", dataType = "int"),
    })
    @GetMapping(value = "shop/list")
    public ResponseInfo list(@RequestParam(defaultValue = "0") int pageNumber,
                             @RequestParam(defaultValue = "10") int pageSize,
                             @RequestParam(required = false) String name) {

        Pageable pageable = new PageRequest(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "recentSumOrder"));
        Specification<Shop> spec = shopService.buildBannerSpec(name);
        Page<Shop> shopList = shopService.findAll(spec, pageable);

        List<ShopDto> shopDtoListList = shopMapper.toDtoList(shopList.getContent());
        Page<ShopDto> shopDtoPage = new PageImpl<>(shopDtoListList, pageable, shopList.getTotalElements());

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("shopList", shopDtoPage);
        return responseInfo;
    }

    @ApiOperation(value = "商品列表", response = ProductGroupDto.class, notes = "productGroupList", responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "商品名称", paramType = "query", dataType = "string"),
    })
    @GetMapping(value = "product/list")
    public ResponseInfo listByState(@RequestParam(defaultValue = "0") int pageNumber,
                                    @RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(required = false) String name) {

        ResponseInfo responseInfo = ResponseUtils.buildResponseInfo();
        responseInfo.putData("productGroupList", productService.bannerProduct(pageNumber, pageSize, name));
        return responseInfo;
    }
}
