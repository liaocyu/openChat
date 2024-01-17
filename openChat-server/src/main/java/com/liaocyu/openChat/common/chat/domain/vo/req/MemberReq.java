package com.liaocyu.openChat.common.chat.domain.vo.req;

import com.liaocyu.openChat.common.common.domain.vo.req.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/13 17:09
 * @description :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReq extends CursorPageBaseReq {
    @ApiModelProperty("房间号")
    private Long roomId = 1L; // 默认设置为1L
}
