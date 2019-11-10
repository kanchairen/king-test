package com.lky.modules.api;

import com.lky.commons.utils.CollectionUtils;
import com.lky.entity.Cart;
import com.lky.entity.Shop;
import com.lky.service.CartService;
import com.lky.service.ShopService;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车测试
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/11/6
 */
public class CartTest extends BaseTest {

    @Inject
    private CartService cartService;

    @Inject
    private ShopService shopService;

    @Test
    public void cartList() {
        List<Cart> cartList = cartService.findByUserId(2);
        if (!CollectionUtils.isEmpty(cartList)) {
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
            for (Map.Entry<Integer, List<Cart>> entry : collect.entrySet()) {
                Shop shop = shopService.findById(entry.getKey());
                System.out.println(shop.getId() + "-------------" + shop.getName());
            }
        }
    }
}
