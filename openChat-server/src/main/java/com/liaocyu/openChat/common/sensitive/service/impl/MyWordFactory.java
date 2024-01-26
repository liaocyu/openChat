package com.liaocyu.openChat.common.sensitive.service.impl;

import com.liaocyu.openChat.common.sensitive.dao.SensitiveWordDao;
import com.liaocyu.openChat.common.sensitive.domain.SensitiveWord;
import com.liaocyu.openChat.common.sensitive.service.IWordFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 10:06
 * @description :
 */
@Component("myWordFactory")
@RequiredArgsConstructor
public class MyWordFactory implements IWordFactory {
    private final SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> getWordList() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }

}
