package top.lingkang.finalserver.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Controller;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.annotation.FinalServerBoot;

import java.util.Arrays;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
@FinalServerBoot
@Controller
public class Demo01 {
    @Autowired
    private static Environment environment;

    public static void main(String[] args) {
        FinalServerApplication.run(Demo01.class, args);
        StandardEnvironment bean = FinalServerApplication.applicationContext.getBean(StandardEnvironment.class);
        System.out.println(bean.getProperty("server.port"));
    }
}
