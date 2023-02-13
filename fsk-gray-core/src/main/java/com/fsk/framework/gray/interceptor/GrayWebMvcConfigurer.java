package com.fsk.framework.gray.interceptor;

import com.fsk.framework.gray.condition.AppTypeCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: Fsk App
 * @Copyright: com.fsk
 * @Version: 1.0.0
 * @Author: Gary
 * @UpdateTime: 2022/11/14
 * @Describe: GrayWebMvcConfigurer.
 */
//@Conditional(AppTypeCondition.class)
//@Configuration
public class GrayWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private GrayHandlerInterceptor grayHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(grayHandlerInterceptor).addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE);
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
