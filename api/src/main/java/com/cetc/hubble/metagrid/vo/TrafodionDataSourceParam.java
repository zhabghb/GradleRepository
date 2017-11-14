package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Trafodion数据源实体参数
 */

public class TrafodionDataSourceParam extends DataSourceParam{
    // trafodionJDBC.URL
    private String trafodionUrl;
    // trafodion用户名
    private String trafodionUsername;
    // trafodion密码
    private String trafodionPassword;

    @ApiModelProperty(name = "trafodionJDBC.URL",required = false)
    public String getTrafodionUrl() {
        return trafodionUrl;
    }

    public void setTrafodionUrl(String trafodionUrl) {
        this.trafodionUrl = trafodionUrl;
    }

    @ApiModelProperty(name = "trafodion用户名",required = false)
    public String getTrafodionUsername() {
        return trafodionUsername;
    }

    public void setTrafodionUsername(String trafodionUsername) {
        this.trafodionUsername = trafodionUsername;
    }

    @ApiModelProperty(name = "trafodion密码",required = false)
    public String getTrafodionPassword() {
        return trafodionPassword;
    }

    public void setTrafodionPassword(String trafodionPassword) {
        this.trafodionPassword = trafodionPassword;
    }
}
