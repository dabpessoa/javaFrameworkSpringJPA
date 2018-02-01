package me.dabpessoa.framework.util;

import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class JsfUtil {

	private static final String NO_RESOURCE_FOUND = "Missing resource: ";

	public static Object resolveExpression(String expression) {
		FacesContext facesContext = getFacesContext();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
		return valueExp.getValue(elContext);
	}

	public static ELContext getELContext() {
		FacesContext facesContext = getFacesContext();
		ELContext elContext = facesContext.getELContext();
		return elContext;
	}

	public static String resolveRemoteUser() {
		FacesContext facesContext = getFacesContext();
		ExternalContext ectx = facesContext.getExternalContext();
		return ectx.getRemoteUser();
	}

	public static String resolveUserPrincipal() {
		FacesContext facesContext = getFacesContext();
		ExternalContext ectx = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
		return request.getUserPrincipal().getName();
	}

	public static Object resloveMethodExpression(String expression, @SuppressWarnings("rawtypes") Class returnType, @SuppressWarnings("rawtypes") Class[] argTypes,
			Object[] argValues) {
		FacesContext facesContext = getFacesContext();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		MethodExpression methodExpression = elFactory.createMethodExpression(elContext, expression, returnType,
				argTypes);
		return methodExpression.invoke(elContext, argValues);
	}

	public static Boolean resolveExpressionAsBoolean(String expression) {
		return (Boolean) resolveExpression(expression);
	}

	public static String resolveExpressionAsString(String expression) {
		return (String) resolveExpression(expression);
	}

	public static Object getManagedBeanValue(String beanName) {
		StringBuffer buff = new StringBuffer("#{");
		buff.append(beanName);
		buff.append("}");
		return resolveExpression(buff.toString());
	}

	public static void setExpressionValue(String expression, Object newValue) {
		FacesContext facesContext = getFacesContext();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
		@SuppressWarnings("rawtypes")
		Class bindClass = valueExp.getType(elContext);
		if (bindClass.isPrimitive() || bindClass.isInstance(newValue)) {
			valueExp.setValue(elContext, newValue);
		}
	}

	public static void setManagedBeanValue(String beanName, Object newValue) {
		StringBuffer buff = new StringBuffer("#{");
		buff.append(beanName);
		buff.append("}");
		setExpressionValue(buff.toString(), newValue);
	}

	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	public static HttpSession getSession(boolean b) {
		return getRequest().getSession(b);
	}

	public static String getFromHeader(String key) {
		FacesContext ctx = getFacesContext();
		ExternalContext ectx = ctx.getExternalContext();
		return ectx.getRequestHeaderMap().get(key);
	}

	public static String getRequestParam(String paramName) {
		return getRequest().getParameter(paramName);
	}

	public static Object getRequestAttribute(String name) {
		return getRequest().getAttribute(name);
	}

	public static void putRequestAttribute(String key, Object object) {
		getRequest().setAttribute(key, object);
	}

	public static void addRequestParam(String key, Object object) {
		FacesContext.getCurrentInstance()
				.getExternalContext()
				.getRequestMap()
				.put(key, object);
	}

	public static Object getSessionAttribute(String name) {
		return getSession(true).getAttribute(name);
	}

	public static void putSessionAttribute(String key, Object object) {
		getSession(true).setAttribute(key, object);
	}

	public static String getStringFromBundle(String key) {
		ResourceBundle bundle = getBundle();
		return getStringSafely(bundle, key, null);
	}

	public static FacesMessage getMessageFromBundle(String key, FacesMessage.Severity severity) {
		ResourceBundle bundle = getBundle();
		String summary = getStringSafely(bundle, key, null);
		String detail = getStringSafely(bundle, key + "_detail", summary);
		FacesMessage message = new FacesMessage(summary, detail);
		message.setSeverity(severity);
		return message;
	}

	public static void addSucessMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_INFO, msg);
	}

	public static void addErrorMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_ERROR, msg);
	}

	public static void addWarnMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_WARN, msg);
	}

	private static void addMessage(FacesMessage.Severity severity, String msg) {
		FacesContext ctx = getFacesContext();
		FacesMessage fm = new FacesMessage(severity, msg, msg);
		ctx.addMessage(null, fm);
	}

	public static String getRootViewId() {
		return getFacesContext().getViewRoot().getViewId();
	}

	public static String getRootViewComponentId() {
		return getFacesContext().getViewRoot().getId();
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	private static ResourceBundle getBundle() {
		FacesContext ctx = getFacesContext();
		UIViewRoot uiRoot = ctx.getViewRoot();
		Locale locale = uiRoot.getLocale();
		ClassLoader ldr = Thread.currentThread().getContextClassLoader();
		return ResourceBundle.getBundle(ctx.getApplication().getMessageBundle(), locale, ldr);
	}
	
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) getFacesContext().getExternalContext().getRequest();
	}
	
	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) getFacesContext().getExternalContext().getResponse();
	}

	public static void handleNavigation(String outcome){
		FacesContext facesContext = getFacesContext();
		if(facesContext != null){
			facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);
		}
	}
	
	public static void dispatcher(String uri) throws ServletException, IOException {
		RequestDispatcher dispatcher = getRequest().getRequestDispatcher(uri);
		dispatcher.forward((ServletRequest)getRequest(), (ServletResponse)getResponse());
	}

	public static void setRequestAttribute(String name, Object value) {
		getFacesContext().getExternalContext().getRequestMap().put(name, value);
	}

	private static String getStringSafely(ResourceBundle bundle, String key, String defaultValue) {
		String resource = null;
		try {
			resource = bundle.getString(key);
		} catch (MissingResourceException mrex) {
			if (defaultValue != null) {
				resource = defaultValue;
			} else {
				resource = NO_RESOURCE_FOUND + key;
			}
		}
		return resource;
	}

	public static UIComponent findComponentInRoot(String id) {
		UIComponent component = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			UIComponent root = facesContext.getViewRoot();
			component = findComponent(root, id);
		}
		return component;
	}

	public static UIComponent findComponent(UIComponent base, String id) {
		if (id.equals(base.getId()))
			return base;

		UIComponent children = null;
		UIComponent result = null;
		@SuppressWarnings("rawtypes")
		Iterator childrens = base.getFacetsAndChildren();
		while (childrens.hasNext() && (result == null)) {
			children = (UIComponent) childrens.next();
			if (id.equals(children.getId())) {
				result = children;
				break;
			}
			result = findComponent(children, id);
			if (result != null) {
				break;
			}
		}
		return result;
	}
	
	public static void throwMessagesToFacesContext(Collection<FacesMessage> messages){
		if(messages == null){
			return;
		}
		
		for (FacesMessage facesMessage : messages) {
			getFacesContext().addMessage(getRootViewId(), facesMessage);
		}
	}
	
	public static void cleanSubmittedValues(UIComponent component) {
		
		if (component instanceof EditableValueHolder) {
			EditableValueHolder evh = (EditableValueHolder) component;
			evh.setSubmittedValue(null);
			evh.setValue(null);
			evh.setLocalValueSet(false);
			evh.setValid(true);
		}
		
		if(component.getChildCount() > 0){
			for (UIComponent child : component.getChildren()) {
				cleanSubmittedValues(child);
			}
		}
	}
	
	public static UIComponent findComponent(String clientId){
		return FacesContext.getCurrentInstance().getViewRoot().findComponent(clientId);
	}
	
	public static void cleanSubmittedValues(String clientId){
		UIComponent component = findComponent(clientId);
		
		if(component == null){
			throw new FacesException("Componente não encontrado: "+clientId);
		}
		
		cleanSubmittedValues(component);
	}
	
	public static ApplicationContext getWebApplicationContext(){
		return  FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
	}

	public static ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}

	public static void redirect(String toPage) {
		try {
			getExternalContext().redirect(toPage);
		} catch (IOException e) {
			addErrorMessage("Erro ao redirecionar. ERRO: "+e.getMessage());
			e.printStackTrace();
		}
	}

	public static void redirectWithParams(String toPage, Map<String,String> params) {
		try {

			// Adicionar parâmetros na URL.
			if (params != null && !params.isEmpty()) {
				toPage += "?";
				for (String key : params.keySet()) {
					toPage += key +"="+params.get(key);
				}
			}

			redirect(toPage);

		} catch (Exception e) {
			addErrorMessage("Erro ao redirecionar. ERRO: "+e.getMessage());
			e.printStackTrace();
		}
	}

	public static void forward(String toPage) {
		try {
			getExternalContext().dispatch(toPage);
		} catch (IOException e) {
			addErrorMessage("Erro ao redirecionar (FORWARD). ERRO: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String getBaseURL() {
		return getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath();
	}

    public static void throwMessages(ValidatorException e){
	    if(e == null){
	        return;
        }

        Collection<FacesMessage> facesMessages = e.getFacesMessages();

        if(facesMessages != null && !facesMessages.isEmpty()){
            facesMessages.forEach( m -> addMessage(m.getSeverity(), m.getDetail() == null ? m.getSummary() : m.getDetail()));
        }
    }
}