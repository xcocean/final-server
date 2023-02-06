package top.lingkang.finalserver.server.core.impl;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import top.lingkang.finalserver.server.core.FinalServerConfiguration;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.HttpSessionManage;
import top.lingkang.finalserver.server.utils.SerializableUtils;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.http.HttpSession;
import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Session;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author lingkang
 * Created by 2022/12/12
 * @since 1.0.0
 * 数据库存储会话，默认使用MySQL，例
 * // 使用了 beecp 连接池 <a href="https://gitee.com/Chris2018998/BeeCP">beecp</a>
 *  @Configuration
 *  public class MyHttpSessionManage {
 *      @Bean
 *      public DbHttpSessionManage dbHttpSessionManage() {
 *      BeeDataSourceConfig config = new BeeDataSourceConfig();
 *      config.setDriverClassName("com.mysql.cj.jdbc.Driver");
 *      config.setJdbcUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC");
 *      config.setUsername("root");
 *      config.setPassword("123456");
 *      config.setMaxActive(10);
 *      BeeDataSource dataSource = new BeeDataSource(config);
 *      return new DbHttpSessionManage(dataSource);
 *      }
 *  }
 *
 * <p>
 * 建表sql
 * <p>
 * CREATE TABLE `f_store_session` (
 * `id` varchar(50) NOT NULL,
 * `content` blob,
 * `last_time` datetime DEFAULT NULL,
 * PRIMARY KEY (`id`) USING BTREE
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
 */
public class DbHttpSessionManage implements HttpSessionManage {
    private Timer timer = new Timer();
    private DataSource dataSource;
    private static final ThreadLocal<Session> localSession = new ThreadLocal<>();

    public DbHttpSessionManage(DataSource dataSource) {
        if (dataSource == null) {
            throw new RuntimeException("使用数据库存储会话，必须配置数据源：DataSource, 异常原因：DataSource 为空");
        }
        this.dataSource=dataSource;
        // 会话淘汰机制
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanSession();
            }
        }, 6000000, 1800000);// 启动后1小时执行一次，之后每30分钟执行一次
    }

    @Override
    public Session getSession(Request request) {
        Session get = localSession.get();
        if (get != null)
            return get;

        Cookie cookie = request.getCookie(FinalServerProperties.server_session_name);
        if (cookie == null) {
            return new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
        }

        Session session = getSession(cookie.value());
        if (session == null) {
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
        } else if (session.isExpire()) {
            removeSession(cookie.value());
            session = new HttpSession(FinalServerConfiguration.idGenerateFactory.generateSessionId(request));
        }
        localSession.set(session);
        return session;
    }

    @Override
    public void bindCurrentSession(FinalServerContext context) {
        Session session = context.getRequest().getSession();
        if (session.hasUpdateAttribute()) {
            storeSession(session);
            DefaultCookie cookie = new DefaultCookie(FinalServerProperties.server_session_name, context.getRequest().getSession().getId());
            cookie.setMaxAge(FinalServerProperties.server_session_age);
            cookie.setPath("/");
            context.getResponse().addCookie(cookie);
        }
    }

    private Session getSession(String id) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("select * from f_store_session where id=?");
            statement.setMaxRows(1);
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                byte[] contents = resultSet.getBytes("content");
                if (contents == null)
                    return null;
                return SerializableUtils.byteToObject(contents);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void removeSession(String id) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("delete from f_store_session where id=?");
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void storeSession(Session session) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(
                    "select count(id) from f_store_session where id=?"
            );
            statement.setObject(1, session.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getLong(1) == 0) {
                statement = connection.prepareStatement(
                        "insert into f_store_session(id,content,last_time) values(?,?,?)"
                );
                statement.setObject(1, session.getId());
                statement.setObject(2, SerializableUtils.objectToByte(session));
                statement.setObject(3, new Date());
                statement.executeUpdate();
            } else {// 更新
                statement = connection.prepareStatement(
                        "update f_store_session set content=? where id=?"
                );
                statement.setObject(1, SerializableUtils.objectToByte(session));
                statement.setObject(2, session.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cleanSession() {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("delete from f_store_session where last_time<");
            // 预留10分钟
            long removeTime = System.currentTimeMillis() - FinalServerProperties.server_session_age - 600000L;
            statement.setObject(1, new Date(removeTime));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
