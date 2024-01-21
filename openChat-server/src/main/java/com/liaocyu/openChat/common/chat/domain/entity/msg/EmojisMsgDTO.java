package com.liaocyu.openChat.common.chat.domain.entity.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/19 16:33
 * @description : 表情图片消息入参
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmojisMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("下载地址")
    @NotBlank
    private String url;
}
