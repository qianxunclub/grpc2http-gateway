package com.qianxunclub.grpchttpgateway.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationHelper implements ApplicationContextAware, EnvironmentAware {

    private static Environment env;

    private static ApplicationContext context;


    public static Object get(String bean) {
        return context.getBean(bean);
    }

    public static <T> T get(Class<T> type) {
        return context.getBean(type);
    }

    public static String env(String name) {
        return env.getProperty(name);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public static Environment getEnvironment() {
        return env;
    }

}
