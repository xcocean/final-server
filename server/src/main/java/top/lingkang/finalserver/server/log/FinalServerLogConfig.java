package top.lingkang.finalserver.server.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import cn.hutool.core.util.StrUtil;
import org.slf4j.LoggerFactory;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.utils.CommonUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author lingkang
 * Created by 2022/12/6
 * @since 1.0.0
 */
public class FinalServerLogConfig {

    public FinalServerLogConfig() {
        init(null);
    }

    /**
     * @param path 文件的绝对路径
     */
    public static void init(String path) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            InputStream in = null;
            if (StrUtil.isNotBlank(FinalServerProperties.log_file)) {
                File file = new File(FinalServerProperties.log_file);
                if (file.exists()) {
                    in = Files.newInputStream(file.toPath());
                } else {
                    if (FinalServerProperties.log_file.startsWith("/"))
                        file = new File(CommonUtils.getDir() + FinalServerProperties.log_file);
                    else
                        file = new File(CommonUtils.getDir() + File.separator + FinalServerProperties.log_file);
                    if (file.exists())
                        in = Files.newInputStream(file.toPath());
                    else
                        in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream(FinalServerProperties.log_file);
                }
            }

            if (StrUtil.isNotBlank(path))
                in = Files.newInputStream(new File(path).toPath());

            if (in == null)
                in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream("final-server-logback.xml");
            loggerContext.stop();
            loggerContext.reset();
            JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(in);
            in.close();
            if ("true".equals(FinalServerProperties.debug)) {
                setLogLevel("DEBUG");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param level ALL TRACE DEBUG INFO WARN ERROR OFF (不区分大小写)
     */
    public static void setLogLevel(String level) {
        try {
            InputStream in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream(FinalServerProperties.log_file);
            if (in == null)
                in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream("final-server-logback.xml");

            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.stop();
            loggerContext.reset();
            JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(in);
            in.close();
            ch.qos.logback.classic.Logger root = loggerContext.getLogger("ROOT");
            if (root != null) {
                root.setLevel(Level.toLevel(level));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
