package top.lingkang.finalserver.server.web.http;

/**
 * @author lingkang
 * Created by 2022/12/7
 * @since 1.0.0
 */
public interface HandlerChain {
    /**
     * @return 返回 true正常处理，若返回false，将不会返回后续处理，并结束当前连接
     */
    boolean handler(FinalServerContext context) throws Exception;
}
