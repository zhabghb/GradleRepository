package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * PGXZ数据源实体参数
 */

public class PGXZDataSourceParam extends DataSourceParam {
    // pgxzJDBC.URL
    private String pgxzUrl;
    // pgxz用户名
    private String pgxzUsername;
    // pgxz密码
    private String pgxzPassword;

    @ApiModelProperty(name = "pgxzJDBC.URL", required = false)
    public String getPgxzUrl() {
        return pgxzUrl;
    }

    public void setPgxzUrl(String pgxzUrl) {
        this.pgxzUrl = pgxzUrl;
    }

    @ApiModelProperty(name = "pgxz用户名", required = false)
    public String getPgxzUsername() {
        return pgxzUsername;
    }

    public void setPgxzUsername(String pgxzUsername) {
        this.pgxzUsername = pgxzUsername;
    }

    @ApiModelProperty(name = "pgxz密码", required = false)
    public String getPgxzPassword() {
        return pgxzPassword;
    }

    public void setPgxzPassword(String pgxzPassword) {
        this.pgxzPassword = pgxzPassword;
    }
}
