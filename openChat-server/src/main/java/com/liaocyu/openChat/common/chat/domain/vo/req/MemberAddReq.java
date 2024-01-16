package com.liaocyu.openChat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/14 16:24
 * @description : 添加群成员
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAddReq {
    @NotNull
    @ApiModelProperty("房间id")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 50)
    @ApiModelProperty("邀请的uid")
    private List<Long> uidList;
}
