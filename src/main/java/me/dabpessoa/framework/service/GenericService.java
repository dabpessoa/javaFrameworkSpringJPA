package me.dabpessoa.framework.service;

import me.dabpessoa.framework.dao.BaseEntity;
import me.dabpessoa.framework.dao.GenericAbstractDao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface GenericService<Entity extends BaseEntity, key extends Serializable> extends Serializable {

    default List<Entity> find(Entity entity) {
        throw new UnsupportedOperationException("Método find do service não foi sobrescrito.");
    }

    List<Entity> findAll();

    List<Entity> findAll(int firstItem, int maxItem);

    void insert(Entity entity);

    void insertAll(Collection<Entity> entities);

    void update(Entity entity);

    void updateAll(Collection<Entity> entities);

    void delete(Entity entity);

    void deleteAll(List<Entity> entities);

    void insertOrUpdate(Entity entity);

    void insertOrUpdateAll(Collection<Entity> entities);

    Entity merge(Entity entity);

    Long getRowCount();

    Entity findByKey(key id);

    GenericAbstractDao<Entity, key> getRepository();

}