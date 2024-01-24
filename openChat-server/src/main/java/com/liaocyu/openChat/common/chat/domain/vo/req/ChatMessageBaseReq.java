package com.liaocyu.openChat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 16:46
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageBaseReq {

    @NotNull
    @ApiModelProperty("消息Id")
    private Long msgId;

    @NotNull
    @ApiModelProperty("会话Id")
    private Long roomId;
}
