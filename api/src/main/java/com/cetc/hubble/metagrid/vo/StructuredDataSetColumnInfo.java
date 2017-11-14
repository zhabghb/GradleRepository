package com.cetc.hubble.metagrid.vo;

import java.util.List;

/**
 * Data set column information.
 *
 * @author tao
 */
public class StructuredDataSetColumnInfo {

    // data set identification code
    Long dataSetID;
    List<StructuredDataSetColumn> columns;

    public StructuredDataSetColumnInfo() {
    }

    public StructuredDataSetColumnInfo(Long dataSetID, List<StructuredDataSetColumn> columns) {
        this.dataSetID = dataSetID;
        this.columns = columns;
    }

    public Long getDataSetID() { return dataSetID; }

    public void setDataSetID(Long dataSetID) { this.dataSetID = dataSetID; }

    public List<StructuredDataSetColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<StructuredDataSetColumn> columns) {
        this.columns = columns;
    }

}
