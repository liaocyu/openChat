package com.liaocyu.openChat.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 15:03
 * @description :
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
@Target(ElementType.METHOD) // 作用在方法上
public @interface RedissonLock {

    /**
     * key的前缀，默认取方法全限定名，可以自己指定
     * @return
     */
    String prefixKey() default "";

    /**
     * 支持SpringEl表达式，自定义key
     * @return
     */
    String key();

    /**
     * 等待锁的排队时间，默认快速失败
     * @return
     */
    int waitTime() default -1;

    /**
     * 等待时间单位，默认毫秒
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

}
