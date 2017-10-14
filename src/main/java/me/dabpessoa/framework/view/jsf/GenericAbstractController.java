package me.dabpessoa.framework.view.jsf;

import me.dabpessoa.framework.dao.BaseEntity;
import me.dabpessoa.framework.enums.ViewState;
import me.dabpessoa.framework.exceptions.ApplicationCheckedException;
import me.dabpessoa.framework.exceptions.ApplicationRuntimeException;
import me.dabpessoa.framework.service.AbstractSpringContextProvider;
import me.dabpessoa.framework.service.GenericAbstractService;
import me.dabpessoa.framework.service.SpringContextProvider;
import me.dabpessoa.framework.util.GenericsUtils;
import me.dabpessoa.framework.util.JsfUtil;
import me.dabpessoa.framework.util.UtilsFunctions;
import me.dabpessoa.framework.view.annotation.Crud;
import me.dabpessoa.framework.view.annotation.ViewController;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericAbstractController<Entity extends BaseEntity, Key extends Serializable, Service extends GenericAbstractService<Entity, Key, ?, ?>, P extends SpringContextProvider> extends AbstractSpringContextProvider<P> implements GenericController {

	public static final String ID_PARAM_NAME = "id";
	public static final String ACTION_PARAM_NAME = "action";

	private Logger logger;
	
	protected Entity entity;
	protected Entity searchEntity;
	protected Service service;
	
	protected Class<Entity> entityClass;
	protected Class<Key> keyClass;
	protected Class<Service> serviceClass;
	
	protected List<Entity> searchList;
	protected List<Entity> deletableList;

	protected ViewState viewState;
	
	public GenericAbstractController() {}

	/**
	 * A inicialização deve acontecer no @PostConstruct
	 * para que qualquer acesso às dependências ocorra sem erros pois estas já
	 * estarão injetadas (Inicializadas).
	 */
	@PostConstruct
	public void initController() {
		Object paramAction = JsfUtil.getRequestParam(ACTION_PARAM_NAME);
		if (paramAction != null) {
			String action = paramAction.toString();
			ViewState viewState = ViewState.findByEnumName(action);
			switch (viewState) {
				case INSERT: {
					prepareInsert();
				} break;
				case DELETE: {
					prepareDelete();
				} break;
				case VIEW: {
					prepareView();
				} break;
				case UPDATE: {
					prepareUpdate();
				} break;
				case SEARCH: case NONE: default: {
					prepareSearch();
				} break;
			}
		} else {
			prepareSearch();
		}
	}

	public void find() {
		try {
			searchList = getService().find(getSearchEntity());
			if (searchList == null || searchList.isEmpty()) {
				JsfUtil.addWarnMessage("Nenhum registro encontrado.");
			}
		} catch (ApplicationRuntimeException e) {
			JsfUtil.addErrorMessage(e.getMessage());
		} catch (Exception e) {
			if (e instanceof ApplicationCheckedException) {
				JsfUtil.addErrorMessage(e.getMessage());
			} else {
				JsfUtil.addErrorMessage("Erro inesperado ao consultar entidade. Favor contactar o suporte. Mensagem: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public String save() {
		try {
			beforeSave();
			getService().insert(getEntity());
			initEntity();
			JsfUtil.addSucessMessage("Salvo com sucesso!");
			return getFormPageLocation();
		} catch (ApplicationRuntimeException e) {
			JsfUtil.addErrorMessage(e.getMessage());
		} catch (Exception e) {
			if (e instanceof ApplicationCheckedException) {
				JsfUtil.addErrorMessage(e.getMessage());
			} else {
				JsfUtil.addErrorMessage("Erro inesperado ao inserir. Favor contactar o suporte. Mensagem: "+e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String update() {
		try {
			beforeUpdate();
			getService().update(getEntity());
			initEntity();
			JsfUtil.addSucessMessage("Atualizado com sucesso!");
			return getListPageLocation();
		} catch (ApplicationRuntimeException e) {
			JsfUtil.addErrorMessage(e.getMessage());
		} catch (Exception e) {
			if (e instanceof ApplicationCheckedException) {
				JsfUtil.addErrorMessage(e.getMessage());
			} else {
				JsfUtil.addErrorMessage("Erro inesperado ao atualizar registro. Favor contactar o suporte. Mensagem: "+e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public void beforeSave() {}

	public void beforeUpdate() {}

	public void addDeletableEntity(Entity entity) {
		deletableList.add(entity);
	}

	public void confirmDelete() {
		deleteAll(getDeletableList());
	}

	public void delete() {
		delete(getEntity());
	}

	public void delete(Entity entity) {
		if (entity != null) deleteAll(Arrays.asList(entity));
	}

	public void deleteAll(List<Entity> entities) {
		try {
			if (entities != null && !entities.isEmpty()) {
				getService().deleteAllFast(entities);
				if (getSearchList() != null) getSearchList().removeAll(entities);
			}
			setDeletableList(new ArrayList<Entity>());
			JsfUtil.addSucessMessage("Removido com sucesso!");
		} catch (ApplicationRuntimeException e) {
			JsfUtil.addErrorMessage(e.getMessage());
		} catch (Exception e) {
			if (e instanceof ApplicationCheckedException) {
				JsfUtil.addErrorMessage(e.getMessage());
			} else {
				JsfUtil.addErrorMessage("Erro inesperado ao remover registro. Favor contactar o suporte. Mensagem: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void rollbackDelete() {
		setDeletableList(new ArrayList<Entity>());
	}

	public String prepareInsert() {
		initEntity();
		this.viewState = ViewState.INSERT;
		return getFormPageLocation();
	}

	public String prepareUpdate() {
		Object paramId = JsfUtil.getRequestParam(ID_PARAM_NAME);
		return prepareUpdate(paramId);
	}

	public String prepareUpdate(Object paramId) {
		if(paramId != null){
			Serializable id = parseStringId(paramId.toString(), getKeyClass());
			entity = (Entity) getService().findByKey(((Key) id));
		} else {
			initEntity();
		}
		this.viewState = ViewState.UPDATE;
		return getFormPageLocation();
	}

	public String prepareView() {
		Object paramId = JsfUtil.getRequestParam(ID_PARAM_NAME);
		return prepareView(paramId);
	}

	public String prepareView(Object paramId) {
		if(paramId != null){
			Serializable id = parseStringId(paramId.toString(), getKeyClass());
			entity = (Entity) getService().findByKey(((Key) id));
		} else {
			initEntity();
		}
		this.viewState = ViewState.VIEW;
		return getFormPageLocation();
	}

	public String prepareSearch() {
		initEntitySearch();
		this.viewState = ViewState.SEARCH;
		deletableList = new ArrayList<Entity>();
		return getListPageLocation();
	}

	public String prepareDelete() {
		this.viewState = ViewState.DELETE;
		return null;
	}
	
	public void initEntity() {
		try {
			entity = getEntityClass().newInstance();
			entity.init();
		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().log(Level.SEVERE, "Erro ao instanciar a entidade  " + getEntityClass(), e);
		} 
	}
	
	public void initEntitySearch() {
		try {
			searchEntity = getEntityClass().newInstance();
			searchEntity.init();
		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().log(Level.SEVERE, "Erro ao instanciar a entidade  " + getEntityClass(), e);
		} 
	}
	
	public Entity getEntity() {
		if(entity == null){
			initEntity();
		}
		return entity;
	}
	
	public Entity getSearchEntity() {
		if(searchEntity == null){
			initEntitySearch();
		}
		return searchEntity;
	}
	
	public List<Entity> getSearchList() {
		if (searchList == null) {
			searchList = new ArrayList<Entity>();
		}
		return searchList;
	}
	
	public Service getService() {
		if(service == null){
			service = getSpringContextProvider().getBean(getServiceClass());
		}
		return service;
	}
	
	@SuppressWarnings("unchecked")
	protected Class<Entity> getEntityClass(){
		if(entityClass == null){
			entityClass = (Class<Entity>) GenericsUtils.discoverClass( this.getClass() , 0);
		}
		return entityClass;
	}
	
	@SuppressWarnings("unchecked")
	public Class<Key> getKeyClass() {
		if (keyClass == null) {
			keyClass = (Class<Key>) GenericsUtils.discoverClass(this.getClass(), 1);
		}
		return keyClass;
	}
	
	@SuppressWarnings("unchecked")
	public Class<Service> getServiceClass() {
		if( serviceClass == null ){
			serviceClass = (Class<Service>) GenericsUtils.discoverClass( getClass() , 2) ;
		}
		return serviceClass;
	}
	
	public String getFormPageLocation() {
		return getFormPageLocation(null);
	}

	public String getFormPageLocation(String... paramAndValues) {
		return UtilsFunctions.addURLParamsStyleToString(getCrudAnnotation().formPageLocation(), paramAndValues);
	}
	
	public String getListPageLocation() {
		return getCrudAnnotation().listPageLocation();
	}
	
	private Crud getCrudAnnotation() {
        Crud crud = this.getClass().getAnnotation(Crud.class);

        if(crud == null){
            ViewController viewController = this.getClass().getAnnotation(ViewController.class);
            if(viewController != null){
                return viewController.crud();
            }
        }

        return crud;
	}
	
	public Logger getLogger() {
		if(logger == null){
			logger = Logger.getLogger(getClass().getName());
		}
		return logger;
	}

	public boolean isInserting() {
		return ViewState.INSERT.equals(this.viewState);
	}

	public boolean isUpdating() {
		return ViewState.UPDATE.equals(this.viewState);
	}

	public boolean isSearching() {
		return ViewState.SEARCH.equals(this.viewState);
	}

	public boolean isDeleting() {
		return ViewState.DELETE.equals(this.viewState);
	}

	public static Serializable parseStringId(String value, Class<?> idClass){
		if(idClass.getTypeName().equals(Integer.class.getTypeName())){
			return Integer.valueOf(value);
		}
		if(idClass.getTypeName().equals(Long.class.getTypeName())){
			return Long.valueOf(value);
		}
		return null;
	}

	public String cancel() {
		return getListPageLocation();
	}

	public void clean() {
		initEntity();
		initEntitySearch();
		searchList = new ArrayList<>();
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public List<Entity> getDeletableList() {
		return deletableList;
	}

	public void setDeletableList(List<Entity> deletableList) {
		this.deletableList = deletableList;
	}

    public void setSearchList(List<Entity> searchList) {
        this.searchList = searchList;
    }

}