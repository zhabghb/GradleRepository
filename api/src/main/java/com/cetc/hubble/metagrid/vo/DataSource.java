package com.cetc.hubble.metagrid.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunfeng on 2016/10/17.
 */
public class DataSource {
    // 主键
    private int id;
    private String etlJobName;
    // 数据源分类
    private String etlType;
    //cron表达式
    private String cronExpr;
    // 是否激活
    private boolean isActive;
    // 数据源名称
    private String dataSourceName;
    // 创建时间
    private String createTime;
    // 最后更新时间
    private String lastUpdateTime;
    // EtlJobProperty集合
    private List<DataSourceProperty> etlJobProperties = new ArrayList<DataSourceProperty>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEtlJobName() {
        return etlJobName;
    }

    public void setEtlJobName(String etlJobName) {
        this.etlJobName = etlJobName;
    }

    public String getEtlType() {
        return etlType;
    }

    public void setEtlType(String etlType) {
        this.etlType = etlType;
    }

    public String getCronExpr() {
        return cronExpr;
    }

    public void setCronExpr(String cronExpr) {
        this.cronExpr = cronExpr;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<DataSourceProperty> getEtlJobProperties() {
        return etlJobProperties;
    }

    public void setEtlJobProperties(List<DataSourceProperty> etlJobProperties) {
        this.etlJobProperties = etlJobProperties;
    }
}
