package top.lingkang.finalserver.example.test14;

import top.lingkang.finalserver.server.annotation.*;

/**
 * @author lingkang
 * Created by 2023/4/1
 */
@Controller
public class TestController {
    @GET
    public void index() {
    }

    @POST("a/a")
    public void index1() {
    }

    @GET("a/a")
    public void index2() {
    }

    @PUT("/a/a")
    public void index3() {
    }

    @GET("/a/{a}")
    public void index4() {
    }

    @POST("a/{a}")
    public void index5() {
    }

    @DELETE("a/{a}")
    public void index6() {
    }

}
