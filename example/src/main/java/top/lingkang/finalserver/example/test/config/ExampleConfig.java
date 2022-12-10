package top.lingkang.finalserver.example.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
