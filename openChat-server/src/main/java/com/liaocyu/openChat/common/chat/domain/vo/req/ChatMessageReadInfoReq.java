package com.liaocyu.openChat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/22 17:46
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadInfoReq {

    @ApiModelProperty("消息id集合（只查本人）")
    @Size(max = 20)
    private List<Long> msgIds;
}
