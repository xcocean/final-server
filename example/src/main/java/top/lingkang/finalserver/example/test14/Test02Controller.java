package top.lingkang.finalserver.example.test14;

import top.lingkang.finalserver.server.annotation.Controller;
import top.lingkang.finalserver.server.annotation.GET;
import top.lingkang.finalserver.server.annotation.RequestMapping;

/**
 * @author lingkang
 * Created by 2023/4/1
 */
@RequestMapping("/t")
@Controller
public class Test02Controller {
    @GET("page/aa")
    public void index(){
    }
}
