package com.cetc.hubble.metagrid.vo;

import java.util.List;
import java.util.Map;

/**
 * 数据服务数据规格自定义表单
 */
public class WhServiceSpec {
    private String title = "元数据表";
    private String type = "object";
    private Map<String,WhSeviceSpecDataSetInfo> properties;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, WhSeviceSpecDataSetInfo> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, WhSeviceSpecDataSetInfo> properties) {
        this.properties = properties;
    }
}
