package com.cetc.hubble.metagrid.vo;

import java.util.ArrayList;

/**
 * Created by dahey on 2016/12/12.
 */
public class FrontModelData {
    private TreeNode tableData;
    private String name;
    private ArrayList<FrontModelField> fields;

    public TreeNode getTableData () {
        return tableData;
    }

    public void setTableData (TreeNode tableData) {
        this.tableData = tableData;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public ArrayList<FrontModelField> getFields () {
        return fields;
    }

    public void setFields (ArrayList<FrontModelField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString () {
        return "ModelData{" +
                "tableData=" + tableData +
                ", name=" + name +
                ", fields=" + fields +
                '}';
    }
}
