package com.cetc.hubble.metagrid.vo;

import java.util.List;
import java.util.Map;

/**
 * Created by dahey on 2017/2/13.
 * 数据质量特征VO
 */
public class DataQuality {
    private Long fieldId;
    private Long totalCount;
    private Long validCount;
    private Long invalidCount;
    private Long nulls;
    private Long empties;
    private Long uniqueValues;
    private String sample;
    private List<Map<String, Object>> columns;

    public List<Map<String, Object>> getColumns () {
        return columns;
    }

    public void setColumns (List<Map<String, Object>> columns) {
        this.columns = columns;
    }

    public Long getTotalCount () {
        return totalCount;
    }

    public void setTotalCount (Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getValidCount () {
        return validCount;
    }

    public void setValidCount (Long validCount) {
        this.validCount = validCount;
    }

    public Long getInvalidCount () {
        return invalidCount;
    }

    public void setInvalidCount (Long invalidCount) {
        this.invalidCount = invalidCount;
    }

    public Long getNulls () {
        return nulls;
    }

    public void setNulls (Long nulls) {
        this.nulls = nulls;
    }

    public Long getEmpties () {
        return empties;
    }

    public void setEmpties (Long empties) {
        this.empties = empties;
    }

    public Long getUniqueValues () {
        return uniqueValues;
    }

    public void setUniqueValues (Long uniqueValues) {
        this.uniqueValues = uniqueValues;
    }

    public Long getFieldId () {
        return fieldId;
    }

    public void setFieldId (Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getSample () {
        return sample;
    }

    public void setSample (String sample) {
        this.sample = sample;
    }


    @Override
    public String toString () {
        return "DataQuality{" +
                "fieldId=" + fieldId +
                ", totalCount=" + totalCount +
                ", validCount=" + validCount +
                ", invalidCount=" + invalidCount +
                ", nulls=" + nulls +
                ", empties=" + empties +
                ", uniqueValues=" + uniqueValues +
                '}';
    }
}
