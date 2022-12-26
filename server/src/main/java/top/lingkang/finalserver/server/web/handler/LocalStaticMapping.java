package top.lingkang.finalserver.server.web.handler;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/9
 * @since 1.0.0
 * 可以继承实现静态文件处理
 * @Override
 * public void addStaticByAbsolutePath(List<String> paths) {
 * paths.add("d:/");
 * paths.add("C:\\Users\\Administrator\\Desktop\\picture");
 * }
 */
public class LocalStaticMapping {

    /**
     * 添加本地静态文件映射，应该添加绝对路径，若存在同名文件，按添加的先后顺序处理
     *
     * @param paths 格式例如 /user/lk/data 、c:/data、d:/ 、C:\\Users\\Administrator\\Desktop\\picture
     */
    public void addStaticByAbsolutePath(List<String> paths) {

    }

    private List<String> paths = new ArrayList<>();

    @PostConstruct
    public void init() {
        addStaticByAbsolutePath(paths);
    }

    public List<String> getPaths() {
        return paths;
    }
}
