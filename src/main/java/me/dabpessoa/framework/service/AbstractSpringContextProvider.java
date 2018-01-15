package me.dabpessoa.framework.service;

import me.dabpessoa.framework.util.GenericsUtils;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public abstract class AbstractSpringContextProvider<P extends SpringContextProvider> {

    protected P springContextProvider;

    public SpringContextProvider getSpringContextProvider() {
        if (this.springContextProvider == null) {
            Class springContextProviderClass = GenericsUtils.discoverClass(this.getClass(), 3);
            if (springContextProviderClass.isInterface()) {

                springContextProviderClass = null;
                String className = null;

                // Encontrar classe através de parâmetro de contexto.
                if (FacesContext.getCurrentInstance() != null) {
                    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
                    className = servletContext.getInitParameter("springContextProviderClass");
                }

                if (className == null) {
                    className = System.getProperty("springContextProviderClass");
                }

                if (className != null) {
                    try {
                        springContextProviderClass = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Não foi possível carregar a classe: "+className+". (Classe configurada como parâmetro de contexto da aplicação web).");
                    }
                }

                if (springContextProviderClass == null) {
                    throw new RuntimeException("ERRO => Nenhum provedor de contexto do spring configurado para a aplicação. Favor configurar uma variável com o nome 'springContextProviderClass' nas propriedades do sistema ou no contexto inicial da aplicação web.");
                }

            }

            try {
                this.springContextProvider = (P)springContextProviderClass.newInstance();
            } catch (InstantiationException var3) {
                var3.printStackTrace();
            } catch (IllegalAccessException var4) {
                var4.printStackTrace();
            }
        }

        return this.springContextProvider;
    }

    public void setSpringContextProvider(P springContextProvider) {
        this.springContextProvider = springContextProvider;
    }

}
