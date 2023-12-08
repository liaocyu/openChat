package com.liaocyu.openChat.common.websocket.domian.vo.req;

import lombok.Data;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 14:31
 * @description : webSocket 前端请求体
 */
@Data
public class WSBaseReq {
    /**
     * 请求类型
     * @see com.liaocyu.openChat.common.websocket.domian.enums.WSReqTypeEnum
     */
    private Integer type;
    /**
     * 每个请求包具体的数据，类型不同结果不同
     * 传过来的是json字符串，反序列化为java对象的
     */
    private String data;
}
