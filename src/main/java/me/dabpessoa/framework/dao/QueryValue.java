package me.dabpessoa.framework.dao;

public class QueryValue {

    private String entityPropertyName;
    private String queryParamName;
    private Object queryParamValue;

    public QueryValue() {}

    public QueryValue(String entityPropertyName, String queryParamName, Object queryParamValue) {
        this.entityPropertyName = entityPropertyName;
        this.queryParamName = queryParamName;
        this.queryParamValue = queryParamValue;
    }

    public QueryValue(String entityPropertyName, Object queryParamValue) {
        this.entityPropertyName = entityPropertyName;
        this.queryParamValue = queryParamValue;
    }

    public String getEntityPropertyName() {
        return entityPropertyName;
    }

    public void setEntityPropertyName(String entityPropertyName) {
        this.entityPropertyName = entityPropertyName;
    }

    public String getQueryParamName() {
        return queryParamName;
    }

    public void setQueryParamName(String queryParamName) {
        this.queryParamName = queryParamName;
    }

    public Object getQueryParamValue() {
        return queryParamValue;
    }

    public void setQueryParamValue(Object queryParamValue) {
        this.queryParamValue = queryParamValue;
    }

}