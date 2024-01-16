package com.liaocyu.openChat.common.chat.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/15 11:30
 * @description :
 */
@Data
public class AdminRevokeReq {
    @NotNull
    @ApiModelProperty("房间号")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 3)
    @ApiModelProperty("需要撤销管理的列表")
    private List<Long> uidList;
}
