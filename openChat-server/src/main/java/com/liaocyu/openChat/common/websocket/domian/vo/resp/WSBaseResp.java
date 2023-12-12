package com.liaocyu.openChat.common.websocket.domian.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 14:54
 * @description : ws的基本返回信息体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {
    /**
     * ws 推动给前端的消息
     * @see com.liaocyu.openChat.common.websocket.domian.enums.WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
