package me.dabpessoa.framework.service;

import org.springframework.context.ApplicationContext;

public interface SpringContextProvider {

    enum SpringContextLoadType {ANNOTATION, XML}

    ApplicationContext getContext(SpringContextLoadType springContextLoadType, String... activeProfiles);
    String[] getActiveProfiles();

    default <T> T getBean(String name) {
        // ANNOTATION DEFAULT
        return (T) getConfigurationAnnotationBean(name);
    }

    default <T> T getBean(String name, String... activeProfiles) {
        // ANNOTATION DEFAULT
        return getConfigurationAnnotationBean(name, activeProfiles);
    }

    default <T> T getBeanWithConstructorArgs(String name, String... constructorArgs) {
        // ANNOTATION DEFAULT
        return (T) getContext(SpringContextLoadType.ANNOTATION, getActiveProfiles()).getBean(name, constructorArgs);
    }

    default <T> T getBeanWithConstructorArgs(String name, String[] constructorArgs, String... activeProfiles) {
        // ANNOTATION DEFAULT
        return (T) getContext(SpringContextLoadType.ANNOTATION, activeProfiles).getBean(name, constructorArgs);
    }

    default <T> T getBean(Class<?> clazz) {
        // ANNOTATION DEFAULT
        return (T) getContext(SpringContextLoadType.ANNOTATION, getActiveProfiles()).getBean(clazz);
    }

    default <T> T getBean(Class<?> clazz, String... activeProfiles) {
        // ANNOTATION DEFAULT
        return getConfigurationAnnotationBean(clazz, activeProfiles);
    }

    default <T> T getXMLBean(String name) {
        return (T) getContext(SpringContextLoadType.XML, getActiveProfiles()).getBean(name);
    }

    default <T> T getXMLBean(Class<?> clazz) {
        return (T) getContext(SpringContextLoadType.XML, getActiveProfiles()).getBean(clazz);
    }

    default <T> T getConfigurationAnnotationBean(String name) {
        return (T) getContext(SpringContextLoadType.ANNOTATION, getActiveProfiles()).getBean(name);
    }

    default <T> T getConfigurationAnnotationBean(Class<?> clazz) {
        return (T) getContext(SpringContextLoadType.ANNOTATION, getActiveProfiles()).getBean(clazz);
    }

    default <T> T getConfigurationAnnotationBean(String name, String... activeProfiles) {
        return (T) getContext(SpringContextLoadType.ANNOTATION, activeProfiles).getBean(name);
    }

    default <T> T getConfigurationAnnotationBean(Class<?> clazz, String... activeProfiles) {
        return (T) getContext(SpringContextLoadType.ANNOTATION, activeProfiles).getBean(clazz);
    }

}
