package com.cetc.hubble.metagrid.vo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/14.
 */
public class DomainServiceVO {

    private Integer id;
    private String name;
    private String description;
    private List<Map<String, Object>> dataResources;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Map<String, Object>> getDataResources() {
        return dataResources;
    }

    public void setDataResources(List<Map<String, Object>> dataResources) {
        this.dataResources = dataResources;
    }
}
