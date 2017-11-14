package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import metagrid.common.vo.QueryParam;

/**
 * 导出查询数据的参数
 */
@ApiModel(value = "ExportParam",description = "导出的请求参数")
public class ExportParam {
    @ApiModelProperty(notes = "是否是同步处理，暂不支持", example = "1000", required = false)
    private Boolean sync = false;
    @ApiModelProperty(notes = "返回数据的限制", example = "1000", required = true)
    private int limit;
    @ApiModelProperty(notes = "查询的SQL", example = "select * from table_name", required = true)
    private String sql;
    @ApiModelProperty(notes = "数据源ID", example = "1", required = true)
    private int sourceId;
    @ApiModelProperty(notes = "异步执行后通知的地址", example = "http://192.168.0.1/whitehole/callback", required = true)
    private String callBackUrl ;


    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
}