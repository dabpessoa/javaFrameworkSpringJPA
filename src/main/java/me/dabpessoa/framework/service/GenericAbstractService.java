package me.dabpessoa.framework.service;

import me.dabpessoa.framework.dao.BaseEntity;
import me.dabpessoa.framework.dao.GenericAbstractDao;
import me.dabpessoa.framework.util.GenericsUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class GenericAbstractService<T extends BaseEntity, ID extends Serializable, R extends GenericAbstractDao<T, ID>, P extends SpringContextProvider> extends AbstractSpringContextProvider<P> implements GenericService<T, ID> {
	private static final long serialVersionUID = 1L;

	protected R repository;
	
	@Override
	@SuppressWarnings("unchecked")
	public R getRepository(){
		if(repository == null){
			Class<R> daoClass = (Class<R>) GenericsUtils.discoverClass(this.getClass() , 2);
			repository = getSpringContextProvider().getBean(daoClass);
		}
		return repository;
	}

	public List<T> findByHQLEntityFilter(T entity) {
		return getRepository().getDaoHelper().findByHQLEntityFilter(entity);
	}

	public List<T> findByHQLEntityFilter(T entity, String... orderByFields) {
		return getRepository().getDaoHelper().findByHQLEntityFilter(entity, orderByFields);
	}

	public List<T> findByHQLFilter(Map<String, Object> params) {
		return findByHQLFilter(params, null);
	}

	public List<T> findByHQLFilter(Map<String, Object> params, String... orderByFields) {
		Class<?> clazz = GenericsUtils.discoverClass( this.getClass() , 0);
		return getRepository().getDaoHelper().findByHQLFilter(clazz, params, orderByFields);
	}

	@Override
	@Transactional(readOnly=true)
	public List<T> findAll() {
		List<T> list = getRepository().findAll();
		return list;
	}

	@Override
	@Transactional(readOnly=true)
	public List<T> findAll(int firstItem, int maxItem) {
		return getRepository().findAll(firstItem, maxItem);
	}

	@Override
	@Transactional(readOnly=true)
	public T findByKey(ID id) {
		return getRepository().findByKey(id);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Long getRowCount() {
		return getRepository().getRowCount();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void insert(T bean) {
		getRepository().insert(bean);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void update(T bean) {
		getRepository().update(bean);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void insertAll(Collection<T> entities) {
		for (T entity : entities) {
			insert(entity);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void updateAll(Collection<T> entities) {
		for (T entity : entities) {
			update(entity);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void insertOrUpdateAll(Collection<T> entities) {
		if (entities == null) return;
		for (T entity : entities) {
			insertOrUpdate(entity);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void insertOrUpdate(T entity) {
		if (entity == null) return;
		if (entity.getId() == null) {
			insert(entity);
		} else {
			update(entity);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void delete(T entity) {
		beforeDelete(entity);
		boolean managed = getRepository().getEntityManager().contains(entity);
		if (!managed) entity = merge(entity);
		getRepository().delete(entity);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void deleteAll(List<T> entities) {
		beforeDeleteAll(entities);
		if (entities != null && !entities.isEmpty()) {
			for (T entity : entities) {
				delete(entity);
			}
		}
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public int deleteFast(T entity) {
		beforeDelete(entity);
		return getRepository().deleteFast(entity);
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void deleteAllFast(List<T> entities) {
		beforeDeleteAll(entities);
		if (entities != null && !entities.isEmpty()) {
			for (T entity : entities) {
				deleteFast(entity);
			}
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public T merge(T entity) {
		return getRepository().getEntityManager().merge(entity);
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeDelete(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeDeleteAll(List<T> entities) {}

}
