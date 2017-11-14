package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2016/10/12.
 *
 *  目录树
 */

public class StageLog {


    private Long id;
    private String updateTime;
    private String type;
    private String status;
    private Integer datasetId;
    private String datasetName;
    private String parentName;
    private String datasetAlias;
    private String username;
    private String sourceName;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getDatasetAlias() {
        return datasetAlias;
    }

    public void setDatasetAlias(String datasetAlias) {
        this.datasetAlias = datasetAlias;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }


}
