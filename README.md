# final-server

### 介绍
final-server是一个基于netty实现的高性能web服务器。使用spring 5.x作为底层，兼容spring生态。


### 特点

* 相较于 `springboot` 运行内存减少一半以上
* netty http2处理
* 自带基于RPC角色鉴权（默认不启用）
* 长连接 websocket
* filter功能
* web监听
* IOC、AOP
* 文件上传、文件下载
* 静态文件映射，文件断点续传
* web session管理、分布式session

### 文档

官网文档：[http://final-server.1it.top](http://final-server.1it.top)

gitee文档：[https://gitee.com/lingkang_top/final-server/tree/master/docs/md](https://gitee.com/lingkang_top/final-server/tree/master/docs/md)

Maven公共仓库：[https://mvnrepository.com/artifact/top.lingkang/server](https://mvnrepository.com/artifact/top.lingkang/server)

### 入门
```xml
<dependency>
    <groupId>top.lingkang</groupId>
    <artifactId>server</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class, args);
    }

    @GET // 默认 url=/
    public void index(FinalServerContext context) {
        context.getResponse().returnString("hello, final server");
    }

    @GET("/t")
    public void template(FinalServerContext context) throws Exception {
        // 返回文件到前端
        context.getResponse().returnFile(new ResponseFile("C:\\Users\\Administrator\\Desktop\\temp-凡人修仙传.mp4"));
        // 告诉前端，下载文件
        // context.getResponse().setHeader("Content-type", "application/octet-stream");
        // 附件下载 attachment 附件 inline 在线打开(默认值)
        // context.getResponse().setHeader("Content-Disposition", "attachment;fileName="+URLEncoder.encode("temp-凡人修仙传.mp4", "UTF-8"));
    }

    @GET("s")
    public Object s(){
        return "hello, final server， 直接返回string";
    }

    @GET("e")
    public Object e(Integer id){
        // http://localhost:7070/e?id=123
        return id;
    }

    @GET("/index")
    public void index(FinalServerContext context){
        // 添加值到session中
        context.getRequest().getSession().setAttribute("sessionValue","这是session值");

        HashMap<String, Object> map = new HashMap<>();
        map.put("vv","直接输出模板渲染的变量vv");// 直接输出模板渲染的变量
        context.getResponse().returnTemplate("index",map);
        // 返回 resources/templates/index.html 渲染模板
        // context.getResponse().returnTemplate("index");
    }
}
```


