package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * hdfs数据源实体参数
 */
public class HdfsDataSourceParam extends DataSourceParam{
    private String hdfsUrl;
    private String hdfsUsername;
    private String hdfsPassword;
    private String hdfsdataSerport;





    @ApiModelProperty(name = "hdfs.URL",example = "http://172.16.50.80:7180",required = false)
    public String getHdfsUrl() {
        return hdfsUrl;
    }

    public void setHdfsUrl(String hdfsUrl) {
        this.hdfsUrl = hdfsUrl;
    }

    @ApiModelProperty(name = "hdfs用户名",required = false)
    public String getHdfsUsername() {
        return hdfsUsername;
    }

    public void setHdfsUsername(String hdfsUsername) {
        this.hdfsUsername = hdfsUsername;
    }

    @ApiModelProperty(name = "hdfs密码",required = false)
    public String getHdfsPassword() {
        return hdfsPassword;
    }

    public void setHdfsPassword(String hdfsPassword) {
        this.hdfsPassword = hdfsPassword;
    }


    @ApiModelProperty(name = "hdfs數據服務端口",example = "8020",required = false)
    public String getHdfsdataSerport() {
        return hdfsdataSerport;
    }

    public void setHdfsdataSerport(String hdfsdataSerport) {
        this.hdfsdataSerport = hdfsdataSerport;
    }
}
