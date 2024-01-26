package com.liaocyu.openChat.common.sensitive.config;

import com.liaocyu.openChat.common.sensitive.service.impl.MyWordFactory;
import com.liaocyu.openChat.common.sensitive.bootstrap.SensitiveWordBs;
import com.liaocyu.openChat.common.sensitive.filter.DFAFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 10:04
 * @description :
 */
@Configuration
@RequiredArgsConstructor
public class SensitiveWordConfig {
    private final MyWordFactory myWordFactory;

    /**
     * 初始化引导类
     *
     * @return 初始化引导类
     * @since 1.0.0
     */
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .filterStrategy(DFAFilter.getInstance())
                .sensitiveWord(myWordFactory)
                .init();
    }
}
