package com.liaocyu.openChat.common.chat.domain.entity.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.liaocyu.openChat.common.chat.domain.entity.msg.*;
import com.liaocyu.openChat.common.common.utils.discover.domain.UrlInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/19 15:53
 * @description : TODO 消息扩展属性
 *
 * 对于 @EqualsAndHashCode(callSuper = true) 是 lombox提供的注解
 *       用于生成 equals 和 hashcode 方法
 *       默认使用 @EqualsAndHashCode 的方法时，callSuper 为false
 *       设置 callSuper 为true时，生成的equals和hashcode方法会调用父类的对应方法 并将父类的字段纳入比较或哈希计算的范围内
 *
 *  对于 @SuperBuilder 是 Lombox 提供的一个注解，用于生成具有父类属性和构造函数的构建器模式
 *       扩展了 @Builder 注解，使得子类可以继承父类的属性和构造函数，并包含父类属性的构建器方法
 *       使用时需要注意： 1、父类必须使用 @SuperBuilder 或者 @Builder注解，以便生成构建器方法
 *                     2、子类使用 @SuperBuilder 注解，并通过 @SuperBuilder(toBuilder = true) 属性将父类的构建器方法引入到子类中
 *                     3、在子类中使用 @Builder.Default 注解来设置默认值
 *
 *       @SuperBuilder
 *       public class ParentClass {
 *           private String name;
 *           private int age;
 *       }
 *
 *       @SuperBuilder(toBuilder = true)
 *       public class ChildClass extends ParentClass {
 *           private String childProperty;
 *       }
 *
 *       public class Main {
 *           public static void main(String[] args) {
 *               ChildClass child = ChildClass.builder()
 *                       .name("John")
 *                       .age(25)
 *                       .childProperty("Child Property")
 *                       .build();
 *           }
 *       }
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // ignoreUnknown 属性指示在反序列化过程中是否忽略未知的 JSON 属性。如果设置为 true，则 JSON 字符串中存在但在目标对象中没有对应属性的字段将被忽略，不会导致反序列化失败。
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageExtra implements Serializable {
    private static final long serialVersionUID = 1L;
    //url跳转链接
    private Map<String, UrlInfo> urlContentMap;
    //消息撤回详情
    private MsgRecall recall;
    //艾特的uid
    private List<Long> atUidList;
    //文件消息
    private FileMsgDTO fileMsg;
    //图片消息
    private ImgMsgDTO imgMsgDTO;
    //语音消息
    private SoundMsgDTO soundMsgDTO;
    //文件消息
    private VideoMsgDTO videoMsgDTO;

    /**
     * 表情图片信息
     */
    private EmojisMsgDTO emojisMsgDTO;

}
