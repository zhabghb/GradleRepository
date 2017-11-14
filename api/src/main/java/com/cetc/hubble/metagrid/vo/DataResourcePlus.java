package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/2/20.
 */
public class DataResourcePlus {
    private String ip;
    private String port;
    private String dbType;
    private String dbName;
    private String tableName;
    private String additional;
    private String lastExtractedTime;
    private String count;
    private String tag;
    private String alias;


    public String getIp () {
        return ip;
    }

    public void setIp (String ip) {
        this.ip = ip;
    }

    public String getPort () {
        return port;
    }

    public void setPort (String port) {
        this.port = port;
    }

    public String getDbType () {
        return dbType;
    }

    public void setDbType (String dbType) {
        this.dbType = dbType;
    }

    public String getDbName () {
        return dbName;
    }

    public void setDbName (String dbName) {
        this.dbName = dbName;
    }

    public String getTableName () {
        return tableName;
    }

    public void setTableName (String tableName) {
        this.tableName = tableName;
    }

    public String getAdditional () {
        return additional;
    }

    public void setAdditional (String additional) {
        this.additional = additional;
    }

    public String getLastExtractedTime () {
        return lastExtractedTime;
    }

    public void setLastExtractedTime (String lastExtractedTime) {
        this.lastExtractedTime = lastExtractedTime;
    }

    public String getCount () {
        return count;
    }

    public void setCount (String count) {
        this.count = count;
    }

    public String getTag () {
        return tag;
    }

    public void setTag (String tag) {
        this.tag = tag;
    }

    public String getAlias () {
        return alias;
    }

    public void setAlias (String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataResourcePlus that = (DataResourcePlus) o;

        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (dbType != null ? !dbType.equals(that.dbType) : that.dbType != null) return false;
        if (dbName != null ? !dbName.equals(that.dbName) : that.dbName != null) return false;
        if (tableName != null ? !tableName.equals(that.tableName) : that.tableName != null) return false;
        return additional != null ? additional.equals(that.additional) : that.additional == null;

    }

    @Override
    public int hashCode () {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        result = 31 * result + (dbName != null ? dbName.hashCode() : 0);
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        result = 31 * result + (additional != null ? additional.hashCode() : 0);
        return result;
    }

    @Override
    public String toString () {
        return "DataResourcePlus{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", dbType='" + dbType + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", additional='" + additional + '\'' +
                ", lastExtractedTime='" + lastExtractedTime + '\'' +
                ", count=" + count +
                ", tag='" + tag + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
