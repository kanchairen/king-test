package com.lky.mapper;

import com.lky.dto.CartDto;
import com.lky.entity.Cart;
import com.lky.entity.Product;
import com.lky.service.ProductService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import javax.inject.Inject;
import java.util.List;

/**
 * 购物车Dto转换
 *
 * @author huangjingang
 * @version 1.0
 * @since 2017/10/20
 */

@Mapper(componentModel = "jsr330")
public abstract class CartMapper {

    @Inject
    private ProductService productService;

    @Mappings({
            @Mapping(source = "productId", target = "product"),
    })
    public abstract CartDto toDto(Cart cart);

    public abstract List<CartDto> toListDto(List<Cart> cartList);

    public Product idToProduct(Integer productId) {
        return productId != null ? productService.findById(productId) : null;
    }
}
