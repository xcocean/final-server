# final-server

### 介绍
final-server是一个基于netty实现的高性能web服务器。使用spring作为底层，兼容spring生态。

### 快速入门

引入依赖
```
<dependency>
    <groupId>top.lingkang</groupId>
    <artifactId>server</artifactId>
    <version>x.y.z</version>
</dependency>
```

快速启动
```java
@Controller
@FinalServerBoot
public class WebApp {
    public static void main(String[] args) {
        FinalServerApplication.run(WebApp.class, args);
    }
}
```
`以spring 5.x 为底层，能够使用spring 5.x 的能力。`

## web

```java
@Controller// 注解当前类为controller
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
* 默认使用 `thymeleaf` 作为html模板渲染<br>

> 默认的静态资源目录为 `resources/static`，其中`mp4中文.mp4`位于`resources/static/mp4中文.mp4`<br>

> 模板目录为`resources/templates`，html文件为：`resources/templates/index.html`
```html
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>hello</title>
</head>
<body>
hi，[[${vv}]]
<hr>
session:[[${session}]]
<hr>
<video width="320" height="240" controls src="/mp4中文.mp4"></video>
<hr>
[[${request.getHeaders()}]]
</body>
</html>
```

## 获取当前请求上下文

```html
FinalServerContext.currentContext()
```

## 特点

* 相较于 `springboot` 运行内存减半
* http处理
* 自带基于RPC角色鉴权
* websocket
* filter功能
* web监听
* IOC、AOP
* 文件上传、文件下载
* 静态文件映射，文件断点续传
* web session管理


## 局限性

> 新的web生态也有局限性

* 未实现JTA，无法把事务交由`spring`管理，即无法使用`spring-tx`的功能，事务需要自己手动管理。