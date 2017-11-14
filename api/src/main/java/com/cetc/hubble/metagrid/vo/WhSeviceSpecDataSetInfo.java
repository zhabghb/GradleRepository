package com.cetc.hubble.metagrid.vo;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据服务数据规格自定义表单数据表信息
 */
public class WhSeviceSpecDataSetInfo {
    private String type = "array";
    private Boolean required =true;
    private String format="table";
    private String title;
    private Map<String,Boolean> options ;
    private Boolean uniqueItems =true;
    private Map<String,Object> items;
    private List<WhServiceSpecColumn> def_wh_default___;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setOptions(Map<String, Boolean> options) {
        this.options = options;
    }

    public Boolean getUniqueItems() {
        return uniqueItems;
    }

    public void setUniqueItems(Boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
    }

    public void setItems(Map<String, Object> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Boolean> getOptions() {
        return options;
    }

    public void setOptions(Boolean collapsed) {
        Map<String, Boolean> options =new LinkedHashMap<>();
        options.put("collapsed",collapsed);
        options.put("disable_collapse", false);
        this.options = options;
    }

    public Map<String, Object> getItems() {
        return items;
    }

    public void setItems() {
        Map<String, Object> items = new LinkedHashMap<>();
        items.put("type","object");
        Map<String, Object> properties = new LinkedHashMap<>();

        Map<String, Object> columnName = new LinkedHashMap<>();
        columnName.put("type","string");
        columnName.put("title","字段名");
        columnName.put("readOnly", true);

        Map<String, Object> columnDesc = new LinkedHashMap<>();
        columnDesc.put("type", "string");
        columnDesc.put("title", "字段描述");
        columnDesc.put("readOnly", true);

        Map<String, Object> checked = new LinkedHashMap<>();
        checked.put("type", "boolean");
        checked.put("title", "是否选择");
        checked.put("default", true);
        checked.put("format", "checkbox");

        properties.put("columnName",columnName);
        properties.put("columnDesc",columnDesc);
        properties.put("checked",checked);
        items.put("properties",properties);
        this.items = items;
    }

    public List<WhServiceSpecColumn> getDef_wh_default___() {
        return def_wh_default___;
    }

    public void setDef_wh_default___(List<WhServiceSpecColumn> def_wh_default___) {
        this.def_wh_default___ = def_wh_default___;
    }
}
