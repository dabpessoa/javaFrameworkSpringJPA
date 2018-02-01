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
<<<<<<< HEAD
	public void insert(T entity) {
		validateInsert(entity);
		validateInsertAndUpdate(entity);

		beforeInsert(entity);
		beforeInsertAndUpdate(entity);

		getRepository().insert(entity);

		afterInsert(entity);
		afterInsertAndUpdate(entity);
=======
	public void insert(T bean) {
		getRepository().insert(bean);
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
<<<<<<< HEAD
	public void update(T entity) {
		validateUpdate(entity);
		validateInsertAndUpdate(entity);

		beforeUpdate(entity);
		beforeInsertAndUpdate(entity);

		getRepository().update(entity);

		afterUpdate(entity);
		afterInsertAndUpdate(entity);
=======
	public void update(T bean) {
		getRepository().update(bean);
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void insertAll(Collection<T> entities) {
<<<<<<< HEAD
		beforeInsertAll(entities);
		for (T entity : entities) {
			insert(entity);
		}
		afterInsertAll(entities);
=======
		for (T entity : entities) {
			insert(entity);
		}
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void updateAll(Collection<T> entities) {
<<<<<<< HEAD
		beforeUpdateAll(entities);
		for (T entity : entities) {
			update(entity);
		}
		afterUpdateAll(entities);
=======
		for (T entity : entities) {
			update(entity);
		}
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void insertOrUpdateAll(Collection<T> entities) {
<<<<<<< HEAD
		beforeInsertOrUpdateAll(entities);
=======
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
		if (entities == null) return;
		for (T entity : entities) {
			insertOrUpdate(entity);
		}
<<<<<<< HEAD
		afterInsertOrUpdateAll(entities);
=======
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void insertOrUpdate(T entity) {
<<<<<<< HEAD
		beforeInsertOrUpdate(entity);
		if (entity == null) throw new NullPointerException("Entidade nula. Class: "+GenericsUtils.discoverClass( this.getClass() , 0));

		if (entity.isInserting()) insert(entity);
		else update(entity);

		afterInsertOrUpdate(entity);
=======
		if (entity == null) return;
		if (entity.getId() == null) {
			insert(entity);
		} else {
			update(entity);
		}
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void delete(T entity) {
		beforeDelete(entity);
		boolean managed = getRepository().getEntityManager().contains(entity);
		if (!managed) entity = merge(entity);
		getRepository().delete(entity);
<<<<<<< HEAD
		afterDelete(entity);
=======
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
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
<<<<<<< HEAD
		afterDeleteAll(entities);
=======
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public int deleteFast(T entity) {
		beforeDelete(entity);
<<<<<<< HEAD
		int deleteReturn = getRepository().deleteFast(entity);
		afterDelete(entity);
		return deleteReturn;
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void deleteAllFast(Collection<T> entities) {
=======
		return getRepository().deleteFast(entity);
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void deleteAllFast(List<T> entities) {
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
		beforeDeleteAll(entities);
		if (entities != null && !entities.isEmpty()) {
			for (T entity : entities) {
				deleteFast(entity);
			}
		}
<<<<<<< HEAD
		afterDeleteAll(entities);
=======
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public T merge(T entity) {
		return getRepository().getEntityManager().merge(entity);
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeDelete(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
<<<<<<< HEAD
	public void afterDelete(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeDeleteAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterDeleteAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeInsert(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterInsert(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeInsertAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterInsertAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeUpdate(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterUpdate(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeUpdateAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterUpdateAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeInsertOrUpdate(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterInsertOrUpdate(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeInsertOrUpdateAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterInsertOrUpdateAll(Collection<T> entities) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void validateInsert(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void validateUpdate(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void validateInsertAndUpdate(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void beforeInsertAndUpdate(T entity) {}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Throwable.class)
	public void afterInsertAndUpdate(T entity) {}
=======
	public void beforeDeleteAll(List<T> entities) {}
>>>>>>> 3af74038f154f5fba8e64565706a1718f3dbda61

}
