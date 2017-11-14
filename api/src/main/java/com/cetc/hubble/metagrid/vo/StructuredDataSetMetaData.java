package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * Meta data object for structured data.
 *
 * @author tao
 */
public class StructuredDataSetMetaData {

    // data set identification code
    private Long dataSetID;
    private String name;
    private String parentName;
    private String dataSourceName;
    private String urn;
    private String type;
    private List<Tag> tags;
    private String creationTime;
    private String storageLocation;
    // user comment on table or in other words bias name
    private String biasName;
    private Long columnNum;
    private String lastModificationTime;

    private Map<String, Object>  dataSource;


    public StructuredDataSetMetaData() {
    }

    public StructuredDataSetMetaData(Long dataSetID, String name,String parentName, String dataSourceName, List<Tag> tags, String creationTime,
                                     String storageLocation, String biasName, Long columnNum, String lastModificationTime,String urn,String type) {
        this.dataSetID = dataSetID;
        this.name = name;
        this.parentName = parentName;
        this.dataSourceName = dataSourceName;
        this.tags = tags;
        this.creationTime = creationTime;
        this.storageLocation = storageLocation;
        this.biasName = biasName;
        this.columnNum = columnNum;
        this.lastModificationTime = lastModificationTime;
        this.urn = urn;
        this.type = type;
    }

    @ApiModelProperty(name = "数据集ID", example = "1", required = true)
    public Long getDataSetID() {
        return dataSetID;
    }

    public void setDataSetID(Long dataSetID) {
        this.dataSetID = dataSetID;
    }

    @ApiModelProperty(name = "数据集名称", example = "hive_dpajxx")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(name = "数据名称", example = "hive_dpajxx", required = false)
    public String getDataSourceName() { return dataSourceName; }

    public void setDataSourceName(String dataSourceName) { this.dataSourceName = dataSourceName; }

    @ApiModelProperty(name = "标签", example = "车辆", required = true)
    public List<Tag> getTags() {
        return tags;
    }

    public void setTag(List<Tag> tags) {
        this.tags = tags;
    }

    @ApiModelProperty(name = "创建时间", example = "2016/01/18 12:00:00", required = false)
    public String getCreationTime() { return creationTime; }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    @ApiModelProperty(name = "存储位置", example = "warehouse/chinacloud/hive_dqajxx", required = false)
    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    @ApiModelProperty(name = "中文表名/别名", example = "案件信息", required = true)
    public String getBiasName() {
        return biasName;
    }

    public void setBiasName(String biasName) {
        this.biasName = biasName;
    }

    @ApiModelProperty(name = "字段个数", example = "18", required = false)
    public Long getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(Long columnNum) {
        this.columnNum = columnNum;
    }

    @ApiModelProperty(name = "最后修改时间", example = "2016/01/18 12:00:00", required = false)
    public String getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(String lastModificationTime) { this.lastModificationTime = lastModificationTime; }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Map<String, Object> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, Object> dataSource) {
        this.dataSource = dataSource;
    }
}