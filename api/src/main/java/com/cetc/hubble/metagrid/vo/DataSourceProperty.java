package com.cetc.hubble.metagrid.vo;

/**
 * Created by sunfeng on 2016/10/17.
 */
public class DataSourceProperty {
    // 属性名称
    private String propertyName;
    // 属性值
    private String propertyValue;
    // 是否加密
    private boolean isEncrypted;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
}
