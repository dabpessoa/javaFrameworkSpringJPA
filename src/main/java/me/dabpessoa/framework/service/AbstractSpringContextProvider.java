package me.dabpessoa.framework.service;

import me.dabpessoa.framework.util.GenericsUtils;

public abstract class AbstractSpringContextProvider<P extends SpringContextProvider> {

    protected P springContextProvider;

    public SpringContextProvider getSpringContextProvider() {
        if (springContextProvider == null) {
            Class<SpringContextProvider> springContextProviderClass = (Class<SpringContextProvider>) GenericsUtils.discoverClass(this.getClass() , 0);
            try {
                springContextProvider = (P) springContextProviderClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return springContextProvider;
    }

    public void setSpringContextProvider(P springContextProvider) {
        this.springContextProvider = springContextProvider;
    }
}
