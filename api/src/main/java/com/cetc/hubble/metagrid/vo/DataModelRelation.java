package com.cetc.hubble.metagrid.vo;

import io.swagger.models.auth.In;

/**
 * Created by dahey on 2016/12/12.
 */
public class DataModelRelation {
    private Long id;
    private Integer modelId;
    private Long sourceId;
    private Long targetId;
    private Integer relationTypeId;

    public DataModelRelation () {
    }

    public DataModelRelation (Integer modelId, Long sourceId, Long targetId, Integer relationTypeId) {
        this.modelId = modelId;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.relationTypeId = relationTypeId;
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

    public Long getSourceId () {
        return sourceId;
    }

    public void setSourceId (Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId () {
        return targetId;
    }

    public void setTargetId (Long targetId) {
        this.targetId = targetId;
    }

    public Integer getRelationTypeId () {
        return relationTypeId;
    }

    public void setRelationTypeId (Integer relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    @Override
    public String toString () {
        return "DataModelRelation{" +
                "id=" + id +
                ", modelId=" + modelId +
                ", sourceId=" + sourceId +
                ", targetId=" + targetId +
                ", relationTypeId=" + relationTypeId +
                '}';
    }
}
