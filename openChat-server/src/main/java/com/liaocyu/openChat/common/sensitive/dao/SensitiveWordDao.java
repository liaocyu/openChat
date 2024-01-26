package com.liaocyu.openChat.common.sensitive.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liaocyu.openChat.common.sensitive.domain.SensitiveWord;
import com.liaocyu.openChat.common.sensitive.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Service;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/25 10:08
 * @description :
 */
@Service("sensitiveWordDao")
public class SensitiveWordDao extends ServiceImpl<SensitiveWordMapper, SensitiveWord> {

}
