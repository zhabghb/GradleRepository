package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Oracle数据源实体参数
 */

public class OracleDataSourceParam extends DataSourceParam{
    // oracleJDBC.URL
    private String oracleUrl;
    // oracle用户名
    private String oracleUsername;
    // oracle密码
    private String oraclePassword;

    @ApiModelProperty(name = "oracleJDBC.URL",required = false)
    public String getOracleUrl() {
        return oracleUrl;
    }

    public void setOracleUrl(String oracleUrl) {
        this.oracleUrl = oracleUrl;
    }

    @ApiModelProperty(name = "oracle用户名",required = false)
    public String getOracleUsername() {
        return oracleUsername;
    }

    public void setOracleUsername(String oracleUsername) {
        this.oracleUsername = oracleUsername;
    }

    @ApiModelProperty(name = "oracle密码",required = false)
    public String getOraclePassword() {
        return oraclePassword;
    }

    public void setOraclePassword(String oraclePassword) {
        this.oraclePassword = oraclePassword;
    }
}
