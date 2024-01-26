package com.liaocyu.openChat.common.sensitive.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 10:07
 * @description :
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sensitive_word")
public class SensitiveWord {
    private String word;
}
