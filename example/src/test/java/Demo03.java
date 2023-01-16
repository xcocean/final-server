import cn.hutool.core.util.StrUtil;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingkang
 * 2023/1/16
 **/
public class Demo03 {
    public static void main(String[] args) {
        ExpressionParser ep = new SpelExpressionParser();
        //创建上下文变量
        EvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("name", "hello");
        System.out.println(ep.parseExpression("{#name}").getValue(ctx));

        String path = "{ name }/{a}";
        System.out.println(get(path));

        PathMatcher pathMatcher=new AntPathMatcher();
        System.out.println(pathMatcher.match("/as/{asd}/{asd}","/as/sssssss/asd"));
        System.out.println(pathMatcher.match("/as/{asd}/{asd}","/as/sssssss/{asd}"));
        System.out.println(pathMatcher.extractUriTemplateVariables("/as/{asd}/{aa}","/as/sssssss/{asd}"));
    }

    private static List<String> get(String path) {
        List<String> list = new ArrayList<>();
        int start = path.indexOf("{");
        if (start != -1) {
            int end = path.indexOf("}", start + 1);
            if (end == -1)
                throw new RuntimeException("error");
            list.add(StrUtil.trimToEmpty(path.substring(start + 1, end)));
            for (; ; ) {
                start = path.indexOf("{", end);
                if (start == -1)
                    break;
                end = path.indexOf("}", start + 1);
                if (end == -1)
                    throw new RuntimeException("error");
                list.add(StrUtil.trimToEmpty(path.substring(start + 1, end)));
            }
        }

        return list;
    }
}
