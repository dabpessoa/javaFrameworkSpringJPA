package me.dabpessoa.framework.dao;

import me.dabpessoa.framework.util.Primitive;
import me.dabpessoa.framework.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JPADaoHelper {

	@PersistenceContext
	private EntityManager entityManager;

	public <T> List<T> queryHQLList(String query) {
		return queryAny(query, null, null, true, true);
	}

    public <T> List<T> queryHQLList(String query, Map<String,Object> params, Integer first, Integer max) {
        return queryAny(query, params, null, true, true, first, max);
    }

	public <T> List<T> queryHQLList(String query, Map<String,Object> params) {
		return queryAny(query, params, null, true, true);
	}
	
	public <T> T queryHQLSingleResult(String query, Map<String,Object> params) {
		return queryAny(query, params, null, true, false);
	}	
	
	@SuppressWarnings("unchecked")
	public <T> List<T> querySQLList(String sql, Map<String,Object> params, Class<?> entityClass, String... fieldsNames ) {
		List<Object[]> resultsList = querySQLList(sql, params);
		try {
			return (List<T>) convertObjectsByFieldsName(resultsList, entityClass, fieldsNames);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Erro ao tentar consultar entidade via native SQL.");
	}

	@SuppressWarnings("unchecked")
	public <T> T querySQLSingleResult(String query, Map<String,Object> params, Class<?> entityClass, String... fieldsNames ) {
		Object[] resultObjectArray = querySQLSingleResult(query, params);
		try {
			return (T) convertObjectsByFieldsName(resultObjectArray, entityClass, fieldsNames);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Erro ao tentar consultar entidade via native SQL.");
	}

	public <T> List<T> querySQLList(String query, Map<String,Object> params) {
		return queryAny(query, params, null, false, true);
	}

	public <T> List<T> querySQLList(String query, Map<String, Object> params, Class<?> entityClass) {
		return queryAny(query, params, entityClass, false, true);
	}

	public <T> T querySQLSingleResult(String query, Map<String,Object> params) {
		return queryAny(query, params, null, false, false);
	}

	public <T> T querySQLSingleResult(String query, Map<String, Object> params, Class<?> entityClass) {
		return queryAny(query, params, entityClass, false, false);
	}

	public int executeJPQL(String query, Map<String, Object> params) {
		return execute(query, params, null, true);
	}

	public int executeSQL(String query, Map<String, Object> params) {
		return execute(query, params, null, false);
	}

	public int executeSQL(String query, Map<String, Object> params, Class<?> entityClass) {
		return execute(query, params, entityClass, false);
	}
	
	private int execute(String query, Map<String, Object> params, Class<?> entityClass, boolean isJPQL) {

		Query q = createQuery(query, entityClass, isJPQL);
		preencherParametros(q, params);

		// execute query
		return q.executeUpdate();

	}
	
	@SuppressWarnings("unchecked")
	private <T> T queryAny(String query, Map<String, Object> replacements, Class<?> entityClass, boolean isJPQL, boolean isResultList) {

//		basicSQLParamsWrap(replacements);

		return queryAny(query, replacements, entityClass, isJPQL, isResultList, null, null);

	}

    private <T> T queryAny(String query, Map<String, Object> replacements, Class<?> entityClass, boolean isJPQL, boolean isResultList, Integer first, Integer max) {

        Query q = createQuery(query, entityClass, isJPQL);
        preencherParametros(q, replacements);

        if(first != null)
            q.setFirstResult(first);

        if(max != null)
            q.setMaxResults(max);

        // execute query
        try {
            if (isResultList) {
                return (T) q.getResultList();
            } else {
                return (T) q.getSingleResult();
            }
        } catch (NoResultException e) {
            return null;
        }

    }


    public Query createQuery(String query, Class<?> entityClass, boolean isJPQL) {
		Query q;

		// create query
		if (isJPQL) {
			q = entityManager.createQuery(query);
		} else {
			if (entityClass != null) q = entityManager.createNativeQuery(query, entityClass);
			else q = entityManager.createNativeQuery(query);
		}

		return q;
	}

	public void preencherParametros(Query query, Map<String, Object> params) {
		if (params != null) {
			for (String paramKey : params.keySet()) {
				query.setParameter(paramKey, params.get(paramKey));
			}
		}
	}

	public void basicSQLParamsWrap(Map<String, Object> params) {
		if (params != null) {
			for (String key : params.keySet()) {
				Object value = sqlValueWrap(params.get(key));
				if (value != null) params.put(key, value);
			}
		}
	}

	public String sqlValueWrap(Object value) {
		return SQLObjectWrapper.wrap(value);
	}

	public <T> List<T> convertObjectsByFieldsName(List<Object[]> resultsList, Class<T> clazz, String... fieldsNames) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		List<T> list = new ArrayList<T>();
		for (Object[] results : resultsList) {
			list.add(convertObjectsByFieldsName(results, clazz, fieldsNames));
		}
		return list;
	}

	public <T> T convertObjectsByFieldsName(Object[] resultObjectArray, Class<T> clazz, String... fieldsNames) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		T entity = clazz.newInstance();
		for (int i = 0 ; i < fieldsNames.length ; i++) {
			Field field = ReflectionUtils.findFieldByName(clazz, fieldsNames[i]);

			Object transformedFieldValue = basicFieldTypeTransform(resultObjectArray[i], field);

			ReflectionUtils.setFieldValue(entity, field, transformedFieldValue);
		}
		return entity;
	}

	public <T> Map<String, Object> createMapParams(T entity, List<Field> fields) {
		Map<String, Object> params = new HashMap<>();
		if (fields != null) {
			for (Field field : fields) {
				String fieldName = field.getName();
				Object fieldValue = ReflectionUtils.findFieldValue(entity, field);
				params.put(fieldName, fieldValue);
			}
		}
		return params;
	}

	public <T> List<QueryValue> createQueryValueListParams(T entity, List<Field> fields) {
		List<QueryValue> params = new ArrayList<>();
		if (fields != null) {
			for (Field field : fields) {
				String fieldName = field.getName();
				Object fieldValue = ReflectionUtils.findFieldValue(entity, field);
				params.add(new QueryValue(fieldName, fieldValue));
			}
		}
		return params;
	}

	public <T> List<QueryValue> createDefaultFieldsQueryValueParams(T entity) {
		List<Field> fields = ReflectionUtils.findFieldsByAnnotations(entity.getClass(), Column.class, JoinColumn.class, ManyToOne.class, AssociationOverride.class);
		return createQueryValueListParams(entity, fields);
	}

	public <T> List<QueryValue> createDefaultFieldsQueryValueNotNullParams(T entity) {
		List<Field> fields = ReflectionUtils.findFieldsByAnnotations(entity.getClass(), Column.class, JoinColumn.class, ManyToOne.class, AssociationOverride.class);
		return removeNullValues(createQueryValueListParams(entity, fields));
	}

	public <T> Map<String, Object> createDefaultFieldsMapParams(T entity) {
		List<Field> fields = ReflectionUtils.findFieldsByAnnotations(entity.getClass(), Column.class, JoinColumn.class, ManyToOne.class, AssociationOverride.class);
		return createMapParams(entity, fields);
	}

	public <T> Map<String, Object> createDefaultFieldsMapNotNullParams(T entity) {
		List<Field> fields = ReflectionUtils.findFieldsByAnnotations(entity.getClass(), Column.class, JoinColumn.class, ManyToOne.class, AssociationOverride.class);
		return removeNullValues(createMapParams(entity, fields));
	}

	public <T> Map<String, Object> createDefaultFieldsMapAndObjectIdNotNullParams(T entity) {
		List<Field> fields = ReflectionUtils.findFieldsByAnnotations(entity.getClass(), Column.class, JoinColumn.class, ManyToOne.class, AssociationOverride.class);
		return removeNullValuesAndNullObjectIds(createMapParams(entity, fields));
	}

	@Transactional(readOnly=true)
	public <T> List<T> findByHQLEntityFilter(T entity, String... fieldsOrderBy) {
		Map<String, Object> params = createDefaultFieldsMapParams(entity);
		return findByHQLFilter(entity.getClass(), params, fieldsOrderBy);
	}

	@Transactional(readOnly=true)
	public <T> List<T> findByHQLEntityFilterWithFromAppend(T entity, String fromClauseAppendText, String... fieldsOrderBy) {
		Map<String, Object> params = createDefaultFieldsMapParams(entity);
		return findByHQLFilter(entity.getClass(), params, fromClauseAppendText, fieldsOrderBy);
	}

	@Transactional(readOnly=true)
	public <T> List<T> findByHQLFilter(Class<?> clazz, Map<String, Object> params, String... fieldsOrderBy) {
		List<QueryValue> queryValues = new ArrayList<>();
		for (String key : params.keySet()) {
			queryValues.add(new QueryValue(key, params.get(key)));
		}
		return findByHQLFilter(clazz, queryValues, fieldsOrderBy);
	}

	@Transactional(readOnly=true)
	public <T> List<T> findByHQLFilter(Class<?> clazz, Map<String, Object> params, String fromClauseAppendText, String... fieldsOrderBy) {
		List<QueryValue> queryValues = new ArrayList<>();
		for (String key : params.keySet()) {
			queryValues.add(new QueryValue(key, params.get(key)));
		}
		return findByHQLFilter(clazz, queryValues, fromClauseAppendText, fieldsOrderBy);
	}

	@Transactional(readOnly=true)
	public <T> List<T> findByHQLFilter(Class<?> clazz, List<QueryValue> queryValues, String... fieldsOrderBy) {
		return findByHQLFilter(clazz, queryValues, null, fieldsOrderBy);
	}

	@Transactional(readOnly=true)
	public <T> List<T> findByHQLFilter(Class<?> clazz, List<QueryValue> queryValues, String fromClauseAppendText, String... fieldsOrderBy) {
		if (fromClauseAppendText == null) fromClauseAppendText = "";
		StringBuffer sb = new StringBuffer("from "+clazz.getName()+" "+fromClauseAppendText+" where 1=1 ");

		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<QueryValue> it = queryValues.iterator();
		while (it.hasNext()) {

			QueryValue queryValue = it.next();
			String entityPropertyName = queryValue.getEntityPropertyName();
			String queryParamName = queryValue.getQueryParamName();
			if (queryParamName == null) queryParamName = queryValue.getEntityPropertyName();
			Object value = queryValue.getQueryParamValue();

			if (value != null && !value.toString().isEmpty()) {

				if (!Primitive.isPrimitiveOrWrapper(value) && !(value instanceof String)) {
					// Se entrar nesse IF, então o valor não é nenhum dos tipos primitivos é um Objeto diferente de String.
					if (value instanceof List) {
						List valueList = (List) value;
						if (valueList == null || valueList.isEmpty()) {
							it.remove();
						} else {
							sb.append(" and "+entityPropertyName+" in "+"(:"+queryParamName+") ");
							map.put(queryParamName, value);
						}
						continue;
					} else {
						Object v = ReflectionUtils.findFirstFieldValueByAnnotation(value, Id.class);
						if (v == null) {
							it.remove();
							continue;
						}
					}
				}

				if (value instanceof String) sb.append(" and lower("+entityPropertyName+") like "+"lower(:"+queryParamName+") ");
				else sb.append(" and "+entityPropertyName+" = "+":"+queryParamName+" ");

				map.put(queryParamName, value);

			} else it.remove();
		}

		if (fieldsOrderBy != null && fieldsOrderBy.length != 0) {
			for (int i = 0 ; i < fieldsOrderBy.length ; i++) {
				String fieldOrderBy = fieldsOrderBy[i];
				if (fieldOrderBy != null && !fieldOrderBy.isEmpty()) {
					if (i == 0) sb.append(" order by ");
					if (i + 1 == fieldsOrderBy.length) sb.append(fieldOrderBy);
					else sb.append(fieldOrderBy+", ");
				}
			}
		}

		Query q = getEntityManager().createQuery(sb.toString());
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof String) q.setParameter(key, "%"+value.toString()+"%");
			else q.setParameter(key, value);
		}
		return q.getResultList();

	}

	public Map<String, Object> removeNullValues(Map<String, Object> params) {
		if (params != null) {
			Map<String, Object> notNullParams = new HashMap<>();
			for (String key : params.keySet()) {
				Object value = params.get(key);

				if (value != null && !value.toString().isEmpty()) {
					notNullParams.put(key, value);
				}
			}
			return notNullParams;
		} return null;
	}

	public Map<String, Object> removeNullValuesAndNullObjectIds(Map<String, Object> params) {
		if (params != null) {
			Map<String, Object> notNullParams = new HashMap<>();

			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object value = params.get(key);

				if (value != null && !value.toString().isEmpty()) {
					if (!Primitive.isPrimitiveOrWrapper(value) && !(value instanceof String)) {
						Object idValue = ReflectionUtils.findFirstFieldValueByAnnotation(value, Id.class);
						if (idValue == null) {
							continue;
						}
					}

					notNullParams.put(key, value);
				}
			}

			return notNullParams;
		} return null;
	}

	public List<QueryValue> removeNullValues(List<QueryValue> queryValues) {
		if (queryValues != null) {
			List<QueryValue> notNullParams = new ArrayList<>();
			for (QueryValue queryValue : queryValues) {
				if (queryValue != null) {
					Object value = queryValue.getQueryParamValue();

					if (value != null && !value.toString().isEmpty()) {
						notNullParams.add(new QueryValue(queryValue.getEntityPropertyName(), value));
					}
				}
			} return notNullParams;
		} return null;
	}

	public Object deepObjectSearchValue(String key, Object objectValue) {

		if (objectValue == null) return null;

		if (!Primitive.isPrimitiveOrWrapper(objectValue) && !(objectValue instanceof String)) {
			int dotIndex = key.indexOf(".");

			String property;
			if (dotIndex != -1) property = key.substring(0, dotIndex);
			else property = key;

			Object newObjectValue = ReflectionUtils.findFieldValue(objectValue, property);

			String newKey;
			if (property != key) {
				newKey = key.substring(dotIndex+1);
			} else {
				return newObjectValue;
			}

			return deepObjectSearchValue(newKey, newObjectValue);
		}

		return null;

	}

	public Object basicFieldTypeTransform(Object value, Field field) {

		Type fieldType = field.getType();
		String fieldName = field.getName();

		if (value != null) {

			try {

				String myValue = value.toString();
				myValue = myValue.trim();

				if (Integer.class.equals(fieldType)) {
					value = new Double(myValue).intValue();
				} else if (String.class.equals(fieldType)) {
					value = myValue;
				} else if (BigDecimal.class.equals(fieldType)) {
					value = new BigDecimal(myValue);
				} else if (Long.class.equals(fieldType)) {
					value = Long.parseLong(myValue);
				} else if (Byte.class.equals(fieldType)) {
					value = Byte.parseByte(myValue);
				} else if (Short.class.equals(fieldType)) {
					value = Short.parseShort(myValue);
				} else if (Double.class.equals(fieldType)) {
					value = Double.parseDouble(myValue);
				} else if (Float.class.equals(fieldType)) {
					value = Float.parseFloat(myValue);
				} else if (BigInteger.class.equals(fieldType)) {
					value = new BigInteger(myValue);
				}

			} catch (NumberFormatException e) {
				throw new RuntimeException("Não foi possível setar o valor: "+value+", no campo: "+fieldName+", do tipo: "+fieldType+", da classe: "+field.getDeclaringClass()+". Erro de conversão de tipo.", e);
			}

		}

		return value;

	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

}

class SQLObjectWrapper {

	public static <T> String wrap(Object value) {

		if (value == null) {
			return nullWrap();
		} else if (value instanceof String) {
			return stringWrap((String) value);
		} else if (value instanceof Number) {
			return numberWrap((Number) value);
		} else if (value instanceof Date) {
			return timestampWrap((Date) value);
		} else if (value instanceof Character) {
			return characterWrap((Character) value);
		} else if (value instanceof Boolean) {
			return booleanWrap((Boolean) value);
		}

		throw new RuntimeException("Unwrappable Object: "+value.getClass()+", value: "+value);

	}

	public static String dateWrap(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return String.format("to_date('%s', '%s')", sdf.format(date), "DD/MM/YYYY HH24:MI:SS");
	}

	public static String timestampWrap(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return String.format("to_timestamp('%s', '%s')", sdf.format(date), "DD/MM/YYYY HH24:MI:SS");
	}

	public static String numberWrap(Number number) {
		return number+"";
	}

	public static String booleanWrap(Boolean value) {
		return value.toString();
	}

	public static String stringWrap(String value) {
		if (value.indexOf("'") != -1) {
			value = value.replace("'", "''");
		}
		return "'"+value+"'";
	}

	public static String characterWrap(Character value) {
		return "'"+value+"'";
	}

	public static String nullWrap() {
		return null;
	}

    public boolean isPersisted(BaseEntity entity){
        return entity != null && entity.getId() != null;
    }

    public boolean isNotPersisted(BaseEntity entity){
        return !isPersisted(entity);
    }
}
