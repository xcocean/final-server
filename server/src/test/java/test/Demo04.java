package test;

import cn.hutool.core.text.AntPathMatcher;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.regex.Pattern;

/**
 * @author lingkang
 * Created by 2022/12/9
 */
public class Demo04 {
    public static void main(String[] args) {
        AntPathMatcher matcher=new AntPathMatcher();
        System.out.println(matcher.match("/del/{name}/up","/del/asdasd/up"));
        System.out.println(matcher.match("/del/{name}/{up}","/del/asdasd/up"));

        String[] split = "/del/{name}/{up}".split("/");
        for (String s:split){
            if (s.contains("{")){

            }
        }
    }
}
