package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/2/13.
 */
public class DataQualityTopn {
    private Long fieldId;
    private String fieldValue;
    private Long count;

    public String getFieldValue () {
        return fieldValue;
    }

    public void setFieldValue (String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Long getCount () {
        return count;
    }

    public void setCount (Long count) {
        this.count = count;
    }


    public Long getFieldId () {
        return fieldId;
    }

    public void setFieldId (Long fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public String toString () {
        return "DataQualityTopn{" +
                "fieldId=" + fieldId +
                ", fieldValue='" + fieldValue + '\'' +
                ", count=" + count +
                '}';
    }
}
