package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * HBase数据源实体参数
 */

public class HBaseDataSourceParam extends DataSourceParam{
/*    // trafodionJDBC.URL
    private String trafodionUrl;
    // trafodion用户名
    private String trafodionUsername;
    // trafodion密码
    private String trafodionPassword;*/

    //hiveServer2Url
    private String hiveServer2Url;
    // hbaseUrl
//    private String hbaseUrl;
    // zookeeperUrl
    private String zookeeperUrl;
    // zookeeper client port
    private String zkClientPort;

//    @ApiModelProperty(name = "HBaseURL",required = false)
//    public String getHbaseUrl() {
//        return hbaseUrl;
//    }

//    public void setHbaseUrl(String hbaseUrl) {
//        this.hbaseUrl = hbaseUrl;
//    }

    @ApiModelProperty(name = "Zookeeper集群", value = "172.16.50.22", required = true)
    public String getZookeeperUrl() {
        return zookeeperUrl;
    }

    public void setZookeeperUrl(String zookeeperUrl) {
        this.zookeeperUrl = zookeeperUrl;
    }

    @ApiModelProperty(name = "ZK客户端口", value = "2181", required = true)
    public String getZkClientPort() {
        return zkClientPort;
    }

    public void setZkClientPort(String zkClientPort) {
        this.zkClientPort = zkClientPort;
    }

    public String getHiveServer2Url () {
        return hiveServer2Url;
    }

    public void setHiveServer2Url (String hiveServer2Url) {
        this.hiveServer2Url = hiveServer2Url;
    }
}
