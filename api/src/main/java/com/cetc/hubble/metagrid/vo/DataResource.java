package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/2/20.
 */
public class DataResource {
    private String ip;
    private String port;
    private String dbType;
    private String dbName;
    private String tableName;
    private String additional;

    public DataResource () {
    }

    public DataResource (String ip, String port, String dbType, String dbName, String tableName, String additional) {
        this.ip = ip;
        this.port = port;
        this.dbType = dbType;
        this.dbName = dbName;
        this.tableName = tableName;
        this.additional = additional;
    }

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

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataResource that = (DataResource) o;

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
        return "DataResource{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", dbType='" + dbType + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", additional='" + additional + '\'' +
                '}';
    }
}
