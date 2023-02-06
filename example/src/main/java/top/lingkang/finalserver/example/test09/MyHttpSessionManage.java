package top.lingkang.finalserver.example.test09;

import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lingkang.finalserver.server.core.impl.DbHttpSessionManage;

/**
 * @author lingkang
 * 2023/2/6
 **/
@Configuration
public class MyHttpSessionManage {
    @Bean
    public DbHttpSessionManage dbHttpSessionManage() {
        BeeDataSourceConfig config = new BeeDataSourceConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");// org.sqlite.JDBC
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC");
        config.setUsername("root");
        config.setPassword("123456");
        config.setMaxActive(10);
        BeeDataSource dataSource = new BeeDataSource(config);
        return new DbHttpSessionManage(dataSource);
    }
}
