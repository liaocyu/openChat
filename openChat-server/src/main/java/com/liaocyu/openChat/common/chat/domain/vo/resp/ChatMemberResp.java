package com.liaocyu.openChat.common.chat.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/13 17:05
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp {
    @ApiModelProperty("uid")
    private Long uid;
    /**
     * @see com.liaocyu.openChat.common.websocket.domian.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;

    /**
     * 角色ID
     */
    private Integer roleId;

    @ApiModelProperty("最后一次上下线时间")
    private Date lastOptTime;
}
