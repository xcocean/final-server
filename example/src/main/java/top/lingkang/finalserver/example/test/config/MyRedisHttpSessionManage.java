package top.lingkang.finalserver.example.test.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lingkang.finalserver.server.core.impl.RedisHttpSessionManage;

/**
 * @author lingkang
 * Created by 2022/12/13
 */
@Configuration
public class MyRedisHttpSessionManage {

    @Bean
    public RedisHttpSessionManage httpSessionManage() {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0).setPassword(null);
        RedissonClient redissonClient = Redisson.create(config);
        return new RedisHttpSessionManage(redissonClient);
    }
}
