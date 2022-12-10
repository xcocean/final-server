package top.lingkang.finalserver.server.web;

import top.lingkang.finalserver.server.web.http.Request;
import top.lingkang.finalserver.server.web.http.Response;

/**
 * @author lingkang
 * Created by 2022/12/10
 */
public class FinalServerHttpContext {
    private static final ThreadLocal<Request> req=new ThreadLocal<>();
    private static final ThreadLocal<Response> res=new ThreadLocal<>();

    public static Request getRequest(){
        return req.get();
    }

    public static Response getResponse(){
        return res.get();
    }

    public static void init(Request request,Response response){
        req.set(request);
        res.set(response);
    }
}
