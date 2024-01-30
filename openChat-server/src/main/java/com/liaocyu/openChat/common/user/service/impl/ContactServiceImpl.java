package com.liaocyu.openChat.common.user.service.impl;

import com.liaocyu.openChat.common.chat.dao.ContactDao;
import com.liaocyu.openChat.common.chat.domain.dto.MsgReadInfoDTO;
import com.liaocyu.openChat.common.chat.domain.entity.Message;
import com.liaocyu.openChat.common.common.utils.AssertUtil;
import com.liaocyu.openChat.common.chat.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 17:52
 * @description :
 */
@Service("contactService")
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactDao contactDao;
    @Override
    public Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages) {
        Map<Long, List<Message>> roomGroup = messages.stream().collect(Collectors.groupingBy(Message::getRoomId));
        AssertUtil.equal(roomGroup.size(), 1, "只能查相同房间下的消息");
        Long roomId = roomGroup.keySet().iterator().next();
        Integer totalCount = contactDao.getTotalCount(roomId);
        return messages.stream().map(message -> {
            MsgReadInfoDTO readInfoDTO = new MsgReadInfoDTO();
            readInfoDTO.setMsgId(message.getId());
            Integer readCount = contactDao.getReadCount(message);
            readInfoDTO.setReadCount(readCount);
            readInfoDTO.setUnReadCount(totalCount - readCount - 1);
            return readInfoDTO;
        }).collect(Collectors.toMap(MsgReadInfoDTO::getMsgId, Function.identity()));
    }
}
