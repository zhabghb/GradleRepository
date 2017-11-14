package com.cetc.hubble.metagrid.vo;


/**
 * HBase列属性结果对象
 */

public class HBaseColResult{
    // 列名
    private String colName;
    // 列值
    private String colValue;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColValue() {
        return colValue;
    }

    public void setColValue(String colValue) {
        this.colValue = colValue;
    }
}
