package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by tao on 5/01/17.
 */
public class DataModelStatus {

    private Integer id;
    private String name;
    private String alias;
    private Long tableCount;
    private Long relationshipCount;
    private Long fieldCount;

    public DataModelStatus() {
    }

    public DataModelStatus(Integer id, String name, String alias,
                           Long tableCount, Long relationshipCount, Long fieldCount) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.tableCount = tableCount;
        this.relationshipCount = relationshipCount;
        this.fieldCount = fieldCount;
    }

    @ApiModelProperty(name = "模型ID", example = "1")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ApiModelProperty(name = "模型名称", example = "样例")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(name = "模型别名", example = "样例")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @ApiModelProperty(name = "模型涉及表总数", example = "1")
    public Long getTableCount() {
        return tableCount;
    }

    public void setTableCount(Long tableCount) {
        this.tableCount = tableCount;
    }

    @ApiModelProperty(name = "模型涉及关联关系数量", example = "1")
    public Long getRelationshipCount() {
        return relationshipCount;
    }

    public void setRelationshipCount(Long relationshipCount) {
        this.relationshipCount = relationshipCount;
    }

    @ApiModelProperty(name = "模型涉及字段数量", example = "1")
    public Long getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(Long fieldCount) {
        this.fieldCount = fieldCount;
    }
}
