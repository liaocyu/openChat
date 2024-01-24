package com.liaocyu.openChat.common.user.service;

import cn.hutool.core.collection.CollUtil;
import com.liaocyu.openChat.common.chat.domain.dto.MsgReadInfoDTO;
import com.liaocyu.openChat.common.chat.domain.entity.Contact;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liaocyu.openChat.common.chat.domain.entity.Message;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会话列表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
public interface ContactService{

    Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages);
}
