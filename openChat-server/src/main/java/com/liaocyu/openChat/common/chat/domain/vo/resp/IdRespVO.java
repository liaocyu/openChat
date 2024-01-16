package com.liaocyu.openChat.common.chat.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/15 10:39
 * @description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdRespVO {
    @ApiModelProperty("id")
    private long id;

    public static IdRespVO id(Long id) {
        IdRespVO idRespVO = new IdRespVO();
        idRespVO.setId(id);
        return idRespVO;
    }
}
