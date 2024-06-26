package com.liaocyu.openChat.common.common.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/19 13:29
 * @description : 配置所有拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    TokenInterceptor tokenInterceptor;

    @Autowired
    CollectorInterceptor collectorInterceptor;

    @Autowired
    BlackInterceptor blackInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/capi/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/capi/**");
        registry.addInterceptor(blackInterceptor)
                .addPathPatterns("/capi/**");
    }
}
