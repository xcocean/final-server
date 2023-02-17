package top.lingkang.finalserver.server.web.handler;

import cn.hutool.core.lang.Assert;
import top.lingkang.finalserver.server.web.entity.ResponseFile;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lingkang
 * Created by 2022/12/9
 * @Override public void addStaticByAbsolutePath(List<String> paths) {
 * paths.add("d:/");
 * paths.add("C:\\Users\\Administrator\\Desktop\\picture");
 * }
 * @since 1.0.0
 * 可以继承实现静态文件处理，优先级高于 resources/static 中的静态文件
 */
public class LocalStaticMapping implements RequestHandler {

    /**
     * 添加本地静态文件映射，应该添加绝对路径，若存在同名文件，按添加的先后顺序处理
     *
     * @param paths 格式例如 /user/lk/data 、c:/data、d:/ 、C:\\Users\\Administrator\\Desktop\\picture
     */
    public void addStaticByAbsolutePath(List<String> paths) {
    }

    private List<String> paths = new ArrayList<>();
    private String[] basePaths = new String[0];

    public void init() {
        addStaticByAbsolutePath(paths);
        List<String> list = new ArrayList<>();
        for (String basePath : paths) {
            Assert.notBlank(basePath, "静态资源映射路径不能为空！");
            if (basePath.endsWith("/"))
                basePath = basePath.substring(0, basePath.length() - 1);
            if (basePath.endsWith("//"))
                basePath = basePath.substring(0, basePath.length() - 2);
            if (basePath.endsWith("\\\\"))
                basePath = basePath.substring(0, basePath.length() - 2);
            if (basePath.endsWith("\\"))
                basePath = basePath.substring(0, basePath.length() - 1);
            list.add(basePath);
        }
        basePaths = list.toArray(new String[0]);
    }

    public List<String> getPaths() {
        return paths;
    }

    @Override
    public boolean handler(FinalServerContext context) throws Exception {
        String path = context.getRequest().getPath();
        if (path.contains(".")) {
            for (String basePath : basePaths) {
                File file = new File(basePath + path);
                if (file.exists()) {
                    context.getResponse().returnFile(new ResponseFile(file));
                    return true;
                }
            }
        }
        return false;
    }
}
