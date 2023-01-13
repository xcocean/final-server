package top.lingkang.finalserver.example.app;

import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lingkang.finalsql.config.SqlConfig;
import top.lingkang.finalsql.dialect.Mysql57Dialect;
import top.lingkang.finalsql.sql.FinalSql;
import top.lingkang.finalsql.sql.core.FinalSqlManage;

/**
 * @author lingkang
 * 2023/1/11
 **/
@Configuration
public class DbConfig {
    @Bean
    public FinalSql finalSql() {
        // 初始化连接池
        BeeDataSourceConfig config = new BeeDataSourceConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");// org.sqlite.JDBC
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC");
        config.setUsername("root");
        config.setPassword("123456");
        config.setMaxActive(5);
        BeeDataSource dataSource = new BeeDataSource(config);
        // 配置orm
        SqlConfig sqlConfig = new SqlConfig(dataSource);
        sqlConfig.setShowLog(true);
        sqlConfig.setSqlDialect(new Mysql57Dialect());
        return new FinalSqlManage(sqlConfig);
    }
}
