package top.lingkang.finalserver.server.error;

/**
 * @author lingkang
 * 2023/1/5
 * 请求的内容太大异常，一般为上传文件超出 server.maxContentLength 的设置大小
 **/
public class ContentTooLargeException extends FinalServerException{
    public ContentTooLargeException(String message) {
        super(message);
    }
}
