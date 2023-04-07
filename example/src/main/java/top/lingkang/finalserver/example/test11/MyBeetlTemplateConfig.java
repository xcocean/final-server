package top.lingkang.finalserver.example.test11;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lingkang.finalserver.server.core.impl.BeetlHttpParseTemplate;
import top.lingkang.finalserver.server.web.handler.LocalStaticMapping;

import java.util.List;

/**
 * @author lingkang
 * 2023/2/13
 **/
@Configuration
public class MyBeetlTemplateConfig extends BeetlHttpParseTemplate {
    @Bean
    public LocalStaticMapping localStaticMapping(){
        return new LocalStaticMapping(){
            @Override
            public void addStaticByAbsolutePath(List<String> paths) {
                paths.add("C:\\Users\\Administrator\\Videos");
            }
        };
    }
}
