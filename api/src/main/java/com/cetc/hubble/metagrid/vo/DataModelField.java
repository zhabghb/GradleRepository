package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2016/12/12.
 */
public class DataModelField {
    private Long id;
    private Integer modelId;
    private Long fieldId;
    private String fieldAlias;
    private String fieldType;
    private String fieldName;

    public DataModelField () {
    }

    public DataModelField (Integer modelId, Long fieldId, String fieldName,String fieldAlias,String fieldType) {
        this.modelId = modelId;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.fieldAlias = fieldAlias;
        this.fieldType = fieldType;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Integer getModelId () {
        return modelId;
    }

    public void setModelId (Integer modelId) {
        this.modelId = modelId;
    }

    public Long getFieldId () {
        return fieldId;
    }

    public void setFieldId (Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldAlias () {
        return fieldAlias;
    }

    public void setFieldAlias (String fieldAlias) {
        this.fieldAlias = fieldAlias;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "DataModelField{" +
                "id=" + id +
                ", modelId=" + modelId +
                ", fieldId=" + fieldId +
                ", fieldAlias='" + fieldAlias + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
