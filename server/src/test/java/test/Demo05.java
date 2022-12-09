package test;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import top.lingkang.finalserver.server.annotation.GET;

/**
 * @author lingkang
 * Created by 2022/12/10
 */
public class Demo05 {
    public static void main(String[] args) {
        System.out.println(GET.class.getSimpleName());
    }
}
