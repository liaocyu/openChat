package com.liaocyu.openChat.common.sensitive.service;

import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 10:11
 * @description :
 */
public interface IWordFactory {

    /**
     * 返回敏感词数据源
     *
     * @return 结果
     * @since 0.0.13
     */
    List<String> getWordList();
}
