package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by dahey on 2016/9/5.
 *
 *  查询SQL历史
 */

public class QueryHistory {
    private String sql;
    private Integer sourceId;
    /*不入DB，仅便于前台展示*/
    private String sourceName;
    private String db;
    private Date createTime;
    // 用户起的SQL归类名称
    private String sqlName;

    public QueryHistory(){

    }

    public QueryHistory(String sql, String sqlName, String sourceName, String db,Date createTime) {
        this.sql = sql;
        this.sqlName = sqlName;
        this.sourceName = sourceName;
        this.db = db;
        this.createTime = createTime;
    }

    @ApiModelProperty(name = "SQL",example = "select * from example",required = true)
    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
    @ApiModelProperty(name = "数据源ID",example = "oracle",required = true)
    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }
    @ApiModelProperty(name = "选中的数据库",example = "default",required = true)
    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    @ApiModelProperty(name = "保存时间",example = "2016-11-01 10:53:42",required = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @ApiModelProperty(name = "SQL名称",example = "车辆查询",required = false)
    public String getSqlName() { return sqlName; }

    public void setSqlName(String sqlName) { this.sqlName = sqlName; }
}
