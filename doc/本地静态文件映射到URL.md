# 本地静态文件映射到URL
```java
@Component
public class MyLocalStaticMapping extends LocalStaticMapping {

    @Override
    public void addStaticByAbsolutePath(List<String> paths) {
        paths.add("d:/");
        paths.add("C:\\Users\\Administrator\\Desktop\\picture");
    }
}
```
访问URL：http://localhost:7070/20220111093335.jpg
其中，`20220111093335.jpg` 文件位于 `C:\Users\Administrator\Desktop\picture` 目录下