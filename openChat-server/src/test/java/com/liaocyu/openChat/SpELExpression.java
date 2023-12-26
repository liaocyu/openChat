package com.liaocyu.openChat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : create by lcy
 * @Project : openChat
 * @createTime : 2023/12/25 16:27
 * @description :
 * el 表达式的简单使用
 *
 * 实现原理
 * 1、创建解析器： SpEL使用ExpressionParser接口表示解析器，提供SpelExpressionParser默认实现
 * 2、构造上下文： 准备需要的上下文数据，使用 StandardEvaluationContext 的 setVariable 来设置上下文数据
 * 3、解析表达式： 使用ExpressionParser 的 parseExpression 来解析相应的表达式为 Expression 对象
 * 4、求值： 通过Expression 接口的getValue方法根据上下文（EvaluationContext , RootObjext） 获得表达式值
 */
@SpringBootTest
public class SpELExpression {

    @Test
    void spelExpression() {
        List<Integer> primes = new ArrayList<Integer>();
        primes.addAll(Arrays.asList(2,3,5,7,11,13,17));

        // 创建解析器
        ExpressionParser parser = new SpelExpressionParser();
        //构造上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("primes",primes);

        //解析表达式
        Expression exp =parser.parseExpression("#primes.?[#this>10]");
        // 求值
        List<Integer> primesGreaterThanTen = (List<Integer>)exp.getValue(context);
        System.out.println(primesGreaterThanTen);
    }
}
