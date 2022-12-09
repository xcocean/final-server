package top.lingkang.finalserver.server.log;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class FinalSystemOut {
    public static void error(String err) {
        System.err.println(err);
        showStackTrace();
    }

    private static void showStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            if (stackTrace[i].getClassName().startsWith("top.lingkang.finalserver.server"))
                continue;
            System.out.println(stackTrace[i]);
            break;
        }
    }
}
