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
 * @createTime : 2024/1/6 21:21
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApproveReq {

    @NotNull
    @ApiModelProperty("申请id")
    private Long applyId;
}
