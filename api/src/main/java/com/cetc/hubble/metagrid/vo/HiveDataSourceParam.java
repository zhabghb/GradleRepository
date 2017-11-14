package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Hive数据源实体参数
 */

public class HiveDataSourceParam extends DataSourceParam{
/*    // trafodionJDBC.URL
    private String trafodionUrl;
    // trafodion用户名
    private String trafodionUsername;
    // trafodion密码
    private String trafodionPassword;*/

    //hiveServer2Url
    private String hiveServer2Url;
    // hiveJDBC.URL
    private String hiveUrl;
    // hive用户名
    private String hiveUsername;
    // hive密码
    private String hivePassword;

    @ApiModelProperty(name = "hiveJDBC.URL",required = false)
    public String getHiveUrl() {
        return hiveUrl;
    }

    public void setHiveUrl(String hiveUrl) {
        this.hiveUrl = hiveUrl;
    }

    @ApiModelProperty(name = "hive用户名",required = false)
    public String getHiveUsername() {
        return hiveUsername;
    }

    public void setHiveUsername(String hiveUsername) {
        this.hiveUsername = hiveUsername;
    }

    @ApiModelProperty(name = "hive密码",required = false)
    public String getHivePassword() {
        return hivePassword;
    }

    public void setHivePassword(String hivePassword) {
        this.hivePassword = hivePassword;
    }

    public String getHiveServer2Url () {
        return hiveServer2Url;
    }

    public void setHiveServer2 (String hiveServer2Url) {
        this.hiveServer2Url = hiveServer2Url;
    }
}
