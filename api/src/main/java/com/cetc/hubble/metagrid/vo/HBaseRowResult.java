package com.cetc.hubble.metagrid.vo;


import java.util.ArrayList;
import java.util.List;

/**
 * HBase行结果对象
 */

public class HBaseRowResult {
    // 行id
    private String rowId;
    // 列簇结果集合
    private List<HBaseColFamilyResult> colFamily = new ArrayList<HBaseColFamilyResult>();

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public List<HBaseColFamilyResult> getColFamily() {
        return colFamily;
    }

    public void setColFamily(List<HBaseColFamilyResult> colFamily) {
        this.colFamily = colFamily;
    }
}
