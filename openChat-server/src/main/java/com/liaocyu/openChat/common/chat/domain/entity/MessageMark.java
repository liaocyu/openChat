package com.liaocyu.openChat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/10 10:02
 * @description : 消息标记表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message_mark")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageMark implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息表id
     */
    @TableField("msg_id")
    private Long msgId;

    /**
     * 标记人uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 标记类型 1点赞 2举报
     */
    @TableField("type")
    private Integer type;

    /**
     * 消息状态 0正常 1取消
     */
    @TableField("status")
    @TableLogic(value = "0", delval = "1")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
