package top.lingkang.finalserver.server.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.InputStream;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class FinalServerLogConfig {
    private static final Logger log = LoggerFactory.getLogger(FinalServerLogConfig.class);

    public FinalServerLogConfig() {
        try {
            InputStream in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream("logback.xml");
            if (in == null)
                in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream("final-server-logback.xml");

            LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
            loggerContext.stop();
            loggerContext.reset();
            JoranConfigurator configurator = new ch.qos.logback.classic.joran.JoranConfigurator();
            configurator.setContext(loggerContext);
            configurator.doConfigure(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param level ALL TRACE DEBUG INFO WARN ERROR OFF (不区分大小写)
     */
    public void setLogLevel(String level) {
        try {
            InputStream in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream("logback.xml");
            if (in == null)
                in = FinalServerLogConfig.class.getClassLoader().getResourceAsStream("final-server-logback.xml");

            LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
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