package com.lky.global.annotation;

import java.lang.annotation.*;

/**
 * api接口，忽略Token验证
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthIgnore {
}
