package top.lingkang.finalserver.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.web.http.FinalServerContext;
import top.lingkang.finalserver.server.web.security.http.FinalSecurityHolder;
import top.lingkang.finalsql.sql.Condition;
import top.lingkang.finalsql.sql.FinalSql;

import java.util.Date;
import java.util.List;

/**
 * @author lingkang
 * 2023/1/11
 **/
@Controller
public class WebController {
    @Autowired
    private FinalSql finalSql;
    @Autowired
    private FinalSecurityHolder finalSecurityHolder;

    @GET("/sql")
    public void s(FinalServerContext context) {
        List<User> select = finalSql.select(User.class);
        context.getResponse().returnJsonObject(select);
        update();
    }

    public void insert() {
        User user = new User();
        user.setId(2);
        user.setUsername("2");
        user.setPassword("2");
        finalSql.insert(user);
    }

    public void update() {
        User user = finalSql.selectOne(User.class, new Condition().orderByAsc("id"));
        user.setCreateTime(new Date());
        finalSql.update(user);
    }

    @GET("/login")
    public void login(FinalServerContext context){
        finalSecurityHolder.login("admin",new String[]{"user"});
        context.getResponse().returnString("login success");
    }
}
