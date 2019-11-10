package com.lky.service;

import com.google.common.collect.Lists;
import com.lky.commons.base.BaseDao;
import com.lky.commons.base.BaseService;
import com.lky.commons.utils.AssertUtils;
import com.lky.commons.utils.CollectionUtils;
import com.lky.dao.CartDao;
import com.lky.dto.CartListDto;
import com.lky.entity.Cart;
import com.lky.entity.Product;
import com.lky.entity.Shop;
import com.lky.entity.User;
import com.lky.mapper.CartMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.lky.commons.code.PublicResCode.PARAMS_EXCEPTION;
import static com.lky.commons.code.PublicResCode.PARAMS_IS_NULL;
import static com.lky.enums.code.OrderResCode.PRODUCT_STOCK_NO;

/**
 * 购物车
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/24
 */
@Service
public class CartService extends BaseService<Cart, Integer> {

    @Inject
    private CartDao cartDao;

    @Inject
    private CartMapper cartMapper;

    @Inject
    private ProductService productService;

    @Inject
    private ShopService shopService;

    @Override
    public BaseDao<Cart, Integer> getBaseDao() {
        return this.cartDao;
    }

    public List<Cart> findByUserId(Integer userId) {
        return cartDao.findByUserIdOrderByIdDesc(userId);
    }

    public Cart findByUserIdAndProductId(Integer userId, Integer productId) {
        return cartDao.findByUserIdAndProductId(userId, productId);
    }

    public void create(User user, Integer productId, int number) {

        Product product = productService.findById(productId);
        AssertUtils.notNull(PARAMS_EXCEPTION, product);
        int stock = product.getStock();
        AssertUtils.isTrue(PRODUCT_STOCK_NO, stock >= number);

        //查询购物车中是否有同一个商品
        Cart cart = this.findByUserIdAndProductId(user.getId(), productId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(user.getId());
            cart.setProductId(productId);
            cart.setNumber(number);
            cart.setShopId(product.getShopId());
            super.save(cart);
        } else {
            cart.setNumber(number + cart.getNumber());
            super.update(cart);
        }
    }

    public Cart modify(User user, Cart c) {
        Integer productId = c.getProductId();
        int number = c.getNumber();
        AssertUtils.notNull(PARAMS_IS_NULL, new String[]{"id", "productId", "number"}, c.getId(), productId, number);
        AssertUtils.isTrue(PARAMS_EXCEPTION, number > 0);
        //查询购物车中是否有同一个商品
        Cart cart = this.findByUserIdAndProductId(user.getId(), productId);
        if (cart != null && !c.getId().equals(cart.getId())) {
            number += cart.getNumber();
            super.delete(c.getId());
        } else {
            cart = super.findById(c.getId());
            AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"cart"}, cart);
            AssertUtils.notNull(PARAMS_EXCEPTION, new String[]{"productId"}, productService.findById(productId));
            cart.setProductId(productId);
        }
        cart.setNumber(number);
        cart.setUpdateTime(new Date());
        super.update(cart);
        return cart;
    }

    public List<CartListDto> list(User user) {
        List<Cart> cartList = this.findByUserId(user.getId());
        if (!CollectionUtils.isEmpty(cartList)) {
//            Map<Integer, List<Cart>> collect = cartList.stream().collect(Collectors.groupingBy(Cart::getShopId));
            Map<Integer, List<Cart>> collect = new LinkedHashMap<>();
            if (!CollectionUtils.isEmpty(cartList)) {
                for (Cart cart : cartList) {
                    Integer shopId = cart.getShopId();
                    if (collect.containsKey(shopId)) {
                        collect.get(shopId).add(cart);
                    } else {
                        List<Cart> list = new ArrayList<>();
                        list.add(cart);
                        collect.put(shopId, list);
                    }
                }
            }
            List<CartListDto> list = Lists.newArrayListWithCapacity(collect.size());
            for (Map.Entry<Integer, List<Cart>> entry : collect.entrySet()) {
                Shop shop = shopService.findById(entry.getKey());

                CartListDto cartListDto = new CartListDto();
                cartListDto.setShopId(shop.getId());
                cartListDto.setShopName(shop.getName());
                cartListDto.setCartList(cartMapper.toListDto(entry.getValue()));
                list.add(cartListDto);
            }
            return list;
        }
        return null;
    }
}
