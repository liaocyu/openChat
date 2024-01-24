package com.liaocyu.openChat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/24 15:51
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiResp {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "表情url")
    private String expressionUrl;
}
