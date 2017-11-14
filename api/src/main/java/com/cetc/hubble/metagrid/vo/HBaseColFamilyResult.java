package com.cetc.hubble.metagrid.vo;


import java.util.ArrayList;
import java.util.List;

/**
 * HBase列簇结果对象
 */

public class HBaseColFamilyResult {
    // 列簇名
    private String name;
    // 列属性结果集合
    private List<HBaseColResult> detail = new ArrayList<HBaseColResult>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HBaseColResult> getDetail() {
        return detail;
    }

    public void setDetail(List<HBaseColResult> detail) {
        this.detail = detail;
    }
}
