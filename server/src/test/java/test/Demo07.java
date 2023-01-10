package test;

import cn.hutool.core.util.ObjectUtil;
import top.lingkang.finalserver.server.web.entity.RequestInfo;

/**
 * @author lingkang
 * 2023/1/10
 **/
public class Demo07 {
    public static void main(String[] args) {
        System.out.println(RequestInfo.class);
        System.out.println(ObjectUtil.isBasicType(RequestInfo.class));
    }
}
