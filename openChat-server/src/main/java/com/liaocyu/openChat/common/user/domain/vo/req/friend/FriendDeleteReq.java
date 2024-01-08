package com.liaocyu.openChat.common.user.domain.vo.req.friend;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/8 10:47
 * @description :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendDeleteReq {
    @NotNull
    @ApiModelProperty("好友uid")
    private Long targetUid;
}
