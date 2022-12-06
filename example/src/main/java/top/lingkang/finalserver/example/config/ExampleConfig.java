package top.lingkang.finalserver.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lingkang.finalserver.example.MyTest;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@Configuration
public class ExampleConfig {
    @Bean
    public MyTest myTest(){
        return new MyTest();
    }

}
