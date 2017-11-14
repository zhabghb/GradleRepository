package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 数据源实体参数父类
 */

public class DataSourceParam {
    // 主键id
    private String id;
    // 数据源分类名称
    private String categoryName;
    // 数据源名称
    private String dataSourceName;
    // cron表达式
    private String cronExpression;

    @ApiModelProperty(name = "主键",example = "新增没有，修改有",required = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ApiModelProperty(name = "数据源分类名称",example = "选择的数据源分类名称",required = true)
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @ApiModelProperty(name = "数据源名称",example = "成都公安数据源",required = true)
    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @ApiModelProperty(name = "cron表达式",required = true)
    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
