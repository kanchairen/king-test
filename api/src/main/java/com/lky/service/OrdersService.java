package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.code.PublicResCode;
import com.lky.commons.utils.*;
import com.lky.dao.OrdersDao;
import com.lky.dao.OrdersReturnDao;
import com.lky.dao.ProductDao;
import com.lky.dto.*;
import com.lky.entity.*;
import com.lky.enums.code.ShopResCode;
import com.lky.enums.dict.*;
import com.lky.mapper.ImageMapper;
import com.lky.mapper.OrdersReturnMapper;
import com.lky.mapper.ShopMapper;
import com.lky.utils.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.dto.ProcessOrdersDto.ORDERS_TYPE_ONLINE;
import static com.lky.enums.code.OrderResCode.*;
import static com.lky.enums.dict.OrdersDict.*;
import static com.lky.enums.dict.OrdersReturnDict.RETURN_TYPE_EXCHANGE;
import static com.lky.enums.dict.ProductGroupDict.AUDIT_STATE_YES;

/**
 * 订单
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/19
 */
@Service
public class OrdersService extends BaseService<Orders, String> {

    @Inject
    private OrdersDao ordersDao;

    @Inject
    private ReceiveAddressService receiveAddressService;

    @Inject
    private FreightRuleService freightRuleService;

    @Inject
    private ProductService productService;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopMapper shopMapper;

    @Inject
    private ComputeService computeService;

    @Inject
    private OrdersItemService ordersItemService;

    @Inject
    private OrdersCloseService ordersCloseService;

    @Inject
    private OrdersReceiveService ordersReceiveService;

    @Inject
    private OrdersReturnService ordersReturnService;

    @Inject
    private OrdersSendService ordersSendService;

    @Inject
    private OrdersReturnMapper ordersReturnMapper;

    @Inject
    private PaymentRecordService paymentRecordService;

    @Inject
    private CartService cartService;

    @Inject
    private ExpressService expressService;

    @Inject
    private ProductGroupService productGroupService;

    @Inject
    private OrdersJobService ordersJobService;

    @Inject
    private UserMessageService userMessageService;

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private ProductDao productDao;

    @Inject
    private OrdersReturnDao ordersReturnDao;

    @Inject
    private PointService pointService;

    @Override
    public BaseDao<Orders, String> getBaseDao() {
        return this.ordersDao;
    }

    public Orders findByIdAndUser(String id, User user) {
        return ordersDao.findByIdAndUser(id, user);
    }

    /**
     * 确认订单详情
     *
     * @param user                 用户
     * @param ordersConfirmListDto 进入确认订单详情参数
     * @return 确认订单详情信息
     */
    public OrdersConfirmListVo confirmDetail(User user, OrdersConfirmListDto ordersConfirmListDto) {

        OrdersConfirmListVo ordersConfirmListVo = new OrdersConfirmListVo();

        Integer addressId = ordersConfirmListDto.getAddressId();
        ReceiveAddress receiveAddress;
        Integer cityId = null;
        //地址为空，使用默认地址
        if (addressId != null) {
            receiveAddress = receiveAddressService.findById(addressId);
//            AssertUtils.notNull(PARAMS_EXCEPTION, receiveAddress);
        } else {
            receiveAddress = receiveAddressService.findByFirstAndUserId(user.getId());
        }
        if (receiveAddress != null) {
            String addressDetail = receiveAddress.getAddressDetail();
            AddressDto addressDto = JsonUtils.jsonToObject(addressDetail, AddressDto.class);
            cityId = addressDto.getCity().getId();
        }

        ordersConfirmListVo.setReceiveAddress(receiveAddress);

        //获取确认订单列表
        List<OrdersConfirmDto> ordersConfirmDtos = ordersConfirmListDto.getOrdersConfirmDtos();
        if (!CollectionUtils.isEmpty(ordersConfirmDtos)) {
            List<OrdersConfirmVo> ordersConfirmVos = Lists.newArrayListWithCapacity(ordersConfirmDtos.size());
            for (OrdersConfirmDto ordersConfirmDto : ordersConfirmDtos) {
                OrdersConfirmVo ordersConfirmVo = new OrdersConfirmVo();

                AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"productConfirmDtos"}, ordersConfirmDto.getProductConfirmDtos());
                List<ProductConfirmDto> productConfirmDtos = ordersConfirmDto.getProductConfirmDtos();
                List<Map<String, Object>> proList = Lists.newArrayListWithCapacity(productConfirmDtos.size());
                List<ProductConfirmVo> productConfirmVos = Lists.newArrayListWithCapacity(productConfirmDtos.size());
                if (!CollectionUtils.isEmpty(productConfirmDtos)) {
                    productConfirmDtos.forEach(productConfirmDto -> {
                        String[] checkFiled = {"productId", "number"};
                        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, productConfirmDto.getProductId(), productConfirmDto.getNumber());
                        Product product = productService.findById(productConfirmDto.getProductId());
                        AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"productId"}, product);
                        //校验商品库存
                        AssertUtils.isTrue(PRODUCT_STOCK_NO, product.getName(), product.getStock() >= productConfirmDto.getNumber());
                        Map<String, Object> proMap = new HashMap<>();
                        proMap.put("id", product.getId());
                        proMap.put("number", productConfirmDto.getNumber());
                        proList.add(proMap);

                        ProductConfirmVo productConfirmVo = new ProductConfirmVo();
                        productConfirmVo.setNumber(productConfirmDto.getNumber());
                        productConfirmVo.setProduct(product);
                        productConfirmVos.add(productConfirmVo);
                    });
                }
                //计算订单运费
                double freightPrice = 0;
                if (cityId != null) {
                    freightPrice = freightRuleService.calculateFreight(proList, cityId);
                }
                Shop shop = shopService.findById(ordersConfirmDto.getShopId());
                AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"shopId"}, shop);

                ordersConfirmVo.setFreightPrice(freightPrice);
                ordersConfirmVo.setRemark(ordersConfirmDto.getRemark());
                ordersConfirmVo.setSendType(ordersConfirmDto.getSendType());
                ordersConfirmVo.setShopHeadDto(shopMapper.toHeadDto(shop));
                ordersConfirmVo.setProductConfirmVos(productConfirmVos);
                ordersConfirmVo.setRpointNum(ordersConfirmDto.getRpointNum());
                ordersConfirmVos.add(ordersConfirmVo);
            }
            ordersConfirmListVo.setOrdersConfirmVos(ordersConfirmVos);
        }
        return ordersConfirmListVo;
    }

    /**
     * 创建订单
     *
     * @param user                 用户
     * @param ordersConfirmListDto 创建订单参数
     * @return 生成的组合订单号，如果有多个用逗号隔开
     */
    public String create(User user, OrdersConfirmListDto ordersConfirmListDto) {

        //地址不能为空
        Integer addressId = ordersConfirmListDto.getAddressId();
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"addressId"}, addressId);
        ReceiveAddress receiveAddress = receiveAddressService.findById(addressId);
        AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"addressId"}, receiveAddress);
        AssertUtils.isTrue(PARAMS_EXCEPTION, "addressId", receiveAddress.getUserId() == user.getId());
        String addressDetail = receiveAddress.getAddressDetail();
        AddressDto addressDto = JsonUtils.jsonToObject(addressDetail, AddressDto.class);
        Integer cityId = addressDto.getCity().getId();

        //获取确认订单列表
        StringBuilder ordersIds = new StringBuilder();
        List<OrdersConfirmDto> ordersConfirmDtos = ordersConfirmListDto.getOrdersConfirmDtos();
        if (!CollectionUtils.isEmpty(ordersConfirmDtos)) {
            for (OrdersConfirmDto ordersConfirmDto : ordersConfirmDtos) {
                AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"productConfirmDtos"}, ordersConfirmDto.getProductConfirmDtos());
                List<ProductConfirmDto> productConfirmDtos = ordersConfirmDto.getProductConfirmDtos();
                List<Map<String, Object>> proList = Lists.newArrayListWithCapacity(productConfirmDtos.size());

                //订单号
                String orderCode = IdWorker.getOrderCode();
                //该订单获取的G米
                double givePoint = 0;
                //订单总金额
                double amount = 0;
                //订单应付金额
                double price = 0;
                //订单应付小米
                double rPointPrice = ordersConfirmDto.getRpointNum();
                //订单应付G米
                double wPointPrice = 0;
                //订单商品现金支付金额
                double productPrice = 0;
                //订单项中最低让利比
                double minBenefitRate = 0;
                //判断店铺是否有效
                Shop shop = shopService.findById(ordersConfirmDto.getShopId());
                ShopConfig shopConfig = shop.getShopConfig();
                AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"shopId"}, shop);
                AssertUtils.isTrue(NO_BUY_SELF_PRODUCT, shop.getUser().getId() != user.getId());
                AssertUtils.isTrue(ShopResCode.SHOP_CLOSE, shopService.judgeShopExpire(shop));

                if (!CollectionUtils.isEmpty(productConfirmDtos)) {
                    List<OrdersItem> itemList = Lists.newArrayListWithCapacity(productConfirmDtos.size());
                    for (ProductConfirmDto productConfirmDto : productConfirmDtos) {
                        String[] checkFiled = {"productId", "number"};
                        Integer number = productConfirmDto.getNumber();
                        AssertUtils.notNull(PARAMS_IS_NULL, checkFiled, productConfirmDto.getProductId(), number);
                        AssertUtils.isTrue(PARAMS_EXCEPTION, "number", number > 0);
                        Product product = productService.findById(productConfirmDto.getProductId());
                        AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"productId"}, product);
                        //校验商品库存
                        AssertUtils.isTrue(PRODUCT_STOCK_NO, product.getName(), product.getStock() >= productConfirmDto.getNumber());
                        AssertUtils.isTrue(PRODUCT_OFFLINE, product.getName(), !product.getOffline());
                        Map<String, Object> proMap = new HashMap<>();
                        proMap.put("id", product.getId());
                        proMap.put("number", number);
                        proList.add(proMap);

                        //获取商品让利比
                        double benefitRate = product.getBenefitRate();
                        //消费者获取的G米
                        double ordersItemGiveWPoint;
                        ordersItemGiveWPoint = computeService.consumerGiveWPoint(product.getPrice() * number, product.getBenefitRate());
                        if (shopConfig.getOpenRPoint() && rPointPrice > 0) {
                            if (minBenefitRate == 0 || minBenefitRate > benefitRate) {
                                minBenefitRate = benefitRate;
                            }
                        } else {
                            givePoint += ordersItemGiveWPoint;
                        }
                        productPrice += product.getPrice() * number;

                        amount += product.getPrice() * number;

                        wPointPrice += product.getWpointPrice() * number;
                        //生成线上订单项
                        OrdersItem ordersItem = new OrdersItem();
                        ordersItem.setPrice(product.getPrice());
                        ordersItem.setUserId(user.getId());
                        ordersItem.setShopId(shop.getId());
                        ordersItem.setNumber(number);
                        ordersItem.setOrdersId(orderCode);
                        ordersItem.setBenefitRate(benefitRate);
                        ordersItem.setProduct(product);
                        ordersItem.setGiveWPoint(ordersItemGiveWPoint);
                        ordersItem.setWpointPrice(product.getWpointPrice());
                        ordersItem.setUseRPoint(rPointPrice > 0);
                        itemList.add(ordersItem);

                        ProductGroup productGroup = productGroupService.findById(product.getProductGroupId());
                        AssertUtils.isTrue(PRODUCT_IS_OFFLINE, AUDIT_STATE_YES.compare(productGroup.getAuditState()) &&
                                !productGroup.getOffline());
                        //清空购物车商品
                        Cart cart = cartService.findByUserIdAndProductId(user.getId(), product.getId());
                        if (cart != null) {
                            cartService.delete(cart);
                        }
                        //下单即减少商品库存
                        product.setStock(product.getStock() - productConfirmDto.getNumber());
                        if (product.getStock() == 0) {
                            productGroup.setSellOutNumber(productGroup.getSellOutNumber() + 1);
                            productGroup.setOnSellNumber(productGroup.getOnSellNumber() - 1);
                            productGroupService.update(productGroup);
                        } else {
                            productService.update(product);
                        }
                    }
                    ordersItemService.save(itemList);
                }
                //小米/G米不为0时，相关校验
                if ((rPointPrice > 0) || (wPointPrice > 0)) {
                    UserAsset userAsset = user.getUserAsset();
                    if (rPointPrice > 0) {
                        AssertUtils.isTrue(SHOP_NOT_SUPPORT_RPOINT, shopConfig.getOpenRPoint());
                        AssertUtils.isTrue(PROINT_LACK, userAsset.getRpoint() >= rPointPrice);
                    }
                    if (wPointPrice > 0) {
                        AssertUtils.isTrue(NOT_WPOINT_SHOP, shopConfig.getOpenWPoint());
                        AssertUtils.isTrue(WPOINT_LACK, userAsset.getWpoint() >= wPointPrice);
                    }
                }

                //计算订单运费
                double freightPrice = freightRuleService.calculateFreight(proList, cityId);
                amount += freightPrice;

                //生成线上订单
                Orders orders = new Orders();
                orders.setId(orderCode);
                orders.setReceiveAddress(JsonUtils.objectToJson(receiveAddress));
                orders.setSendType(ordersConfirmDto.getSendType());
                orders.setRemark(ordersConfirmDto.getRemark());
                orders.setShopId(shop.getId());
                orders.setFreightPrice(freightPrice);
                orders.setProductPrice(productPrice);
                price = productPrice - rPointPrice * ComputeService.R_POINT_RATE + freightPrice;
                orders.setPrice(price);
                orders.setRpointPrice(rPointPrice);
                orders.setWpointPrice(wPointPrice);
                orders.setAmount(amount);
                orders.setUser(user);
                orders.setState(String.valueOf(STATE_WAIT));

                if (givePoint == 0 && minBenefitRate > 0) {
                    givePoint = computeService.consumerGiveWPoint(productPrice - rPointPrice * ComputeService.R_POINT_RATE, minBenefitRate);
                }
                orders.setGiveWPoint(givePoint);
                super.save(orders);

                //构建定时任务
                OrdersJob ordersJob = ordersJobService.buildJob(OrdersJobDict.TYPE_CLOSE, orders.getId());
                ordersJobService.add(ordersJob);
                ordersIds.append(orderCode).append(",");
            }
        }
        return ordersIds.deleteCharAt(ordersIds.length() - 1).toString();
    }

    public void close(Orders orders) {
        if (STATE_CLOSE.compare(orders.getState())) {
            return;
        }
        orders.setState(String.valueOf(STATE_CLOSE));
        orders.setUpdateTime(new Date());
        //关闭订单则退回占用库存
        List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId(orders.getId());
        if (!CollectionUtils.isEmpty(ordersItemList)) {
            for (OrdersItem ordersItem : ordersItemList) {
                Product product = ordersItem.getProduct();
                product.setStock(product.getStock() + ordersItem.getNumber());
                ProductGroup productGroup = productGroupService.findById(product.getProductGroupId());
                productGroupService.updateStateNumberAndPrice(productGroup, Boolean.TRUE);
                productGroupService.update(productGroup);
            }
        }
        super.update(orders);

        OrdersClose ordersClose = new OrdersClose();
        ordersClose.setOrdersId(orders.getId());
        ordersClose.setType(String.valueOf(OrdersCloseDict.TYPE_MANUAL));
        ordersClose.setUserId(orders.getUser().getId());
        ordersCloseService.save(ordersClose);
    }

    /**
     * 将超时未支付的订单，进行取消
     *
     * @param orders 需要关闭的订单
     */
    public void closeOverTime(Orders orders) {
        if (STATE_CLOSE.compare(orders.getState())) {
            return;
        }
        orders.setState(String.valueOf(STATE_CLOSE));
        orders.setUpdateTime(new Date());
        //关闭订单则退回占用库存
        List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId(orders.getId());
        if (!CollectionUtils.isEmpty(ordersItemList)) {
            for (OrdersItem ordersItem : ordersItemList) {
                Product product = ordersItem.getProduct();
                product.setStock(product.getStock() + ordersItem.getNumber());
                productService.update(product);
                //如果之前商品库存为0，则更新其商品组数据价格、可获得白积分、需要支付白积分、已售完商品数量
                if (product.getStock() == ordersItem.getNumber()) {
                    List<Product> productList = productDao.findByProductGroupId(product.getProductGroupId());
                    AssertUtils.isTrue(PublicResCode.SERVER_EXCEPTION, !CollectionUtils.isEmpty(productList));
                    ProductGroup productGroup = productGroupService.findById(product.getProductGroupId());
                    int sellOutNumber = 0;
                    for (Product temp : productList) {
                        if (!temp.getOffline() && temp.getStock() == 0) {
                            sellOutNumber++;
                        }
                    }
                    //更新商品组已售完商品数量
                    productGroup.setSellOutNumber(sellOutNumber);

                    List<Product> onlineProductList = productGroup.getProductList()
                            .stream().filter(p -> !p.getOffline())
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(onlineProductList)) {
                        Product headProduct = onlineProductList.get(0);
                        //如果第一个商品为退货商品则更新商品组的显示价格、G米价格、获得G米数量
                        if (product.getId() == headProduct.getId()) {
                            productGroup.setPrice(headProduct.getPrice());
                            productGroup.setWpointPrice(headProduct.getWpointPrice());
                            double wPoint = computeService.consumerGiveWPoint(headProduct.getPrice(), headProduct.getBenefitRate());
                            productGroup.setGetWPoint(wPoint);
                        }
                    }
                    productGroupService.update(productGroup);
                }
            }
        }
        super.update(orders);
        OrdersClose ordersClose = new OrdersClose();
        ordersClose.setOrdersId(orders.getId());
        ordersClose.setType(String.valueOf(OrdersCloseDict.TYPE_MANUAL));
        ordersClose.setUserId(orders.getUser().getId());
        ordersCloseService.save(ordersClose);
    }

    public void del(User user, Orders orders) {
        orders.setDeleted(Boolean.TRUE);
        orders.setUpdateTime(new Date());
        super.update(orders);
    }

    /**
     * 如果有正在申请退换货的订单或者审核拒绝的退换货订单，
     * 则删除退换货记录，更新订单项的退换货状态为null，
     * 用户和商家的G米原样更新，
     * 否则扣减退换货成功的订单项送的G米、小米、金额
     * <p>
     * <p>
     * 确认收货
     * 1、钱或小米转到商家资产里面
     * 2、用户获取的G米更新到用户资产中
     * 3、商家获取的G米更新到用户资产中
     * 4、冻结商家资产保证金
     * 5、解冻用户获取的G米
     * 6、解冻商家获取的G米
     * 7、推广下线权益
     * <p>
     * 自动确认收货（14天后）
     *
     * @param orders 订单
     */
    public void receive(Orders orders) {
        if (STATE_OVER.compare(orders.getState())) {
            return;
        }
        orders.setState(String.valueOf(STATE_OVER));
        orders.setUpdateTime(new Date());
        orders.setOverTime(new Date());
        //订单中有退款申请的情况处理
        if (orders.getReturned() != null && orders.getReturned()) {
            orders.setReturnState(String.valueOf(OrdersReturnDict.STATE_REFUSE));
            Boolean needUpdate = Boolean.FALSE;
            List<OrdersReturn> returnList = ordersReturnDao.findByOrdersId(orders.getId());
            if (!CollectionUtils.isEmpty(returnList)) {
                for (OrdersReturn ordersReturn : returnList) {
                    if (OrdersReturnDict.STATE_APPLY.compare(ordersReturn.getState())) {
                        ordersReturn.setState(String.valueOf(OrdersReturnDict.STATE_REFUSE));
                        ordersReturn.setUpdateTime(new Date());
                        needUpdate = Boolean.TRUE;
                    }
                }
                if (needUpdate) {
                    ordersReturnService.update(returnList);
                }
            }
            List<OrdersItem> ordersItemList = ordersItemService.findByOrdersId(orders.getId());
            if (!CollectionUtils.isEmpty(ordersItemList)) {
                needUpdate = Boolean.FALSE;
                for (OrdersItem ordersItem : ordersItemList) {
                    if (OrdersReturnDict.STATE_APPLY.compare(ordersItem.getReturnState())) {
                        ordersItem.setReturnState(String.valueOf(OrdersReturnDict.STATE_REFUSE));
                        needUpdate = Boolean.TRUE;
                    }
                }
                if (needUpdate) {
                    ordersItemService.update(ordersItemList);
                }
            }
        }
        super.update(orders);

        OrdersReceive ordersReceive = new OrdersReceive();
        ordersReceive.setUserId(orders.getUser().getId());
        ordersReceive.setOrdersId(orders.getId());
        ordersReceive.setType(String.valueOf(OrdersReceiveDict.TYPE_MANUAL));
        ordersReceiveService.save(ordersReceive);

        //删除定时任务
        ordersJobService.del(OrdersJobDict.TYPE_RECEIVE, orders.getId());

        PriceDto priceDto = paymentRecordService.computePayPrice(Lists.newArrayList(orders), Boolean.FALSE, Boolean.TRUE);

        ProcessOrdersDto processOrdersDto = new ProcessOrdersDto();
        processOrdersDto.setUser(orders.getUser());
        processOrdersDto.setShop(shopService.findById(orders.getShopId()));
        processOrdersDto.setOrdersType(ORDERS_TYPE_ONLINE);
        processOrdersDto.setNeedWaitReceive(Boolean.FALSE);
        BeanUtils.copyPropertiesIgnoreNull(priceDto, processOrdersDto);
        pointService.processAssertRecord(processOrdersDto);
    }

    public void ordersReturn(User user, Orders orders, OrdersReturnDto ordersReturnDto) {
        Integer ordersItemId = ordersReturnDto.getOrdersItemId();
        OrdersReturn ordersReturn;
        double price = 0;
        if (ordersItemId != null) {
            OrdersItem ordersItem = ordersItemService.findById(ordersItemId);
            AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"ordersItemId"}, ordersItem);
            price = ordersItem.getPrice() * ordersItem.getNumber();

            ordersReturn = ordersReturnService.findByOrdersItemId(ordersItemId);
            ordersItem.setReturnState(String.valueOf(OrdersReturnDict.STATE_APPLY));
            ordersItemService.update(ordersItem);
        } else {  //G米和小米商城退款,不退运费
            ordersReturn = ordersReturnService.findByOrdersId(orders.getId());
            orders.setReturnState(String.valueOf(OrdersReturnDict.STATE_APPLY));
            price = orders.getPrice() - orders.getFreightPrice();
        }
        if (ordersReturn == null) {
            ordersReturn = ordersReturnMapper.fromDto(ordersReturnDto);
        } else {
            BeanUtils.copyPropertiesIgnoreNull(ordersReturnDto, ordersReturn);
            if (ordersReturnDto.getProofImgList() != null) {
                ordersReturn.setProofImgIds(imageMapper.imgListToStr(ordersReturnDto.getProofImgList()));
            }
        }
        ordersReturn.setShopId(orders.getShopId());
        ordersReturn.setUserId(user.getId());
        ordersReturn.setState(String.valueOf(OrdersReturnDict.STATE_APPLY));
        //退金额
        if (!RETURN_TYPE_EXCHANGE.compare(ordersReturnDto.getReturnType())) {
            //小米商城订单退款
            if (orders.getRpointPrice() > 0) {
                ordersReturn.setRpointPrice(orders.getRpointPrice());
            }
            //G米商城订单退款
            if (orders.getWpointPrice() > 0) {
                ordersReturn.setWpointPrice(orders.getWpointPrice());
            }
            //现金金额退款
            if (price > 0) {
                ordersReturn.setPrice(price);
            }
        } else {
            ordersReturn.setRpointPrice(0);
            ordersReturn.setWpointPrice(0);
            ordersReturn.setPrice(0);
        }
        ordersReturnService.save(ordersReturn);

        orders.setReturned(Boolean.TRUE);
        super.update(orders);
    }

    public void send(User user, Orders orders, Integer expressId, String expressOdd) {
        if (STATE_RECEIVE.compare(orders.getState())) {
            return;
        }
        orders.setState(String.valueOf(STATE_RECEIVE));
        orders.setUpdateTime(new Date());
        orders.setSendTime(new Date());
        super.update(orders);

        //商家发货，给买家发送发货消息
        Map<String, Object> map = new HashMap<>();
        map.put("userId", orders.getUser().getId());
        map.put("orderId", orders.getId());
        Shop shop = shopService.findById(orders.getShopId());
        if (shop != null) {
            map.put("shopName", shop.getName());
        }
        userMessageService.create(UserMessageDict.TARGET_TYPE_SEND, map);

        OrdersSend ordersSend = new OrdersSend();
        if (expressId != null) {
            Express express = expressService.findById(expressId);
            AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"expressId"}, express);
            ordersSend.setExpress(express);
        }
        ordersSend.setUserId(user.getId());
        ordersSend.setExpressOdd(expressOdd);
        ordersSend.setOrdersId(orders.getId());
        ordersSend.setType(orders.getSendType());
        ordersSendService.save(ordersSend);

        //构建定时任务
        OrdersJob ordersJob = ordersJobService.buildJob(OrdersJobDict.TYPE_RECEIVE, orders.getId());
        ordersJobService.add(ordersJob);
    }

    /**
     * 前一天商家所有已完成订单的金额再减去退款的金额
     * 包括现金和小米
     *
     * @return 成交总额
     */
    public double sumMerchantOrdersPrice() {
        Date yesterdayTime = DateUtils.add(new Date(), Calendar.DATE, -1);
        Date yesterdayBegin = DateUtils.getBeginDate(yesterdayTime, Calendar.DAY_OF_YEAR);
        Date yesterdayEnd = DateUtils.getEndDate(yesterdayTime, Calendar.DAY_OF_YEAR);
        Specification<Orders> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("state"), String.valueOf(STATE_OVER)));
            predicates.add(cb.between(root.get("overTime"), yesterdayBegin, yesterdayEnd));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Orders> ordersList = super.findAll(spec);
        if (CollectionUtils.isEmpty(ordersList)) {
            return 0;
        }
        double sum = ordersList.stream().mapToDouble(Orders::getAmount).sum();
        //计算退款的金额
        double returnSum = ordersReturnService.sumOrdersReturned(ordersList);
        return sum - returnSum;
    }

    /**
     * 统计该区域下昨天/7天/30天线上订单的用户消费金额
     * 确认收货的订单金额 - 退款金额
     *
     * @param beginTime 开始时间, 如果null 则计算昨天消费金额
     * @param endTime   结束时间，如果null 则计算昨天消费金额
     * @param area      区域
     * @return 消费总额
     */
    public double sumConsumerAmountByArea(String area, Date beginTime, Date endTime) {
        if (StringUtils.isEmpty(area)) {
            return 0;
        }
        Specification<Orders> spec = (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("state"), String.valueOf(STATE_OVER)));
            if (beginTime != null && endTime != null) {
                predicates.add(cb.between(root.get("overTime"), beginTime, endTime));
            } else {
                Date yesterdayTime = DateUtils.add(new Date(), Calendar.DATE, -1);
                Date yesterdayBegin = DateUtils.getBeginDate(yesterdayTime, Calendar.DAY_OF_YEAR);
                Date yesterdayEnd = DateUtils.getEndDate(yesterdayTime, Calendar.DAY_OF_YEAR);
                predicates.add(cb.between(root.get("overTime"), yesterdayBegin, yesterdayEnd));
            }
            Join<Orders, User> userJoin = root.join("user", JoinType.LEFT);
            predicates.add(cb.like(userJoin.get("area"), area.trim() + "%"));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Orders> ordersList = super.findAll(spec);
        if (CollectionUtils.isEmpty(ordersList)) {
            return 0;
        }
        double sum = ordersList.stream().mapToDouble(Orders::getAmount).sum();
        //计算退款的金额
        double returnSum = ordersReturnService.sumOrdersReturned(ordersList);
        return sum - returnSum;
    }
}
