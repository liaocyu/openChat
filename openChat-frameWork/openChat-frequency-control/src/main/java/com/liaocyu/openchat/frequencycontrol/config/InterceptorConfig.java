package com.liaocyu.openchat.frequencycontrol.config;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 15:33
 * @description :
 */

import com.liaocyu.openchat.frequencycontrol.inteceptor.CollectorInterceptor;
import com.liaocyu.openchat.frequencycontrol.inteceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 配置所有拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private  TokenInterceptor tokenInterceptor;
    @Resource
    private  CollectorInterceptor collectorInterceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/capi/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/capi/**");
    }
}

