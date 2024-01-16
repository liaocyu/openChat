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
 * @createTime : 2024/1/14 18:00
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberExitReq {
    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}
