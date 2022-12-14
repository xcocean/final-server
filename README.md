# final-server

### 介绍
final-server是一个基于netty实现的高性能web服务器

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
`以spring 5.x 为底层，能够使用spring 5.x 的注解。`

## web

```java

```