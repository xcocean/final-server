package top.lingkang.finalserver.server.core.impl;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.util.Validate;
import top.lingkang.finalserver.server.core.FinalServerProperties;
import top.lingkang.finalserver.server.core.HttpParseTemplate;
import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author lingkang
 * Created by 2022/12/11
 * @since 1.0.0
 */
public class DefaultHttpParseTemplate implements HttpParseTemplate {
    // 开放此配置
    public static final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    private static final TemplateEngine templateEngine = new TemplateEngine();

    @Override
    public void init(String templatePath) {
        // HTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // This will convert "home" to "templates/home.html"
        templateResolver.setPrefix(FinalServerProperties.server_template_prefix);
        templateResolver.setSuffix(FinalServerProperties.server_template_suffix);
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(FinalServerProperties.server_template_cacheTime);

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(FinalServerProperties.server_template_cache);
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public byte[] getTemplate(String template, Map<String, Object> map, Map<String, Object> globalMap, FinalServerContext context) throws Exception {
        map.putAll(globalMap);
        return templateEngine.process(template, new TemplateContext(Locale.ROOT, map)).getBytes(StandardCharsets.UTF_8);
    }

    private class TemplateContext implements IContext {
        private Map<String, Object> variables;
        private Locale locale;

        public TemplateContext(final Locale locale, Map<String, Object> variables) {
            this.locale = locale;
            this.variables = variables;
        }

        public final Locale getLocale() {
            return this.locale;
        }

        public final boolean containsVariable(final String name) {
            return this.variables.containsKey(name);
        }

        public final Set<String> getVariableNames() {
            return this.variables.keySet();
        }


        public final Object getVariable(final String name) {
            return this.variables.get(name);
        }


        /**
         * <p>
         * Sets the locale to be used.
         * </p>
         *
         * @param locale the locale.
         */
        public void setLocale(final Locale locale) {
            Validate.notNull(locale, "Locale cannot be null");
            this.locale = locale;
        }


        /**
         * <p>
         * Sets a new variable into the context.
         * </p>
         *
         * @param name  the name of the variable.
         * @param value the value of the variable.
         */
        public void setVariable(final String name, final Object value) {
            this.variables.put(name, value);
        }


        /**
         * <p>
         * Sets several variables at a time into the context.
         * </p>
         *
         * @param variables the variables to be set.
         */
        public void setVariables(final Map<String, Object> variables) {
            if (variables == null) {
                return;
            }
            this.variables.putAll(variables);
        }


        /**
         * <p>
         * Removes a variable from the context.
         * </p>
         *
         * @param name the name of the variable to be removed.
         */
        public void removeVariable(final String name) {
            this.variables.remove(name);
        }


        /**
         * <p>
         * Removes all the variables from the context.
         * </p>
         */
        public void clearVariables() {
            this.variables.clear();
        }
    }
}
