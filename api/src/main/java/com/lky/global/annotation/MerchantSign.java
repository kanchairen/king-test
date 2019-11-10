package com.lky.global.annotation;

import java.lang.annotation.*;

/**
 * 商家标注，验证是否有商家权限
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/10/19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MerchantSign {
}
