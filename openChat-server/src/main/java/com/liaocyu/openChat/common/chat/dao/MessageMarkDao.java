package com.liaocyu.openChat.common.chat.dao;

import com.liaocyu.openChat.common.chat.domain.entity.MessageMark;
import com.liaocyu.openChat.common.chat.mapper.MessageMarkMapper;
import com.liaocyu.openChat.common.chat.service.IMessageMarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-10
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark>{

}
