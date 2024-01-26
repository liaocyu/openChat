package com.liaocyu.openChat;

import com.liaocyu.openChat.common.sensitive.filter.ACFilter;
import com.liaocyu.openChat.common.sensitive.filter.DFAFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 11:05
 * @description :
 */
public class SensitiveTest {

    @Test
    public void DFA() {
        // 敏感词列表
        List<String> sensitiveList = Arrays.asList("abcd", "abcbba", "adabca");
        DFAFilter instance = DFAFilter.getInstance();
        instance.loadWord(sensitiveList); // 加载敏感词列表并构建 树
        System.out.println(instance.hasSensitiveWord("adabcd"));
    }

    @Test
    public void AC() {

        List<String> sensitiveList = Arrays.asList("abcd", "abcbba", "adabca");
        ACFilter instance = new ACFilter();
        instance.loadWord(sensitiveList);
        instance.hasSensitiveWord("adabcd");
    }
    @Test
    public void DFAMulti() {
        List<String> sensitiveList = Arrays.asList("abcd", "ada", "adabca");
        DFAFilter instance = DFAFilter.getInstance();
        instance.loadWord(sensitiveList);
        System.out.println(instance.filter("adabcda"));
    }
    @Test
    public void ACMulti() {
        List<String> sensitiveList = Arrays.asList("abcd", "ada", "adabca");
        ACFilter instance = new ACFilter();
        instance.loadWord(sensitiveList);
        System.out.println(instance.filter("adabcda"));
    }
}
