package com.cetc.hubble.metagrid.vo;

import java.util.Date;
import java.util.List;

/**
 * Created by cyq on 2017/9/26.
 * 数据质量规则第二版
 */

public class DataQualityRule_V2 {

    private Integer id;

    private String name;

    private String description;

    private String jarPath;

    private String source;

    private Date createTime;

    private List<DataQualityRuleParam> ruleParam;

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

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<DataQualityRuleParam> getRuleParam() {
        return ruleParam;
    }

    public void setRuleParam(List<DataQualityRuleParam> ruleParam) {
        this.ruleParam = ruleParam;
    }
}
