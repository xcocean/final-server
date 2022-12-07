package top.lingkang.finalserver.server.utils;

/**
 * @author lingkang
 * Created by 2022/12/7
 */
public class MatchUtils {
    public static boolean hasMatching(String path){
        return path.indexOf("{")!=-1;
    }
}
