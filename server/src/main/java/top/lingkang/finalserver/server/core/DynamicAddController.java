package top.lingkang.finalserver.server.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import top.lingkang.finalserver.server.FinalServerApplication;
import top.lingkang.finalserver.server.error.FinalServerException;
import top.lingkang.finalserver.server.utils.BeanUtils;
import top.lingkang.finalserver.server.web.handler.ControllerRequestHandler;
import top.lingkang.finalserver.server.web.http.RequestMethod;

import java.util.Date;

/**
 * @author lingkang
 * Created by 2023/1/19
 */
public class DynamicAddController {
    private static final Logger log = LoggerFactory.getLogger(DynamicAddController.class);

    public static void addController(String path, RequestMethod method,CustomRequestHandler handler) throws Exception {


        ControllerRequestHandler controllerRequestHandler = FinalServerApplication.applicationContext.getBean(ControllerRequestHandler.class);
        controllerRequestHandler.build();// 重新build请求处理

        log.info("动态注册controller成功");
    }
}
