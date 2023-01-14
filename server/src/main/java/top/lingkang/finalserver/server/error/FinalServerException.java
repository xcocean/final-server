package top.lingkang.finalserver.server.error;

/**
 * @author lingkang
 * 2023/1/5
 **/
public class FinalServerException extends RuntimeException{
    public FinalServerException(String message) {
        super(message);
    }

    public FinalServerException(Throwable cause) {
        super(cause);
    }

    public FinalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
