# final-server

### 介绍
final-server是一个基于netty实现的高性能web服务器。使用 springboot 2.7.x 作为底层，兼容springboot生态。

### 特点

* 相较于 `springboot`（tomcat） 运行内存减少一半以上
* netty http2处理
* 自带基于RPC角色鉴权（默认不启用）
* 长连接 websocket
* filter功能
* web监听
* IOC、AOP
* 文件上传、文件下载
* 静态文件映射，文件断点续传
* web session管理、分布式session

### 快速入门

### 文档

官网文档：[http://final-server.1it.top](http://final-server.1it.top)

gitee文档：[https://gitee.com/lingkang_top/final-server/tree/master/docs/md](https://gitee.com/lingkang_top/final-server/tree/master/docs/md)

Maven公共仓库：[https://mvnrepository.com/artifact/top.lingkang/server](https://mvnrepository.com/artifact/top.lingkang/server)
