package com.lky.global.version;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * 接口版本标识注解
 *
 * @author luckyhua
 * @version 1.0
 * @since 2017/9/20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ApiVersion {

    int value();
}
