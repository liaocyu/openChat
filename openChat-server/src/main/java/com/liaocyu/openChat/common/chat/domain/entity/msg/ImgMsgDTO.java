package com.liaocyu.openChat.common.chat.domain.entity.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2024/1/19 16:07
 * @description : 图片消息入参
 *
 * 对于 @EqualsAndHashCode(callSuper = true) 是 lombox提供的注解
 *      用于生成 equals 和 hashcode 方法
 *      默认使用 @EqualsAndHashCode 的方法时，callSuper 为false
 *      设置 callSuper 为true时，生成的equals和hashcode方法会调用父类的对应方法 并将父类的字段纳入比较或哈希计算的范围内
 *
 * 对于 @SuperBuilder 是 Lombox 提供的一个注解，用于生成具有父类属性和构造函数的构建器模式
 *      扩展了 @Builder 注解，使得子类可以继承父类的属性和构造函数，并包含父类属性的构建器方法
 *      使用时需要注意： 1、父类必须使用 @SuperBuilder 或者 @Builder注解，以便生成构建器方法
 *                    2、子类使用 @SuperBuilder 注解，并通过 @SuperBuilder(toBuilder = true) 属性将父类的构建器方法引入到子类中
 *                    3、在子类中使用 @Builder.Default 注解来设置默认值
 *
 *      @SuperBuilder
 *      public class ParentClass {
 *          private String name;
 *          private int age;
 *      }
 *
 *      @SuperBuilder(toBuilder = true)
 *      public class ChildClass extends ParentClass {
 *          private String childProperty;
 *      }
 *
 *      public class Main {
 *          public static void main(String[] args) {
 *              ChildClass child = ChildClass.builder()
 *                      .name("John")
 *                      .age(25)
 *                      .childProperty("Child Property")
 *                      .build();
 *          }
 *      }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImgMsgDTO extends BaseFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("宽度（像素）")
    @NotNull
    private Integer width;

    @ApiModelProperty("高度（像素）")
    @NotNull
    private Integer height;

}
