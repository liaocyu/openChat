package com.liaocyu.openChat.common.websocket.domian.vo.resp.ws;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/8 15:00
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMemberChange {
    public static final Integer CHANGE_TYPE_ADD = 1;
    public static final Integer CHANGE_TYPE_REMOVE = 2;
    @ApiModelProperty("群组id")
    private Long roomId;
    @ApiModelProperty("变动uid集合")
    private Long uid;
    @ApiModelProperty("变动类型 1加入群组 2移除群组")
    private Integer changeType;
    /**
     * @see com.liaocyu.openChat.common.websocket.domian.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;
    @ApiModelProperty("最后一次上下线时间")
    private Date lastOptTime;
}
