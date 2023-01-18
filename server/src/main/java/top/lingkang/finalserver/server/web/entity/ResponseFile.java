package top.lingkang.finalserver.server.web.entity;

import java.io.File;

/**
 * @author lingkang
 * 2023/1/6
 * @since 1.0.0
 * 文件下载对象
 * <p>
 * 1、返回resources下的文件：
 * URL resource = StaticRequestHandler.class.getClassLoader().getResource("/static/hello.txt");
 * // 中文需要URL编码
 * context.getResponse().returnFile(new ResponseFile(URLDecoder.decode(resource.getPath(), "UTF-8")));
 * <p>
 * 2、返回指定目录文件
 * response.returnFile(new ResponseFile("C:\\Users\\lingkang\\Desktop\\1.xml"));
 * <p>
 * 3、返回文件并告诉浏览器要下载
 * response.returnFile(new ResponseFile("C:\\Users\\lingkang\\Desktop\\1.xml").setDownload(true));
 * <p>
 * 4、返回二进制数组时：
 * // 当数据为二进制时，先将数据转为临时文件，再写入，并设置delete
 * byte[] zipByte = new byte[]{1, 2, 3, 4};
 * File tempFile = File.createTempFile("123前缀", ".zip");// 后缀有.
 * FileUtil.writeBytes(zipByte, tempFile);
 * // 返回下载文件并响应后删除临时文件
 * response.returnFile(new ResponseFile(tempFile.getPath()).setDownload(true).setDelete(true));
 **/
public class ResponseFile {
    private String name; // 只有  isDownload = true 时有效
    private File file;
    private boolean isDelete;// 返回文件后是否删除文件，常用于临时文件
    private boolean isDownload;

    public ResponseFile() {
    }

    public ResponseFile(File file) {
        this.file = file;
    }

    public ResponseFile(String filePath) {
        file = new File(filePath);
    }

    public String getName() {
        return name;
    }

    public ResponseFile setName(String name) {
        this.name = name;
        return this;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ResponseFile setFilePath(String filePath) {
        this.file = new File(filePath);
        return this;
    }

    public ResponseFile setFilePath(File file) {
        this.file = file;
        return this;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public ResponseFile setDelete(boolean delete) {
        isDelete = delete;
        return this;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public ResponseFile setDownload(boolean download) {
        isDownload = download;
        return this;
    }

    @Override
    public String toString() {
        return "ResponseFile{" +
                "name='" + name + '\'' +
                ", file=" + file +
                ", isDelete=" + isDelete +
                ", isDownload=" + isDownload +
                '}';
    }
}
