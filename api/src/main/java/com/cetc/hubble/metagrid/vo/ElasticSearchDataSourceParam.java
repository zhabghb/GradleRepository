package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * ElasticSearch数据源实体参数
 */

public class ElasticSearchDataSourceParam extends DataSourceParam{
    // elasticsearch主机
    private String elasticsearchHost;
    // elasticsearch端口
    private String elasticsearchPort;

    @ApiModelProperty(name = "elasticsearch主机",required = false)
    public String getElasticsearchHost() {
        return elasticsearchHost;
    }

    public void setElasticsearchHost(String elasticsearchHost) {
        this.elasticsearchHost = elasticsearchHost;
    }

    @ApiModelProperty(name = "elasticsearch端口",required = false)
    public String getElasticsearchPort() {
        return elasticsearchPort;
    }

    public void setElasticsearchPort(String elasticsearchPort) {
        this.elasticsearchPort = elasticsearchPort;
    }
}
