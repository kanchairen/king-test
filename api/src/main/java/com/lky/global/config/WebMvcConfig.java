package com.lky.global.config;

import com.lky.global.converter.StringToBaseDictConverterFactory;
import com.lky.global.inteceptor.AuthInterceptor;
import com.lky.global.inteceptor.BizInterceptor;
import com.lky.global.resolver.LoginUserResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.GzipResourceResolver;

import javax.inject.Inject;
import java.util.List;

import static com.lky.enums.dict.AppVersionDict.APP_APK_DIR;

/**
 * 配置拦截器
 * <p>
 *
 * @author luckyhua
 * @version 1.0
 * @EnableWebMvc+继承WebMvcConfigurationAdapter会屏蔽springboot的@EnableAutoConfiguration的设置 继承WebMvcConfigurationSupport会屏蔽spring boot的@EnableAutoConfiguration的设置，无需使用@EnableWebMvc注解
 * 继承WebMvcConfigurationAdapter依旧使用spring boot的@EnableAutoConfiguration的设置
 * </p>
 * @since 2017/9/19
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Inject
    private AuthInterceptor authInterceptor;

    @Inject
    private BizInterceptor bizInterceptor;

    @Inject
    private LoginUserResolver loginUserResolver;

//    @Bean
//    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
//        RequestMappingHandlerMapping handlerMapping = new CustomRequestMapping();
//        handlerMapping.setOrder(0);
//        handlerMapping.setInterceptors(getInterceptors());
//        return handlerMapping;
//    }

    /**
     * 发现如果继承了WebMvcConfigurationSupport，则在yml中配置的相关内容会失效。
     * 去除@EnableWebMvc，@EnableWebMvc是开启注解驱动
     * 下面方法来自于父类，属于spring内部已经实现的东西，不属于ioc容器，直接编码的内容是可以运行的
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger/**").addResourceLocations("classpath:/static/swagger/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        // 相当于application中的spring.resources.static-locations
        registry.addResourceHandler("/apk/**")
                .addResourceLocations("file:" + APP_APK_DIR.getKey())
                .resourceChain(true)
                .addResolver(new GzipResourceResolver());

        super.addResourceHandlers(registry);
    }

    /**
     * 添加自定义转换器
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToBaseDictConverterFactory());
    }

    /**
     * 配置servlet处理
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/**").addPathPatterns("/biz/**");
        registry.addInterceptor(bizInterceptor).addPathPatterns("/biz/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserResolver);
    }
}
