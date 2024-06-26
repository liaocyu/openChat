package com.liaocyu.openChat.common.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.tomcat.jni.SSL.setPassword;

/**
 * Description:
 * Author: <a href="https://github.com/liaocyu">liaocyu</a>
 * Date: 2023-04-22
 */
@Configuration
public class RedissonConfig {
    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword());

        return Redisson.create(config);
    }
}
