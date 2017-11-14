package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2016/12/12.
 */
public class DataModelTable {
    private Long id;
    private Integer modelId;
    private Long tableId;
    private String tableAlias;
    private String canvasPosition;

    public DataModelTable () {
    }

    public DataModelTable (Integer modelId, Long tableId, String tableAlias, String canvasPosition) {
        this.modelId = modelId;
        this.tableId = tableId;
        this.tableAlias = tableAlias;
        this.canvasPosition = canvasPosition;
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

    public Long getTableId () {
        return tableId;
    }

    public void setTableId (Long tableId) {
        this.tableId = tableId;
    }

    public String getTableAlias () {
        return tableAlias;
    }

    public void setTableAlias (String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public String getCanvasPosition () {
        return canvasPosition;
    }

    public void setCanvasPosition (String canvasPosition) {
        this.canvasPosition = canvasPosition;
    }

    @Override
    public String toString () {
        return "DataModelTable{" +
                "id=" + id +
                ", modelId=" + modelId +
                ", tableId=" + tableId +
                ", tableAlias='" + tableAlias + '\'' +
                ", canvasPosition='" + canvasPosition + '\'' +
                '}';
    }
}
