package com.liaocyu.openChat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

import com.liaocyu.openChat.common.chat.domain.enums.GroupRoleEnum;
import lombok.*;

/**
 * <p>
 * 群成员表
 * </p>
 *
 * @author <a href="https://github.com/liaocyu">liaocyu</a>
 * @since 2024-01-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("group_member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群主id
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 成员uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 成员角色 1群主 2管理员 3普通成员
     *
     * @see GroupRoleEnum
     */
    @TableField("role")
    private Integer role;

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
